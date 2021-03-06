/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jen3.graph;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.function.Function;

import org.apache.jen3.datatypes.RDFDatatype;
import org.apache.jen3.graph.impl.LiteralLabel;
import org.apache.jen3.rdf.model.impl.ModelCom;
import org.apache.jen3.reasoner.rulesys.Node_RuleVariable;
import org.apache.jen3.shared.JenaException;
import org.apache.jen3.shared.PrefixMapping;
import org.apache.jen3.sys.Serializer;

/**
 * A Node has five subtypes: Node_Blank, Node_Anon, Node_URI, Node_Variable, and
 * Node_ANY. Nodes are only constructed by the node factory methods, and they
 * will attempt to re-use existing nodes with the same label if they are recent
 * enough.
 */

public abstract class Node implements Serializable {

	final protected Object label;
	static final int THRESHOLD = 10000;

	protected boolean doMatchAbsolute;

	/**
	 * The canonical instance of Node_ANY. No other instances are required.
	 */
	public static final Node ANY = new Node_ANY();

	static final String RDFprefix = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

	/**
	 * Visit a Node and dispatch on it to the appropriate method from the
	 * NodeVisitor <code>v</code>.
	 * 
	 * @param v the visitor to apply to the node
	 * @return the value returned by the applied method
	 */
	public abstract Node visitWith(NodeVisitor v, Object data);

	/**
	 * @return type of node.
	 */

	public abstract NodeTypes getType();

	/**
	 * Answer true iff this node is concrete, ie not variable, ie URI, blank, or
	 * literal.
	 */
	public abstract boolean isConcrete();

	/**
	 * Used when searching the triple store given this resource as a needle. Ideally
	 * this would be abstracted away or given as a "find" method attribute. But,
	 * this required changing way too many Jena method calls.
	 * 
	 * @return When searching, whether absolute matches are needed. This is only
	 *         relevant for variables, i.e., whether they match any other node, or
	 *         whether variable names must match exactly (e.g., used when printing
	 *         N3)
	 */

	public boolean doMatchAbsolute() {
		return doMatchAbsolute;
	}

	public void setMatchAbsolute(boolean matchAbsolute) {
		this.doMatchAbsolute = matchAbsolute;
	}

	/**
	 * Answer true iff this node is a literal node [subclasses override]
	 */
	public boolean isLiteral() {
		return false;
	}

	/**
	 * Answer true iff this node is a blank node [subclasses override]
	 */
	public boolean isBlank() {
		return false;
	}

	/**
	 * Answer true iff this node is a URI node [subclasses override]
	 */
	public boolean isURI() {
		return false;
	}

	/**
	 * Answer true iff this node is a URI node [subclasses override]
	 */
	public boolean isResource() {
		return false;
	}

	/**
	 * Answer true iff this node is a variable node [subclasses override]
	 */
	public boolean isVariable() {
		return false;
	}

	/**
	 * Answer true iff this node is an N3 formula [subclasses override]
	 */
	public boolean isCitedFormula() {
		return false;
	}

	/**
	 * Answer true iff this node is an N3 collection [subclasses override]
	 */
	public boolean isCollection() {
		return false;
	}

	/**
	 * Answer true iff this node is an N3 quantified variable [subclasses override]
	 */
	public boolean isQuantifiedVariable() {
		return false;
	}

	/**
	 * Answer true iff this node is an N3 quantified variable [subclasses override]
	 */
	public boolean isRuleVariable() {
		return false;
	}

	/**
	 * Answer true iff this node is an N3 quick variable [subclasses override]
	 */

	public boolean isQuickVariable() {
		return false;
	}

	/**
	 * Answer true iff this node is an N3 quantifier [subclasses override]
	 */
	public boolean isQuantifier() {
		return false;
	}

	/** get the blank node id if the node is blank, otherwise die horribly */
	public BlankNodeId getBlankNodeId() {
		throw new UnsupportedOperationException(this + " is not a blank node");
	}

