package wvw.semweb.codegen.model.logic;

import java.util.ArrayList;
import java.util.List;

public abstract class MultiCondition extends Condition {

	protected List<Condition> conditions = new ArrayList<>();

	public void add(Condition condition) {
		conditions.add(condition);
	}

	public List<Condition> getConditions() {
		return conditions;
	}
}
