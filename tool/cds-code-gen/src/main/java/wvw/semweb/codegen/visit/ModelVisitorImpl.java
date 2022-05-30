package wvw.semweb.codegen.visit;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.datatypes.TypeMapper;
import org.apache.jen3.datatypes.xsd.XSDDatatype;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.Node_Literal;
import org.apache.jen3.graph.Node_URI;
import org.apache.jen3.graph.Node_Variable;
import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.n3.N3Model;
import org.apache.jen3.rdf.model.Resource;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.vocabulary.N3List;
import org.apache.jen3.vocabulary.N3Log;
import org.apache.jen3.vocabulary.N3Math;
import org.apache.jen3.vocabulary.RDF;

import wvw.semweb.codegen.gen.Util;
import wvw.semweb.codegen.model.adt.ModelElement;
import wvw.semweb.codegen.model.adt.ModelProperty;
import wvw.semweb.codegen.model.adt.ModelADT;
import wvw.semweb.codegen.model.adt.ModelType;
import wvw.semweb.codegen.model.logic.Assignment;
import wvw.semweb.codegen.model.logic.Block;
import wvw.semweb.codegen.model.logic.Comparison;
import wvw.semweb.codegen.model.logic.Condition;
import wvw.semweb.codegen.model.logic.CreateStruct;
import wvw.semweb.codegen.model.logic.Disjunction;
import wvw.semweb.codegen.model.logic.Literal;
import wvw.semweb.codegen.model.logic.NodePath;
import wvw.semweb.codegen.model.logic.StructConstant;
import wvw.semweb.codegen.model.logic.Variable;
import wvw.semweb.codegen.model.logic.Comparison.Comparators;
import wvw.semweb.codegen.parse.rule.GraphEdge;
import wvw.semweb.codegen.parse.rule.GraphNode;
import wvw.semweb.codegen.parse.rule.RuleGraph;
import wvw.semweb.codegen.parse.rule.RuleGraph.ClauseTypes;
import wvw.semweb.owl.OntologyUtil;

public class ModelVisitorImpl extends ModelVisitor {

	public ModelVisitorImpl(N3Model ontology) {
		super(ontology);
	}

	@Override
	public void visit(RuleGraph ruleGraph) throws VisitModelException {
		for (GraphNode root : ruleGraph.getGraphRoots()) {
			Variable start = new Variable(((Node_Variable) root.getId()).getName());

			constructModel(root, new HashSet<>());
			constructLogic(root, null, new NodePath(start), new HashSet<>());
		}
	}

