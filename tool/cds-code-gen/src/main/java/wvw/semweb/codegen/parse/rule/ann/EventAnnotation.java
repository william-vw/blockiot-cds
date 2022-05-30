package wvw.semweb.codegen.parse.rule.ann;

import org.apache.jen3.graph.Node;

public class EventAnnotation extends RuleAnnotation {

	public EventAnnotation(Node event) {
		super(event, AnnotationTypes.EVENT);
	}

	@Override
	public String toString() {
		return "EVENT: " + node.toString();
	}
}
