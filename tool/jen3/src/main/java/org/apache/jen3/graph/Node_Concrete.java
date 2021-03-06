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

/**
    This is the class of "concrete" nodes, ie those which correspond
    to actual RDF data - URIs, blank nodes, and literals.
 */
public abstract class Node_Concrete extends Node
    {
    protected Node_Concrete( Object label )
        {  super( label ); }
        
    @Override
    public boolean isConcrete()
        { return true; }
    
	public Object getLabel() 
		{ return label; }
    
	
	// we don't want just any concrete node to be equal based on their name;
	// equal only to a Node_Named based on shared label
	
	// if a subclass determines that the given object is not strictly equal,
	// it should delegate to this superclass method
	
	@Override
	public boolean equals(Object o) 
		{
		return (o instanceof Node_Named && label.equals(((Node_Named) o).getLabel()));
		}
}