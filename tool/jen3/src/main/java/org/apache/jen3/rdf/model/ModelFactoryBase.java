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

package org.apache.jen3.rdf.model;

import org.apache.jen3.JenaRuntime;
import org.apache.jen3.shared.*;

/**
    Helper functions for ModelFactory - in here to keep from obtruding on the
    end-users.
*/
public class ModelFactoryBase
    {

    protected static String gp( String name )
        {
        String answer = gp( name, null );
        if (answer == null) throw new JenaException( "no binding for " + name );
        return answer;
        }
    
    protected static String gp( String name, String ifAbsent )
        { 
        String answer = JenaRuntime.getSystemProperty( "jena." + name ); 
        return answer == null ? ifAbsent : answer;
        }

    }