	/**
	 * Answer the label of this blank node or throw an UnsupportedOperationException
	 * if it's not blank.
	 */
	public String getBlankNodeLabel() {
		return getBlankNodeId().getLabelString();
	}

	/**
	 * Answer the literal value of a literal node, or throw an
	 * UnsupportedOperationException if it's not a literal node
	 */
	public LiteralLabel getLiteral() {
		throw new UnsupportedOperationException(this + " is not a literal node");
	}

	/**
	 * Answer the value of this node's literal value, if it is a literal; otherwise
	 * die horribly.
	 */
	public Object getLiteralValue() {
		throw new NotLiteral(this);
	}

	/**
	 * Answer the lexical form of this node's literal value, if it is a literal;
	 * otherwise die horribly.
	 */
	public String getLiteralLexicalForm() {
		throw new NotLiteral(this);
	}

	/**
	 * Answer the language of this node's literal value, if it is a literal;
	 * otherwise die horribly.
	 */
	public String getLiteralLanguage() {
		throw new NotLiteral(this);
	}

	/**
	 * Answer the data-type URI of this node's literal value, if it is a literal;
	 * otherwise die horribly.
	 */
	public String getLiteralDatatypeURI() {
		throw new NotLiteral(this);
	}

	/**
	 * Answer the RDF datatype object of this node's literal value, if it is a
	 * literal; otherwise die horribly.
	 */
	public RDFDatatype getLiteralDatatype() {
		throw new NotLiteral(this);
	}

	public boolean getLiteralIsXML() {
		throw new NotLiteral(this);
	}

	/**
	 * Exception thrown if a literal-access operation is attemted on a non-literal
	 * node.
	 */
	public static class NotLiteral extends JenaException {
		public NotLiteral(Node it) {
			super(it + " is not a literal node");
		}
	}

	/**
	 * Answer the object which is the index value for this Node. The default is this
	 * Node itself; overridden in Node_Literal for literal indexing purposes. Only
	 * concrete nodes should use this method.
	 */
	public Object getIndexingValue() {
		return this;
	}

	/** get the URI of this node if it has one, else die horribly */
	public String getURI() {
		throw new UnsupportedOperationException(this + " is not a URI node");
	}

	/** get the namespace part of this node if it's a URI node, else die horribly */
	public String getNameSpace() {
		throw new UnsupportedOperationException(this + " is not a URI node");
	}

	/** get the localname part of this node if it's a URI node, else die horribly */
	public String getLocalName() {
		throw new UnsupportedOperationException(this + " is not a URI node");
	}

	/** get a variable nodes name, otherwise die horribly */
	public String getName() {
		throw new UnsupportedOperationException("this (" + this.getClass() + ") is not a variable node");
	}

	/** answer true iff this node is a URI node with the given URI */
	public boolean hasURI(String uri) {
		return false;
	}

	// DEPRECATED
	/** an abstraction to allow code sharing */
	static abstract class NodeMaker {
		abstract Node construct(Object x);
	}

//	@Deprecated
//	static final NodeMaker makeAnon = new NodeMaker() {
//		@Override
//		Node construct(Object x) {
//			return new Node_Blank(x);
//		}
//	};

	@Deprecated
	static final NodeMaker makeLiteral = new NodeMaker() {
		@Override
		Node construct(Object x) {
			return new Node_Literal(x);
		}
	};

	@Deprecated
	static final NodeMaker makeURI = new NodeMaker() {
		@Override
		Node construct(Object x) {
			return new Node_URI(x);
		}
	};

	@Deprecated
	static final NodeMaker makeVariable = new NodeMaker() {
		@Override
		Node construct(Object x) {
			return new Node_Variable(x);
		}
	};

	/**
	 * The canonical NULL. It appears here so that revised definitions [eg as a
	 * bnode] that require the cache-and-maker system will work; the NodeMaker
	 * constants should be non-null at this point.
	 */
	@Deprecated
	public static final Node NULL = new Node_NULL();

