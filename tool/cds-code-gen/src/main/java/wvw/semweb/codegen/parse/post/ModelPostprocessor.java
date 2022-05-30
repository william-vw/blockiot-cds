package wvw.semweb.codegen.parse.post;

import org.apache.jen3.n3.N3Model;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.logic.IfThen;
import wvw.semweb.codegen.parse.rule.RuleGraph;

public abstract class ModelPostprocessor {

	protected static final Logger log = LogManager.getLogger(ModelPostprocessor.class);

	public enum PostprocessTypes {
		// @formatter:off
		INSERT_STRUCT_EXISTS_CHECK, 
		REMOVE_EXISTS_CHECK_LITERALS(true),
		MERGE_STRUCTS_W_ARRAYS_IN_ROOT,
		INSERT_ALL_ONTO_CONSTANTS(true);
		// @formatter:on

		private boolean def = false;

		private PostprocessTypes() {
		}

		private PostprocessTypes(boolean def) {
			this.def = def;
		}

		public boolean isDefault() {
			return def;
		}
	}

	public static ModelPostprocessor create(PostprocessTypes type) {
		switch (type) {

		case INSERT_STRUCT_EXISTS_CHECK:
			return new InsertStructExistsCheck();

		case REMOVE_EXISTS_CHECK_LITERALS:
			return new RemoveExistChecksForLiterals();

		case MERGE_STRUCTS_W_ARRAYS_IN_ROOT:
			return new MergeStructsWithArraysIntoRoot();

		case INSERT_ALL_ONTO_CONSTANTS:
			return new InsertAllOntologyConstants();
		}

		return null;
	}

	public abstract void postprocess(CodeModel model, IfThen it, RuleGraph ruleGraph, N3Model ontology);
}
