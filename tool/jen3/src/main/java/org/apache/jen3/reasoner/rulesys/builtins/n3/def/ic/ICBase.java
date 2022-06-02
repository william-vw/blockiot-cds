package org.apache.jen3.reasoner.rulesys.builtins.n3.def.ic;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ICBase implements Serializable {

	private static final long serialVersionUID = 6057543837626786859L;

	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}
}
