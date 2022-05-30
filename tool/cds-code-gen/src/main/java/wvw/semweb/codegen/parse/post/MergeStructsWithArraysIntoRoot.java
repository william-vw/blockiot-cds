package wvw.semweb.codegen.parse.post;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;
import org.apache.jen3.n3.N3Model;

import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.adt.ModelProperty;
import wvw.semweb.codegen.model.logic.Assignment;
import wvw.semweb.codegen.model.logic.Block;
import wvw.semweb.codegen.model.logic.Comparison;
import wvw.semweb.codegen.model.logic.Condition;
import wvw.semweb.codegen.model.logic.Conjunction;
import wvw.semweb.codegen.model.logic.IfThen;
import wvw.semweb.codegen.model.logic.MultiCondition;
import wvw.semweb.codegen.model.logic.NodePath;
import wvw.semweb.codegen.model.logic.Operand.Operands;
import wvw.semweb.codegen.model.adt.ModelADT;
import wvw.semweb.codegen.parse.rule.GraphNode;
import wvw.semweb.codegen.parse.rule.RuleGraph;

public class MergeStructsWithArraysIntoRoot extends ModelPostprocessor {

	protected CodeModel model;
	protected IfThen it;

	@Override
	public void postprocess(CodeModel model, IfThen it, RuleGraph ruleGraph, N3Model ontology) {
		this.model = model;
		this.it = it;

		for (GraphNode root : ruleGraph.getGraphRoots()) {
			ModelADT adt = model.getStruct((Node) root.getId());

			consumeStructsRecursively(adt, new ArrayList<>(), new ArrayList<>(), mergePred,
					new MergeConsumer(adt, it));
		}
	}

	private void consumeStructsRecursively(ModelADT curStruct, List<ModelProperty> prpPath,
			List<ModelADT> adtPath, Predicate<ModelADT> filter,
			BiConsumer<List<ModelProperty>, List<ModelADT>> collect) {

		// not dealing with root
		if (!prpPath.isEmpty()) {

			// does the adt have any array-like properties?
			if (filter.test(curStruct)) {
				// if so, let's merge them
				collect.accept(prpPath, adtPath);

				prpPath.clear();
				adtPath.clear();
			}
		}

		// avoid concurrent modification error
		List<ModelProperty> curPrps = new ArrayList<>(curStruct.getProperties());

		curPrps.forEach(newPrp -> {
			if (newPrp.hasTarget() && newPrp.getTarget().hasObjectType()) {
				ModelADT newStruct = newPrp.getTarget().getObjectType();

				List<ModelADT> newStructPath = new ArrayList<>(adtPath);
				newStructPath.add(newStruct);

				List<ModelProperty> newPrpPath = new ArrayList<>(prpPath);
				newPrpPath.add(newPrp);

				consumeStructsRecursively(newStruct, newPrpPath, newStructPath, filter, collect);
			}
		});
	}

	private Predicate<ModelADT> mergePred = new Predicate<ModelADT>() {

		@Override
		public boolean test(ModelADT t) {
			return t.getProperties().stream().anyMatch(p -> p.requiresArray());
		}
	};

	private class MergeConsumer implements BiConsumer<List<ModelProperty>, List<ModelADT>> {

		private ModelADT root;
		private IfThen it;

		public MergeConsumer(ModelADT root, IfThen it) {
			this.root = root;
			this.it = it;
		}

		@Override
		public void accept(List<ModelProperty> prpPath, List<ModelADT> adtPath) {
			log.info("merging into " + root.getString() + ": "
					+ adtPath.stream().map(s -> s.getString()).collect(Collectors.joining(", ")) + " (removing: "
					+ prpPath.stream().map(s -> s.getString()).collect(Collectors.joining(", ")) + ")");

			adtPath.forEach(s -> {
				root.replacing(s);
				model.removeStruct(s);
			});

			// remove original properties
			root.getProperties().removeAll(prpPath);

			// update the if-then blocks as well

			updateComparisons((Conjunction) it.getCondition(), prpPath);
			updateAssignments((Block) it.getThen(), prpPath);
		}

		private void updateComparisons(MultiCondition list, List<ModelProperty> prpPath) {
			Iterator<Condition> condIt = list.getConditions().iterator();
			while (condIt.hasNext()) {
				
				Condition cond = condIt.next();
				switch (cond.getConditionType()) {

				case CONJ:
				case DISJ:
					updateComparisons((MultiCondition) cond, prpPath);
					break;

				case CMP:
					Comparison cmp = (Comparison) cond;

					if (cmp.getOp1().getType() == Operands.NODE_PATH) {
						NodePath p = (NodePath) cmp.getOp1();

						p.getPath().removeAll(prpPath);
						if (p.getPath().isEmpty())
							condIt.remove();
					}

					break;
				}
			}
		}

		private void updateAssignments(Block b, List<ModelProperty> prpPath) {
			b.getStatements().forEach(s -> {
				switch (s.getStatementType()) {

				case BLOCK:
					updateAssignments((Block) s, prpPath);
					break;

				case ASSIGN:
					Assignment assn = (Assignment) s;

					if (assn.getOp1().getType() == Operands.NODE_PATH) {
						NodePath p = (NodePath) assn.getOp1();
						p.getPath().removeAll(prpPath);
					}
					break;

				default:
					break;
				}
			});
		}
	}
}
