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

package org.apache.jen3.reasoner.rulesys.builtins;

import org.apache.jen3.graph.*;
import org.apache.jen3.reasoner.rulesys.*;

/**
 * Bind the second argument to 1+ the first argument. Just used for testing builtins.
 */
public class AddOne extends BaseBuiltin {

    /**
     * Return a name for this builtin, normally this will be the name of the 
     * functor that will be used to invoke it.
     */
    @Override
    public String getName() {
        return "addOne";
    }
    
    /**
     * Return the expected number of arguments for this functor or 0 if the number is flexible.
     */
    @Override
    public int getArgLength() {
        return 2;
    }

    /**
     * This method is invoked when the builtin is called in a rule body.
     * @param args the array of argument values for the builtin, this is an array 
     * of Nodes, some of which may be Node_RuleVariables.
     * @param context an execution context giving access to other relevant data
     * @param length the length of the argument list, may be less than the length of the args array
     * for some rule engines
     * @return return true if the buildin predicate is deemed to have succeeded in
     * the current environment
     */
    @Override
    public boolean bodyCall(Node[] args, int length, RuleContext context) {
        checkArgs(length, context);
        BindingEnvironment env = context.getEnv();
        boolean ok = false;
        Node a0 = getArg(0, args, context);
        Node a1 = getArg(1, args, context);
        if (Util.isNumeric(a0)) {
            Node newVal = Util.makeIntNode( Util.getIntValue(a0) + 1 );
            ok = env.bind(args[1], newVal);
        } else if (Util.isNumeric(a1)) {
            Node newVal = Util.makeIntNode( Util.getIntValue(a1) - 1 );
            ok = env.bind(args[0], newVal);
        }
        return ok;
    }
    
}