	private ModelType constructModel(GraphNode gnode, Set<GraphNode> found) throws VisitModelException {
		if (found.contains(gnode))
			return null;

		found.add(gnode);

		Node node = (Node) gnode.getId();
		log.info("node? " + node);

		// - literal node
		if (node instanceof Node_Literal) {
			Node_Literal nl = (Node_Literal) node;

			// return literal's datatype as target for prior property
			// (assumed this is an end-point in model)
			return new ModelType(nl.getLiteralDatatype());
		}

		// - get the node's type(s) based on the domains and ranges of outgoing,
		// incoming properties from the rule graph
		List<Resource> nodeTypes = getNodeTypes(gnode);

		log.info("found ontology types: " + nodeTypes);

		if (nodeTypes.size() > 1)
			log.warn("found multiple domain/range types for node " + node + " (using first one): " + nodeTypes);

		else if (nodeTypes.size() == 0)
			throw new VisitModelException("did not find any domains/ranges or super-types for node: " + node);

		Resource nodeType = nodeTypes.get(0);

		// - literal datatype
		if (nodeType.getURI().startsWith(XSDDatatype.XSD)) {
			RDFDatatype dt = TypeMapper.getInstance().getTypeByName(nodeType.getURI());

			if (dt == null)
				throw new VisitModelException("could not find datatype for XSD range " + nodeType);

			// return datatype as target
			// (idem above; assumed this is an end-point in model)
			return new ModelType(dt);
		}

		// - object type

		ModelType ret = null;

		// add a new adt to our model
		// (or get previously created one)
		ModelADT modelStruct = model.getOrCreateStruct(Util.localName(nodeType), node);
		modelStruct.setId(nodeType);
		OntologyUtil.loadAnnotations(nodeType.getURI(), modelStruct, ontology);

		// return adt as target
		ret = new ModelType(modelStruct);

		// -- URI node
		if (node instanceof Node_URI)
			// add as constant to our adt
			addConstant((Node_URI) node, true, modelStruct);

		for (GraphEdge edge : gnode.getOut()) {
			log.info("edge? " + node + " -> " + edge.getId());

			Node_URI nodePrp = (Node_URI) edge.getId();
			GraphNode target = edge.getTarget();

			// -- outgoing "type" edge
			// if a URI, this node is an explicit type in the rule

			if (edge.getId().equals(RDF.type.asNode())) {
				if (target.getId() instanceof Node_URI) {
					Node_URI typeUri = (Node_URI) target.getId();

					// e.g., Patient adt with 'patient' type doesn't make a lot of sense
					if (!Util.localName(typeUri).equals(modelStruct.getName()))

						// add as constant to our adt
						addConstant(typeUri, false, modelStruct);
				}

				// - not a regular property (builtin) but builtin
				// (as w/ literals; assumed this is an end-point in model;
				// builtins on literal datatypes are already ignored)
			} else if (toUriComparator(edge) != null) {
				// TODO likely no longer needed if we add all values from ontology

				// if the target is concrete, add those as values in the adt

				// -- individual URI
				if (target.getId() instanceof Node_URI) {
					Node_URI targetUri = (Node_URI) target.getId();

					addConstant(targetUri, true, modelStruct);
					model.setStruct(targetUri, modelStruct);

					// -- collection of URIs
				} else if (target.getId() instanceof Node_Collection) {
					Node_Collection coll = (Node_Collection) target.getId();
					coll.getElements().forEach(el -> {
						if (el instanceof Node_URI) {
							addConstant((Node_URI) el, true, modelStruct);
							model.setStruct(el, modelStruct);
						}
					});

				}
			} else {
				// -- regular property
				ModelProperty modelPrp = new ModelProperty(Util.localName(nodePrp));
				OntologyUtil.loadAnnotations(nodePrp.getURI(), modelPrp, ontology);
				OntologyUtil.loadCardinality(nodePrp.getURI(), modelPrp, ontology);

				// then, recursively call this method on the edge target

				ModelType prpType = constructModel(edge.getTarget(), found);
				// returned model-type will serve as type for our property
				if (prpType != null) {
					modelPrp.setTarget(prpType);
					modelStruct.addProperty(modelPrp);
				}
			}
		}

		return ret;
	}

