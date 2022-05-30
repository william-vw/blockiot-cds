package org.apache.jen3.reasoner.rulesys.builtins.n3.math;

import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.Util;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class Min extends MathBuiltin {

	public Min() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection coll = (Node_Collection) n;
			return coll.getElements().stream().min((n1, n2) -> Util.compareNumbers(n1, n2)).orElse(null);

		}, null), true);
	}
}
