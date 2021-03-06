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

package org.apache.jen3.util;

import java.util.*;

import junit.framework.TestSuite;

import org.apache.jen3.rdf.model.test.ModelTestBase;
import org.apache.jen3.util.CollectionFactory;

/**
 	TestHashUtils - test that the hash utility returns a map.
*/
public class TestCollectionFactory extends ModelTestBase
    {
    public TestCollectionFactory( String name )
    	{ super( name ); }
    
    public static TestSuite suite()
        { return new TestSuite( TestCollectionFactory.class ); }

    public void testHashMapExists()
        {
        assertInstanceOf( Map.class, CollectionFactory.createHashedMap() );
        }
    
    public void testHashMapSized()
        {
        assertInstanceOf( Map.class, CollectionFactory.createHashedMap( 42 ) );
        }
    
    public void testHashMapCopy()
        {
        Map<String, String> map = new HashMap<>();
        map.put( "here", "Bristol" );
        map.put( "there", "Oxford" );
        Map<String, String> copy = CollectionFactory.createHashedMap( map );
        assertEquals( map, copy );
        }
    
    public void testHashSetExists()
        {
        assertInstanceOf( Set.class, CollectionFactory.<Object>createHashedSet() );
        }
    
    public void testHashSetCopy()
        {
        Set<String> s = new HashSet<>();
        s.add( "jelly" );
        s.add( "concrete" );
        Set<String> copy = CollectionFactory.createHashedSet( s );
        assertEquals( s, copy );
        }
    }
