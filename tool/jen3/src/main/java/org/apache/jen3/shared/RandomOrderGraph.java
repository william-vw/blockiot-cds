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

package org.apache.jen3.shared;

import org.apache.jen3.graph.*;
import org.apache.jen3.graph.impl.WrappedGraph;
import org.apache.jen3.rdf.model.*;
import org.apache.jen3.util.iterator.*;

/**
 * Wraps a graph and randomizes the order of find results.
 */
public class RandomOrderGraph extends WrappedGraph {
	
	public static Graph createDefaultGraph() {
		return new RandomOrderGraph(Factory.createDefaultGraph());
	}
	public static Model createDefaultModel() {
		return ModelFactory.createModelForGraph(createDefaultGraph());
	}
    final private int bufsz;
	/**
	 * @param base
	 */
	public RandomOrderGraph(int bufsz, Graph base) {
		super(base);
		this.bufsz = bufsz;
	}
	/**
	 * @param base
	 */
	public RandomOrderGraph(Graph base) {
		this(10,base);
	}
	
	@Override
    public ExtendedIterator<Triple> find( Triple triple )
	{ return new RandomOrderIterator<>(bufsz,super.find( triple ));
	}

	@Override
    public ExtendedIterator<Triple> find( Node s, Node p, Node o )
	{ return new RandomOrderIterator<>(bufsz,super.find( s, p, o ));
	}
	@Override
	public Capabilities getCapabilities() {
		return new MyCapabilities( super.getCapabilities() );
	}
	
	private class MyCapabilities implements Capabilities {
		private Capabilities parentCapabilities;
		
		public MyCapabilities( Capabilities parentCapabilities )
		{
			this.parentCapabilities = parentCapabilities;
		}

		@Override
        public boolean sizeAccurate() {
			return parentCapabilities.sizeAccurate();
		}

		@Override
        public boolean addAllowed() {
			return parentCapabilities.addAllowed();
		}

        @Override
        public boolean deleteAllowed() {
            return parentCapabilities.deleteAllowed();
        }

		@SuppressWarnings("deprecation")
        @Override
        public boolean addAllowed(boolean everyTriple) {
			return parentCapabilities.addAllowed(everyTriple);
		}

        @SuppressWarnings("deprecation")
		@Override
        public boolean deleteAllowed(boolean everyTriple) {
			return parentCapabilities.deleteAllowed(everyTriple);
		}

        @SuppressWarnings("deprecation")
		@Override
        public boolean canBeEmpty() {
			return parentCapabilities.canBeEmpty();
		}

        @SuppressWarnings("deprecation")
		@Override
        public boolean findContractSafe() {
			return parentCapabilities.findContractSafe();
		}

		@Override
        public boolean handlesLiteralTyping() {
			return parentCapabilities.handlesLiteralTyping();
		}

		@Override
		public boolean iteratorRemoveAllowed() {
			return false;
		}
		
		
	}

}
