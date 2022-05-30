package wvw.semweb.codegen.model.adt;

import org.apache.jen3.datatypes.RDFDatatype;

public class ModelType {

	private RDFDatatype dataType;
	private ModelADT objectType;
	
	public ModelType(RDFDatatype dataType) {
		this.dataType = dataType;
	}

	public ModelType(ModelADT objectType) {
		this.objectType = objectType;
	}

	public boolean hasDataType() {
		return dataType != null;
	}
	
	public RDFDatatype getDataType() {
		return dataType;
	}
	
	public boolean hasObjectType() {
		return objectType != null;
	}

	public ModelADT getObjectType() {
		return objectType;
	}
	
	@Override
	public String toString() {
		return (hasDataType() ? dataType.toString() : objectType.getString());
	}
}
