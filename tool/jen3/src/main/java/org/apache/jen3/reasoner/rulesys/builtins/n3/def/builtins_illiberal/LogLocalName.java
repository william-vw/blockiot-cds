package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.log.LocalName;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class LogLocalName extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public LogLocalName() {
        super("http://www.w3.org/2000/10/swap/log#localName", new LocalName(), false, true, true);
        setInputConstraints(new InputIriAndStringOrVariable(this));
    }
}
