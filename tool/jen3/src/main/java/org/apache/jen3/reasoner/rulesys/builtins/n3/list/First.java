package org.apache.jen3.reasoner.rulesys.builtins.n3.list;

import org.apache.jen3.graph.n3.Node_Collection;
import org.apache.jen3.reasoner.rulesys.builtins.n3.flow.BinaryFlowPattern;

public class First extends ListBuiltin {

	public First() {
		super(new BinaryFlowPattern((n, g) -> {
			Node_Collection list = (Node_Collection) n;
			if (list.size() == 0)
				return null;

			return list.getElement(0);

		}, null), true);
	}
}