/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jen3.ext.xerces.xs;

import org.w3c.dom.ls.LSInput;

/**
 * This interface allows one to retrieve an instance of <code>XSLoader</code>. 
 * This interface should be implemented on the same object that implements 
 * DOMImplementation.
 */
public interface XSImplementation {
    /**
     * A list containing the versions of XML Schema documents recognized by 
     * this <code>XSImplemenation</code>.
     */
    public StringList getRecognizedVersions();


    /**
     * Creates a new XSLoader. The newly constructed loader may then be 
     * configured and used to load XML Schemas.
     * @param versions  A list containing the versions of XML Schema 
     *   documents which can be loaded by the <code>XSLoader</code> or 
     *   <code>null</code> to permit XML Schema documents of any recognized 
     *   version to be loaded by the XSLoader. 
     * @return  An XML Schema loader. 
     * @exception XSException
     *   NOT_SUPPORTED_ERR: Raised if the implementation does not support one 
     *   of the specified versions.
     */
    public XSLoader createXSLoader(StringList versions)
                                   throws XSException;
    
    /**
     * Creates an immutable <code>StringList</code> from the given array of <code>String</code>s.
     * @param values the array containing the <code>String</code> values that will be placed in the list.
     * @return an immutable <code>StringList</code> from the given array of <code>String</code>s.
     */
    public StringList createStringList(String[] values);
    
    /**
     * Creates an immutable <code>LSInputList</code> from the given array of <code>LSInput</code>s.
     * @param values the array containing the <code>LSInput</code> values that will be placed in the list.
     * @return an immutable <code>LSInputList</code> from the given array of <code>LSInput</code>s.
     */
    public LSInputList createLSInputList(LSInput[] values);

}
