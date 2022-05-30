package wvw.semweb.codegen.parse.post;

import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.Resource;

import wvw.semweb.codegen.gen.Util;
import wvw.semweb.codegen.model.adt.CodeModel;
import wvw.semweb.codegen.model.adt.ModelElement;
import wvw.semweb.codegen.model.logic.IfThen;
import wvw.semweb.codegen.parse.rule.RuleGraph;
import wvw.semweb.owl.OntologyUtil;

public class InsertAllOntologyConstants extends ModelPostprocessor {

	@Override
	public void postprocess(CodeModel model, IfThen it, RuleGraph ruleGraph, N3Model ontology) {
		model.getAllStructs().forEach(adt -> {
			if (adt.hasConstants()) {
				Resource nodeType = (Resource) adt.getId();

				OntologyUtil.getSubTypes(nodeType, ontology).forEach(subType -> {
					ModelElement cnst = new ModelElement(Util.localName(subType.getLocalName()));
					OntologyUtil.loadAnnotations(subType.getURI(), cnst, ontology);

					adt.addType(cnst);
				});
			}
		});
	}
}
