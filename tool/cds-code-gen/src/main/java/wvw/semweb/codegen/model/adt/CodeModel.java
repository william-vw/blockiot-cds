package wvw.semweb.codegen.model.adt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jen3.graph.Node;

public class CodeModel {

	private static int adtCnt = 0;

	private Map<String, ModelADT> nameStructs = new HashMap<>();
	public Map<Node, ModelADT> nodeStructs = new HashMap<>();

	public ModelADT getOrCreateStruct(String name, Node node) {
		ModelADT ret = nameStructs.get(name);
		if (ret == null) {
			ret = new ModelADT(name, adtCnt++);

			nameStructs.put(name, ret);
		}

		nodeStructs.put(node, ret);

		return ret;
	}

	public void mergeWith(CodeModel newModel) {
		if (this.nameStructs.isEmpty())
			this.nameStructs = newModel.nameStructs;
		else {
			newModel.nameStructs.entrySet().stream().forEach(e -> {
				String name = e.getKey();
				ModelADT adt = e.getValue();

				if (nameStructs.containsKey(name)) {
					ModelADT adt0 = nameStructs.get(name);
					adt0.replacing(adt);

				} else
					nameStructs.put(name, adt);
			});
		}

		// TODO this will likely cause issues if we're not careful with variable naming
		this.nodeStructs.putAll(newModel.nodeStructs);
	}

	// TODO
	public void removeStruct(ModelADT adt) {
		nameStructs.entrySet().removeIf(e -> e.getValue().equals(adt));
		nodeStructs.entrySet().removeIf(e -> e.getValue().equals(adt));
	}

	public ModelADT getStruct(Node node) {
		return nodeStructs.get(node);
	}

	public void setStruct(Node node, ModelADT adt) {
		nodeStructs.put(node, adt);
	}

	public Collection<ModelADT> getAllStructs() {
		List<ModelADT> sorted = new ArrayList<>(nameStructs.values());
		sorted.sort(null);

		return sorted;
	}

	@Override
	public String toString() {
		return nameStructs.values().stream().map(s -> s.toString()).collect(Collectors.joining("\n"));
	}
}
