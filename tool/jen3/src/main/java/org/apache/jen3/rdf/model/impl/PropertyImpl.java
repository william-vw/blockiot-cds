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

package org.apache.jen3.rdf.model.impl;

import org.apache.jen3.enhanced.EnhGraph;
import org.apache.jen3.enhanced.EnhNode;
import org.apache.jen3.enhanced.Implementation;
import org.apache.jen3.graph.Node;
import org.apache.jen3.rdf.model.Model;
import org.apache.jen3.rdf.model.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** An implementation of Property.
 */

public class PropertyImpl extends ResourceImpl implements Property
    {

    @SuppressWarnings("hiding")
    final static public Implementation factory = new Implementation() 
        {
        @Override
        public boolean canWrap( Node n, EnhGraph eg )
            { return n.isURI(); }

        @Override
        public EnhNode wrap( Node n, EnhGraph eg )
            { return new PropertyImpl( n, eg ); }
    };

    protected static Logger logger = LoggerFactory.getLogger( PropertyImpl.class );

    /** Creates new PropertyImpl */
    public PropertyImpl( String uri )
        {
        super( uri );
        }

    @Override public Property inModel( Model m )
        { return getModel() == m ? this : m.createProperty( getURI() ); }

    public PropertyImpl( String nameSpace, String localName )
        {
        super( nameSpace, localName );
        }

    public PropertyImpl( String uri, ModelCom m )
        {
        super( uri, m );
        }

    public PropertyImpl( String nameSpace, String localName, ModelCom m )
        {
        super( nameSpace, localName, m );
        }

    public PropertyImpl( Node n, EnhGraph m )
        {
        super( n, m );
        }

    public PropertyImpl( String nameSpace, String localName, int ordinal, ModelCom m )
        {
        super( nameSpace, localName, m );
        }

    @Override
    public boolean isProperty()
        { return true; }
}