	// non-package level visibility due to "n3" sub-package
	public Node(Object label) {
		this.label = label;
	}

	/**
	 * We object strongly to null labels: for example, they make .equals flaky.
	 * 
	 * @deprecated Use specific {@link NodeFactory} functions.
	 */
	@Deprecated
	public static Node create(NodeMaker maker, Object label) {
		if (label == null)
			throw new JenaException("Node.make: null label");
		return maker.construct(label);
	}

	// to be overwritten by subclasses
	public boolean isContainer() {
		return false;
	}

	// to be overwritten by subclasses
	public boolean includesVars() {
		return false;
	}

	// to be overwritten by subclasses
	public boolean includesRuleVars() {
		return false;
	}

	/**
	 * Nodes only equal other Nodes that have equal labels.
	 */
	@Override
	public abstract boolean equals(Object o);

	/**
	 * Test that two nodes are semantically equivalent. In some cases this may be
	 * the same as equals, in others equals is stricter. For example, two xsd:int
	 * literals with the same value but different lexical form are semantically
	 * equivalent but distinguished by the java equals function.
	 * <p>
	 * Default implementation is to use equals, subclasses should override this.
	 * </p>
	 */
	public boolean sameValueAs(Object o) {
		return equals(o);
	}

	// to be overwritten by subclasses
	public boolean isHashable() {
		return true;
	}

	@Override
	public int hashCode() {
		return label.hashCode() * 31;
	}

	/**
	 * Answer true iff this node accepts the other one as a match. The default is an
	 * equality test; it is over-ridden in subclasses to provide the appropriate
	 * semantics for literals, ANY, and variables.
	 * 
	 * @param other a node to test for matching
	 * @return true iff this node accepts the other as a match
	 */
	public boolean matches(Node other) {
		return (this == Node.ANY) || (this == Node_RuleVariable.WILD) || other.isDataVar() || equals(other);
	}

	public boolean isDataVar() {
		return false;
	}

	// ---- Serializable
	// Must be "protected", not "private".
	protected Object writeReplace() throws ObjectStreamException {
		Function<Node, Object> function = Serializer.getNodeSerializer();
		if (function == null)
			throw new IllegalStateException("Function for Node.writeReplace not set");
		return function.apply(this);
	}

	// Any attempt to serialize without replacement is an error.
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		throw new IllegalStateException();
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		throw new IllegalStateException();
	}

	/**
	 * 
	 * @return whether this node has a label (e.g., which can be looked up in a
	 *         cache). URI and BLANK are cached in the {@link ModelCom} class, but
	 *         not literals
	 * 
	 */

	public boolean doCache() {
		switch (getType()) {
		case URI:
		case BLANK:
		case QUANT_VAR:
		case QUICK_VAR:
			return true;
		default:
			return false;
		}
	}

	/**
	 * 
	 * @return a key for searching other nodes with the same name (e.g., in a
	 *         cache). equality is only based on name (it will ignore node type)
	 * 
	 */

	public Node_Named asNeedle() {
		return new Node_Named(label);
	}

	// ---- Serializable

	/**
	 * Answer a human-readable representation of this Node. It will not compress
	 * URIs, nor quote literals (because at the moment too many places use
	 * toString() for something machine-oriented).
	 */
	@Override
	public String toString() {
		return toString(null);
	}

	/**
	 * Answer a human-readable representation of this Node where literals are quoted
	 * according to <code>quoting</code> but URIs are not compressed.
	 */
	public String toString(boolean quoting) {
		return toString(null, quoting);
	}

	/**
	 * Answer a human-readable representation of the Node, quoting literals and
	 * compressing URIs.
	 */
	public String toString(PrefixMapping pm) {
		return toString(pm, true);
	}

	/**
	 * Answer a human readable representation of this Node, quoting literals if
	 * specified, and compressing URIs using the prefix mapping supplied.
	 */
	public String toString(PrefixMapping pm, boolean quoting) {
		return label.toString();
	}
}
