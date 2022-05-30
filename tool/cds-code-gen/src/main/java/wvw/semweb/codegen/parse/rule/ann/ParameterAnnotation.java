package wvw.semweb.codegen.parse.rule.ann;

import java.util.Objects;

import org.apache.jen3.graph.Node;

public class ParameterAnnotation extends RuleAnnotation {

	public static enum ParameterTypes {
		FUNCTION, LOAD
	}

	private ParameterTypes paramType;

	public ParameterAnnotation(Node param, ParameterTypes type) {
		super(param, AnnotationTypes.PARAM);

		this.paramType = type;
	}

	public ParameterTypes getParameterType() {
		return paramType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(paramType);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParameterAnnotation other = (ParameterAnnotation) obj;
		return paramType == other.paramType;
	}

	@Override
	public String toString() {
		return "PARAM (" + paramType + "): " + node;
	}
}
