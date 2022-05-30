package wvw.semweb.codegen.visit;

import org.apache.jen3.n3.N3Model;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.logic.Block;
import wvw.semweb.codegen.model.logic.Conjunction;
import wvw.semweb.codegen.parse.rule.RuleGraph;

public abstract class ModelVisitor {

	protected static final Logger log = LogManager.getLogger(ModelVisitorImpl.class);

	protected N3Model ontology;

	protected CodeModel model = new CodeModel();
	protected Conjunction cond = new Conjunction();
	protected Block block = new Block();

	public ModelVisitor(N3Model ontology) {
		this.ontology = ontology;
	}

	public CodeModel getModel() {
		return model;
	}

	public Conjunction getCondition() {
		return cond;
	}

	public Block getBlock() {
		return block;
	}

	public abstract void visit(RuleGraph ruleGraph) throws VisitModelException;
}