	private void constructLogic(GraphNode gnode, GraphEdge from, NodePath path, Set<GraphNode> found)
			throws VisitModelException {

		if (found.contains(gnode))
			return;

		found.add(gnode);

		ClauseTypes clauseType = (from != null ? (ClauseTypes) from.getData() : ClauseTypes.BODY);

		Node node = (Node) gnode.getId();

		// - literal node
		if (node instanceof Node_Literal) {
			Node_Literal nl = (Node_Literal) node;

			Literal lit = new Literal(nl.getLiteralValue());
			literalNode(gnode, from, path, clauseType, lit);
		}

		ModelADT modelStruct = model.getStruct(node);

		// - object type
		// (else: literal datatype)

		// -- URI node
		if (node instanceof Node_URI) {
			if (modelStruct != null) {
				Node_URI valueUri = (Node_URI) node;
				ModelElement value = modelStruct.getConstant(Util.localName(valueUri));

				constantNode(gnode, from, path, clauseType, modelStruct, value);
			}

			// -- collection node
		} else if (node instanceof Node_Collection) {
			Node_Collection coll = (Node_Collection) node;

			if (clauseType != ClauseTypes.BODY)
				throw new VisitModelException("currently, collections are only supported in rule body");

			constantDisjNode(coll, from, path);

			// -- variable node
		} else {
			if (modelStruct != null) {

				switch (clauseType) {

				case BODY:
					// (only needed in case of object datatype)
					if (!path.getPath().isEmpty() && !path.getPath().getLast().requiresArray())
						newPath(path);

					break;

				case HEAD:
					// if the (original) node is a blank node
					// (only needed in case of object datatype)

					if (node.isRuleVariable() && ((Node_RuleVariable) node).getOriginal().isBlank()) {
						// deal with existentials in rule head (constructors)
						// (will likely involve updating the path)

						path = newStruct(path, modelStruct);
					}

					break;
				}
			}
		}

		nodePropertiesStart();

		// make sure type properties come first
		// (needed when dealing w/ array-like properties;
		// (existence check should come before other conditions)

		List<GraphEdge> sortedEdges = new ArrayList<>(gnode.getOut());
		sortedEdges.sort((e1, e2) -> {
			Node n1 = (Node) e1.getId();
			if (n1.getURI().equals(RDF.type.getURI()))
				return -1;
			else
				return 1;
		});

		for (GraphEdge edge : sortedEdges) {
//			log.info("edge? " + node.getId() + " -> " + edge.getId() + " -> " + edge.getTarget().getId());

			ClauseTypes clauseType2 = (ClauseTypes) edge.getData();
			GraphNode target = edge.getTarget();

			// - outgoing "type" edge
			// if a URI, this node is an explicit type in the rule
			if (edge.getId().equals(RDF.type.asNode()) && target.getId() instanceof Node_URI) {
				Node_URI typeUri = (Node_URI) target.getId();

				// e.g., Patient adt with 'patient' type doesn't make a lot of sense
				if (!Util.localName(typeUri).equals(modelStruct.getName())) {
					ModelElement type = modelStruct.getConstant(Util.localName(typeUri));

					constantNode(target, edge, path, clauseType2, modelStruct, type);
				}

			} else {
				NodePath path2 = path;

				// - object type & regular property (i.e., not a builtin):
				// add to our path

				if (modelStruct != null && toUriComparator(edge) == null) {
					Node_URI nodePrp = (Node_URI) edge.getId();

					// (needs to be copied; key-type can be overridden)
					ModelProperty modelPrp = modelStruct.getProperty(Util.localName(nodePrp)).copy();

					// copy our current path and add this property to it
					path2 = path.copy();
					path2.add(modelPrp);
				}

				// recursively call this method on the edge target
				constructLogic(edge.getTarget(), edge, path2, found);
			}
		}

		nodePropertiesEnd();
	}

	private void addConstant(Node_URI uri, boolean isValue, ModelADT modelStruct) {
		ModelElement cnst = new ModelElement(Util.localName(uri));
		OntologyUtil.loadAnnotations(uri.getURI(), cnst, ontology);

		// add as constant to our adt
		if (isValue) {
			modelStruct.addValue(cnst);
		} else
			modelStruct.addType(cnst);
	}

	private List<Resource> getNodeTypes(GraphNode node) {
		List<String> in = new ArrayList<>();
		List<String> out = new ArrayList<>();

		// add incoming edges to appropriate list (may be inverse)
		node.getIn().stream().forEach(e -> {
			String id = e.getId().toString();
			if (!e.isInverse())
				in.add(id);
			else
				out.add(id);
		});

		// idem for outgoing edges
		node.getOut().stream().forEach(e -> {
			String id = e.getId().toString();
			if (!e.isInverse())
				out.add(id);
			else
				in.add(id);
		});

		List<Resource> types = OntologyUtil.findDomainRangeTypes(in, out, true, ontology);
		if (types.isEmpty()) {
			// get first specified type in rule
			Optional<GraphEdge> found = node.getOut().stream().filter(edge -> edge.getId().equals(RDF.type.asNode()))
					.findFirst();

			if (found.isPresent()) {
				String res = found.get().getTarget().getId().toString();
				types = OntologyUtil.findSuperTypes(res, ontology);
			}
		}

		return types;
	}

