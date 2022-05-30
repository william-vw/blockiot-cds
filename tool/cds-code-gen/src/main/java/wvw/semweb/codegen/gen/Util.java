package wvw.semweb.codegen.gen;

import org.apache.jen3.graph.Node_URI;
import org.apache.jen3.rdf.model.Resource;

import wvw.semweb.codegen.model.adt.ModelProperty;
import wvw.semweb.codegen.model.logic.NodePath;
import wvw.semweb.codegen.model.logic.Operand;
import wvw.semweb.codegen.model.logic.Operand.Operands;

public class Util {

	public static String localName(Node_URI uri) {
		return localName(uri.getURI());
	}

	public static String localName(Resource uri) {
		return localName(uri.getURI());
	}

	public static String localName(String uri) {
		int idx1 = uri.lastIndexOf("#");
		int idx2 = uri.lastIndexOf("/");

		int idx = (idx1 > idx2 ? idx1 : idx2);
		return uri.substring(idx + 1);
	}

	public static boolean involvesArrayCheck(Operand op1) {
		if (op1.getType() == Operands.NODE_PATH) {
			NodePath path = (NodePath) op1;

			if (path.size() > 1) {
				ModelProperty prp = path.getPath().get(path.size() - 2);
				return prp.requiresArray();
			}
		}

		return false;
	}

	public static boolean involvesArrayAssign(Operand op1) {
		if (op1.getType() == Operands.NODE_PATH) {
			NodePath p = (NodePath) op1;
			if (p.getPath().isEmpty())
				return false;

			ModelProperty lastPrp = p.getPath().getLast();
			return lastPrp.requiresArray();
		}

		return false;
	}
}
