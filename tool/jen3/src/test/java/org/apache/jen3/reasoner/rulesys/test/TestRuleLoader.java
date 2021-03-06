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

import org.apache.jen3.reasoner.rulesys.Rule;
import org.apache.jen3.shared.RulesetNotFoundException;
import org.apache.jen3.shared.WrappedIOException;
import org.junit.Test;

/**
 * Tests for the rule loader
 */
public class TestRuleLoader  {
    
    @Test(expected=RulesetNotFoundException.class)
    public void load_from_file_uri_non_existent() {
        Rule.rulesFromURL("file:///no-such-file.txt");
    }
    
    @Test(expected=WrappedIOException.class)
    public void load_from_file_bad_encoding() {
        Rule.rulesFromURL("testing/reasoners/bugs/bad-encoding.rules");
    }
}
