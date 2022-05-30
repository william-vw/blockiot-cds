package wvw.semweb.codegen.model.logic;

import java.util.stream.Collectors;

public class Conjunction extends MultiCondition {

	@Override
	public Conditions getConditionType() {
		return Conditions.CONJ;
	}

	@Override
	public String toString() {
		return conditions.stream().map(c -> c.toString()).collect(Collectors.joining(" &&\n"));
	}
}
