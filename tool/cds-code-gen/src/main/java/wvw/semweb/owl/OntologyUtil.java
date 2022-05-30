package wvw.semweb.owl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.vocabulary.OWL;
import org.apache.jen3.vocabulary.RDF;
import org.apache.jen3.vocabulary.RDFS;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import wvw.semweb.codegen.model.adt.ModelElement;
import wvw.semweb.codegen.model.adt.ModelProperty;

public class OntologyUtil {

	protected static final Logger log = LogManager.getLogger(OntologyUtil.class);

	public static List<Resource> findDomainRangeTypes(List<String> inPrp, List<String> outPrp, boolean filterSuper,
			N3Model ontology) {

//		log.info("finding domain for:\nin: " + inPrp + "\nout: " + outPrp);

		List<Resource> allDomains = getTypes(outPrp, RDFS.domain, ontology);
		List<Resource> allRanges = getTypes(inPrp, RDFS.range, ontology);

		List<Resource> allTypes = new ArrayList<>();
		allTypes.addAll(allDomains);
		allTypes.addAll(allRanges);

		allTypes = allTypes.stream().filter(t -> !t.isAnon()).collect(Collectors.toList());

		if (allTypes.size() > 1)
//			log.info("allTypes: " + allTypes);

			// (also takes care of duplicates)

			if (filterSuper)
				filterSuperClasses(allTypes);
			else
				// (e.g., prefer "diagnosis" over "diabetes diagnosis")
				filterSubClasses(allTypes);

//		log.info("> class:\n" + cls);
//		log.info("> domains:\n" + allDomains);
//		log.info("> ranges:\n" + allRanges);
//		log.info("> selected types:\n" + allTypes);
//		log.info("\n");

		return allTypes;
	}

	// TODO currently not supporting property restrictions as types
	// (need a type to be a URI)

	public static List<Resource> findSuperTypes(String resource, N3Model ontology) {
		List<Resource> superTypes = ontology.createResource(resource).listProperties(RDFS.subClassOf).toList().stream()
				.map(stmt -> stmt.getObject()).collect(Collectors.toList());

		superTypes = superTypes.stream().filter(t -> !t.isAnon()).collect(Collectors.toList());

		filterSubClasses(superTypes);

		return superTypes;
	}

	public static List<Resource> getSubTypes(Resource resource, N3Model ontology) {
		List<Resource> subTypes = ontology.listStatements(null, RDFS.subClassOf, resource).toList().stream()
				.map(stmt -> stmt.getSubject()).collect(Collectors.toList());

		subTypes = subTypes.stream().filter(t -> !t.isAnon()).collect(Collectors.toList());

		return subTypes;
	}

	// TODO currently not supporting property restrictions as types
	// (need a type to be a URI)

	private static List<Resource> getTypes(List<String> prps, Resource typePrp, N3Model ontology) {
		return prps
				.stream().flatMap(p -> ontology.listStatements(ontology.createResource(p), typePrp, (Resource) null)
						.toList().stream().map(stmt -> stmt.getObject()))
				.filter(n -> n.isURI()).collect(Collectors.toList());
	}

	protected static void filterSubClasses(List<Resource> clses) {
		filterClasses(clses, false);
	}

	protected static void filterSuperClasses(List<Resource> clses) {
		filterClasses(clses, true);
	}

	protected static void filterClasses(List<Resource> clses, boolean filterSuper) {
		l0: for (int i = 0; i < clses.size(); i++) {
			Resource cls1 = clses.get(i);

			for (int j = 0; j < clses.size(); j++) {
				if (i == j)
					continue;

				Resource cls2 = clses.get(j);
				if ((filterSuper && isMoreGeneral(cls1, cls2)) || (!filterSuper && isMoreGeneral(cls2, cls1))) {
					clses.remove(i--);

					continue l0;
				}
			}
		}
	}

	protected static boolean isMoreGeneral(Resource cls1, Resource cls2) {
		Set<Resource> found = new HashSet<>();

		LinkedList<Resource> supClses = new LinkedList<>();
		supClses.add(cls2);

		while (!supClses.isEmpty()) {
			Resource supCls = supClses.removeFirst();

			if (found.contains(supCls))
				continue;
			else
				found.add(supCls);

			if (supCls.equals(cls1))
				return true;

			supClses.addAll(supCls.listProperties(RDFS.subClassOf).toList().stream().map(stmt -> stmt.getObject())
					.collect(Collectors.toList()));
		}

		return false;
	}

	public static void loadAnnotations(String uri, ModelElement el, N3Model ontology) {
		Resource res = ontology.createResource(uri);

		if (res.hasProperty(RDFS.label))
			el.setLabel(res.getPropertyResourceValue(RDFS.label).asLiteral().getString());
	}

	public static void loadCardinality(String uri, ModelProperty prp, N3Model ontology) {
		// NOTE assume maxCardinality of 1 for label
		if (uri.equals(RDFS.label.getURI())) {
			prp.setMaxCardinality(1);
			return;
		}

		Resource prpRes = ontology.createResource(uri);

		ontology.listStatements(null, ontology.createResource(OWL.onProperty), prpRes).forEachRemaining(stmt -> {

			Resource restr = stmt.getSubject();

			if (restr.hasProperty(OWL.maxCardinality)) {
				if (prp.hasMaxCardinality())
					log.warn("found multiple maxCardinality constraints for property " + uri);

				int value = restr.getPropertyResourceValue(OWL.maxCardinality).asLiteral().getInt();
				prp.setMaxCardinality(value);
			}
		});

		if (prpRes.hasProperty(RDF.type, OWL.FunctionalProperty)) {
			if (prp.hasMaxCardinality())
				log.warn("found multiple cardinality-related constraints for property " + uri);

			prp.setMaxCardinality(1);
		}

		if (prp.hasMaxCardinality())
			log.info("found max cardinality for " + uri + ": " + prp.getMaxCardinality());
	}
}
