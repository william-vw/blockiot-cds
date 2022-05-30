package wvw.semweb.codegen.parse.post;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jen3.n3.N3Model;

import wvw.semweb.codegen.gen.Util;
import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.logic.Assignment;
import wvw.semweb.codegen.model.logic.Block;
import wvw.semweb.codegen.model.logic.Comparison;
import wvw.semweb.codegen.model.logic.CreateStruct;
import wvw.semweb.codegen.model.logic.IfThen;
import wvw.semweb.codegen.model.logic.NodePath;
import wvw.semweb.codegen.model.logic.Variable;
import wvw.semweb.codegen.model.logic.CodeStatement.Codes;
import wvw.semweb.codegen.model.logic.Comparison.Comparators;
import wvw.semweb.codegen.parse.rule.RuleGraph;

public class InsertStructExistsCheck extends ModelPostprocessor {

	// for create-adt assignment blocks:
	// modify the code block to check whether something already exists at end of
	// path (visitor code will simply assign a new adt each time)

	// (difficult to do this in visitor since we should only do this for
	// intermediary assignments (i.e., not for the last assignment))

	@Override
	public void postprocess(CodeModel model, IfThen it, RuleGraph ruleGraph, N3Model ontology) {
		// TODO currently only blocks will be created for create-adt assignment
		// blocks; should somehow tag these blocks for extensibility ..

		Block block = (Block) it.getThen();
		List<Block> subBlocks = block.getStatements().stream().filter(stmt -> stmt.getStatementType() == Codes.BLOCK)
				.map(stmt -> (Block) stmt).collect(Collectors.toList());

		for (int i = 0; i < subBlocks.size() - 1; i++) {
			Block subBlock = subBlocks.get(i);

			Assignment asn = (Assignment) subBlock.getStatements().get(0);
			Assignment asn2 = (Assignment) subBlock.getStatements().get(1);

			Variable var = (Variable) asn.getOp1();
			CreateStruct createStruct = (CreateStruct) asn.getOp2();
			NodePath path = (NodePath) asn2.getOp1();

			if (Util.involvesArrayAssign(path)) {

			} else {
				subBlock.clear();

				// check whether adt at current path exists
				// if not, create new adt and assign to path

				Comparison notExists = new Comparison(path, Comparators.NEX);
				Assignment newAsn = new Assignment(path, createStruct);

				subBlock.add(new IfThen(notExists, newAsn));

				// assign (possibly new) adt at end of path to variable

				Assignment newAsn2 = new Assignment(var, path);
				subBlock.add(newAsn2);
			}
		}
	}
}