	// keep track of current adt constructors
	// (any property will be added as constructor parameter)

	private LinkedList<CreateStruct> newStructs = new LinkedList<>();

	private void nodePropertiesStart() {
	}

	private void nodePropertiesEnd() {
		// only consider direct properties as constructor parameters
		// (see #adtNode())
		if (!newStructs.isEmpty()) {
			newStructs.removeLast();
		}
	}

	// - hooks for updating the logic - i.e., cond and block

	private void newPath(NodePath path) {
		Comparison con = new Comparison(path, Comparators.EX);
		cond.add(con);
	}

	// support existential rules
	// if the node represents a blank node, then instantiate a new adt

	private NodePath newStruct(NodePath path, ModelADT modelStruct) {
		// create new adt
		CreateStruct newStruct = new CreateStruct(modelStruct);

		// use as constructor parameter
		// (this is a bit more involved, so don't use new[..] methods)

		if (!newStructs.isEmpty()) {
			CreateStruct curStruct = newStructs.getLast();
			curStruct.add(new Assignment(path, newStruct));

			// properties of blank node will serve as constructor parameters
			// add this adt to the stack; use new assignments as parameters

			// (node path doesn't matter here)
			newStructs.add(newStruct);

			// following adt parameters will have "empty" path
			// (params are identified by predicates)
			return new NodePath();

			// treat as regular statement
		} else {
			Block subBlock = new Block();
			block.add(subBlock);

			Variable v = new Variable();

			// assign to var
			Assignment asn = new Assignment(v, newStruct);
			subBlock.add(asn);

			// assign end of path to newly created adt
			Assignment asn2 = new Assignment(path, v);
			subBlock.add(asn2);

			// properties of blank node will serve as constructor parameters
			// add this adt to the stack; use new assignments as parameters
			// (see first option above)

			// (once constructor is done, new node path starts from variable)
			newStructs.add(newStruct);

			// but, following adt parameters, will have "empty" path
			// (params are identified by predicates)
			return new NodePath();
		}
	}

	// TODO three methods below have a lot in common

	private void literalNode(GraphNode node, GraphEdge from, NodePath path, ClauseTypes clauseType, Literal lit)
			throws VisitModelException {

		// check whether incoming property constitutes a builtin (i.e., comparison)
		Comparators cmp = toLiteralComparator(from, lit.getValue());

		switch (clauseType) {

		case BODY:
			Comparison con = null;
			if (cmp != null)
				// create comparison
				con = new Comparison(path, lit, cmp);
			else
				// if not, then we're checking equality with this literal
				con = new Comparison(path, lit, Comparators.EQ);

			newCondition(con);

			break;

		case HEAD:
			if (cmp != null)
				throw new VisitModelException("not expecting builtin in rule head: " + from.getId());
			else {
				// create assignment with literal
				Assignment assign = new Assignment(path, lit);
				newAssignment(assign);
			}

			break;
		}

		if (!node.getOut().isEmpty())
			throw new VisitModelException("currently assuming that literal nodes are endpoints: " + lit);
	}

