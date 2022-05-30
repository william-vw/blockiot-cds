package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.ComparisonPattern;

public class LessThan extends MathBuiltin {

	public LessThan() {
		super(new ComparisonPattern((n1, n2) -> Util.compareNumbers(n1, n2) == -1), true);
	}
}