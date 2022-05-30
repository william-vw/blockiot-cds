package wvw.semweb.codegen.parse.rule.ann;

import java.util.Collection;
import java.util.stream.Collectors;

import wvw.semweb.codegen.parse.rule.ann.RuleAnnotation.AnnotationTypes;
import wvw.utils.map.HashMultiMapSet;
import wvw.utils.map.MultiMapSet;

public class Annotated {

	protected MultiMapSet<AnnotationTypes, RuleAnnotation> annotations = new HashMultiMapSet<>();

	public void addAll(Annotated other) {
		this.annotations.putAll(other.annotations);
	}
	
	public void addAll(Collection<RuleAnnotation> annotations) {
		annotations.forEach(a -> this.annotations.putValue(a.getType(), a));
	}
	
	public void add(RuleAnnotation annotation) {
		annotations.putValue(annotation.getType(), annotation);
	}

	public boolean has(AnnotationTypes type) {
		return annotations.containsKey(type);
	}

	public Collection<RuleAnnotation> get(AnnotationTypes type) {
		return annotations.get(type);
	}
	
	public Collection<RuleAnnotation> getAll() {
		return annotations.values().stream().flatMap(set -> set.stream()).collect(Collectors.toList());
	}
}