	private void constantNode(GraphNode node, GraphEdge from, NodePath path, ClauseTypes clauseType,
			ModelADT modelStruct, ModelElement value) throws VisitModelException {

		// check whether incoming property constitutes a builtin (i.e., comparison)
		Comparators cmp = toUriComparator(from);

		// assumed that this value is a constant of the model-adt
		StructConstant cnst = new StructConstant(modelStruct, value);

		// type required to index an array-like property
		// (in subsequent copies of this path; key will be used to index the array)

		if (path.size() > 0 && path.getLast().requiresArray()) {
			if (cmp != null)
				throw new VisitModelException("currently not supporting builtins on array-like properties: "
						+ path.getLast().getString() + " (" + from.getId() + ")");

			path.getLast().setKeyType(cnst);

			// also, in this case, no need for comparison w/ uri:
			// only need to check for existance of mapping
			// (this is an endpoint)

			Comparison con = new Comparison(path, Comparators.EX);
			newCondition(con);

		} else {
			// comparing constants:
			// check "type" of end of current path with given constant

			NodePath path2 = path.copy();
			path2.add(ModelProperty.typeProperty());

			switch (clauseType) {

			case BODY:
				Comparison con = null;
				if (cmp != null)
					// create comparison
					con = new Comparison(path2, cnst, cmp);
				else
					// if not, then we're checking equality with this uri
					con = new Comparison(path2, cnst, Comparators.EQ);

				newCondition(con);

				break;

			case HEAD:
				if (cmp != null)
					throw new VisitModelException("not expecting builtin in rule head: " + from.getId());

				else {
					Assignment asn = new Assignment(path2, cnst);
					newAssignment(asn);
				}

				break;
			}
		}

		if (!node.getOut().isEmpty())
			throw new VisitModelException("currently assuming that URI nodes are endpoints: " + value);
	}

	private void constantDisjNode(Node_Collection coll, GraphEdge from, NodePath path) throws VisitModelException {
		Comparators cmp = toUriComparator(from);

		// only supporting this specific case

		// compare each element of collection as constant:
		// i.e. check "type" of end of current path with given constant

		if (cmp != Comparators.ONE_OF)
			throw new VisitModelException("expecting list:in builtin for collection, found: " + from);

		Disjunction disj = new Disjunction();
		newCondition(disj);

		NodePath path2 = path.copy();
		path2.add(ModelProperty.typeProperty());

		for (Node el : coll.getElements()) {
			Node_URI elUri = (Node_URI) el;
			ModelADT elStruct = model.getStruct(el);

			ModelElement value = elStruct.getConstant(Util.localName(elUri));
			StructConstant cnst = new StructConstant(elStruct, value);

			disj.add(new Comparison(path2, cnst, Comparators.EQ));
		}
	}

	private void newAssignment(Assignment assn) {
		if (!newStructs.isEmpty()) {
			CreateStruct curStruct = newStructs.getLast();
			curStruct.add(assn);

		} else {
			block.add(assn);
		}
	}

	private void newCondition(Condition cond) {
		this.cond.add(cond);
	}

	private Comparators toLiteralComparator(GraphEdge edge, Object literal) {
		Node_URI node = (Node_URI) edge.getId();

		if (literal instanceof Integer || literal instanceof Double) {
			if (node.equals(N3Math.lessThan.asNode()))
				return Comparators.LT;
			if (node.equals(N3Math.lessThanOrEqual.asNode()))
				return Comparators.LE;
			if (node.equals(N3Math.lessThanOrEqual.asNode()))
				return Comparators.LE;
			if (node.equals(N3Math.greaterThan.asNode()))
				return Comparators.GT;
			if (node.equals(N3Math.greaterThanOrEqual.asNode()))
				return Comparators.GE;
			if (node.equals(N3Math.notGreaterThan.asNode()))
				return Comparators.NGT;
			if (node.equals(N3Math.notLessThan.asNode()))
				return Comparators.NLT;
			if (node.equals(N3Math.notEqualTo.asNode()))
				return Comparators.NEQ;

			if (node.equals(N3List.in.asNode()))
				return Comparators.ONE_OF;
		}

		return null;
	}

	private Comparators toUriComparator(GraphEdge edge) {
		Node_URI node = (Node_URI) edge.getId();

		if (node.equals(N3Log.equalTo.asNode()))
			return Comparators.EQ;
		if (node.equals(N3Log.notEqualTo.asNode()))
			return Comparators.NEQ;

		if (node.equals(N3List.in.asNode()))
			return Comparators.ONE_OF;
		else
			return null;
	}
}
