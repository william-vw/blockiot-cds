package org.apache.jen3.reasoner.rulesys.builtins.n3.def.builtins_illiberal;

import org.apache.jen3.reasoner.rulesys.builtins.n3.time.DayOfWeek;
import org.apache.jen3.reasoner.rulesys.builtins.n3.def.BuiltinDefinition;

public class TimeDayOfWeek extends BuiltinDefinition {

    private static final long serialVersionUID = 1;

    public TimeDayOfWeek() {
        super("http://www.w3.org/2000/10/swap/time#dayOfWeek", new DayOfWeek(), false, true, true);
        setInputConstraints(new InputDateAndIntableOrVariable(this));
    }
}
