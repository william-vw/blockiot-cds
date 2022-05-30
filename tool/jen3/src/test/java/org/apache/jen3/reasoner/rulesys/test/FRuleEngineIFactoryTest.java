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

package org.apache.jen3.reasoner.rulesys.test;

import java.util.Iterator;

import org.apache.jen3.graph.Capabilities;
import org.apache.jen3.graph.Graph;
import org.apache.jen3.graph.GraphEventManager;
import org.apache.jen3.graph.GraphStatisticsHandler;
import org.apache.jen3.graph.Node;
import org.apache.jen3.graph.TransactionHandler;
import org.apache.jen3.graph.Triple;
import org.apache.jen3.graph.n3.scope.Scope;
import org.apache.jen3.n3.impl.N3ModelImpl.N3GraphConfig;
import org.apache.jen3.reasoner.Derivation;
import org.apache.jen3.reasoner.Reasoner;
import org.apache.jen3.reasoner.ValidityReport;
import org.apache.jen3.reasoner.rulesys.ForwardRuleInfGraphI;
import org.apache.jen3.reasoner.rulesys.Rule;
import org.apache.jen3.reasoner.rulesys.impl.FRuleEngine;
import org.apache.jen3.reasoner.rulesys.impl.FRuleEngineI;
import org.apache.jen3.reasoner.rulesys.impl.FRuleEngineIFactory;
import org.apache.jen3.reasoner.rulesys.impl.RETEEngine;
import org.apache.jen3.shared.AddDeniedException;
import org.apache.jen3.shared.DeleteDeniedException;
import org.apache.jen3.shared.PrefixMapping;
import org.apache.jen3.util.iterator.ExtendedIterator;

import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FRuleEngineIFactoryTest extends TestCase {

	/**
	 * Boilerplate for junit. This is its own test suite
	 */
	public static TestSuite suite() {
		return new TestSuite(FRuleEngineIFactoryTest.class);
	}

	@Override
	public void tearDown() {
		FRuleEngineIFactory.setInstance(new FRuleEngineIFactory());
	}

	public void testItShouldBeASingleton() {
		FRuleEngineIFactory instance = FRuleEngineIFactory.getInstance();

		assertNotNull("A default instance must be created", instance);

		assertSame("The same instance should have be returned", instance, FRuleEngineIFactory.getInstance());
	}

	public void testItShouldLetYouReplaceTheSingletonInstance() {
		MyFRuleEngineIFactory anotherFactory = new MyFRuleEngineIFactory();
		FRuleEngineIFactory.setInstance(anotherFactory);

		assertSame("The instance should have been replaced", anotherFactory, FRuleEngineIFactory.getInstance());
	}

	public void testItShouldInstantiateAFRuleEngineIfUseRETEisFalse() {
		ForwardRuleInfGraphI infGraph = new DummyForwardRuleInfGraph();
		FRuleEngineI engine = FRuleEngineIFactory.getInstance().createFRuleEngineI(infGraph, null, false);

		assertSame("A FRuleEngine should have been instantiated", FRuleEngine.class, engine.getClass());
	}

	public void testItShouldInstantiateAReteEngineIfUseRETEisTrue() {
		ForwardRuleInfGraphI infGraph = new DummyForwardRuleInfGraph();
		FRuleEngineI engine = FRuleEngineIFactory.getInstance().createFRuleEngineI(infGraph, null, true);

		assertSame("A RETEEngine should have been instantiated", RETEEngine.class, engine.getClass());
	}

	private static final class MyFRuleEngineIFactory extends FRuleEngineIFactory {
	}

	private static final class DummyForwardRuleInfGraph implements ForwardRuleInfGraphI {

		@Override
		public Graph getRawGraph() {
			return null;
		}

		@Override
		public Reasoner getReasoner() {
			return null;
		}

		@Override
		public void rebind(Graph data) {
		}

		@Override
		public void rebind(boolean resetDeductions) {
		}
		
		@Override
		public void rebind(Node... rederiveForPredicates) {	
		}

		@Override
		public void prepare() {
		}

		@Override
		public void reset() {
		}

		@Override
		public Node getGlobalProperty(Node property) {
			return null;
		}

		@Override
		public boolean testGlobalProperty(Node property) {
			return false;
		}

		@Override
		public ValidityReport validate() {
			return null;
		}

		@Override
		public ExtendedIterator<Triple> find(Node subject, Node property, Node object, Graph param) {
			return null;
		}

		@Override
		public void setDerivationLogging(boolean logOn) {
		}

		@Override
		public Iterator<Derivation> getDerivation(Triple triple) {
			return null;
		}

		@Override
		public boolean dependsOn(Graph other) {
			return false;
		}

		@Override
		public TransactionHandler getTransactionHandler() {
			return null;
		}

		@Override
		public Capabilities getCapabilities() {
			return null;
		}

		@Override
		public GraphEventManager getEventManager() {
			return null;
		}

		@SuppressWarnings("deprecation")
		@Override
		public GraphStatisticsHandler getStatisticsHandler() {
			return null;
		}

		@Override
		public PrefixMapping getPrefixMapping() {
			return null;
		}

		@Override
		public void add(Triple t) throws AddDeniedException {
		}

		@Override
		public void delete(Triple t) throws DeleteDeniedException {
		}

		@Override
		public ExtendedIterator<Triple> find(Triple m) {
			return null;
		}

		@Override
		public ExtendedIterator<Triple> find(Node s, Node p, Node o) {
			return null;
		}

		@Override
		public boolean isIsomorphicWith(Graph g) {
			return false;
		}

		@Override
		public boolean contains(Node s, Node p, Node o) {
			return false;
		}

		@Override
		public boolean contains(Triple t) {
			return false;
		}

		@Override
		public void clear() {
		}

		@Override
		public void remove(Node s, Node p, Node o) {
		}

		@Override
		public void close() {
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean isClosed() {
			return false;
		}

		@Override
		public void silentAdd(Triple t) {
		}

		@Override
		public boolean shouldTrace() {
			return false;
		}

		@Override
		public void addBRule(Rule brule) {
		}

		@Override
		public void deleteBRule(Rule brule) {
		}

		@Override
		public Graph getDeductionsGraph() {
			return null;
		}

		@Override
		public Graph getCurrentDeductionsGraph() {
			return null;
		}

		@Override
		public void addDeduction(Triple t) {
		}

		@Override
		public ExtendedIterator<Triple> findDataMatches(Node subject, Node predicate, Node object) {
			return null;
		}

		@Override
		public boolean shouldLogDerivations() {
			return false;
		}

		@Override
		public void logDerivation(Triple t, Derivation derivation) {
		}

		@Override
		public void setFunctorFiltering(boolean param) {
		}

		@Override
		public void setDeductionListener(DeductionListener listener) {
		}

		@Override
		public void setConfig(GraphConfig config) {
		}

		@Override
		public GraphConfig getConfig() {
			return null;
		}

		@Override
		public N3GraphConfig getN3Config() {
			return null;
		}

		@Override
		public Node uniqueBlankNode(Scope scope) {
			return null;
		}

		@Override
		public GraphStats getStats() {
			return null;
		}

		@Override
		public boolean hasScope() {
			return false;
		}

		@Override
		public Scope getScope() {
			return null;
		}

		@Override
		public void setScope(Scope scope) {
		}
	}
}
