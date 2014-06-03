package de.so.ma.validation.matching.flatobject;

import com.google.common.base.Objects;
import com.sun.tools.javac.code.Type;

public class ObjectAttribute extends FlatObjectAttribute {
	private FlatObject referencedObject;

	public ObjectAttribute(Type type, String name, FlatObject parentObject) {
		super(type, name, parentObject);
		
		referencedObject = new FlatObject(type, this);
	}

	@Override
	public int getCardinality() {
		return referencedObject.getAttributeCardinality();
	}
	
	public FlatObject getReferencedObject() {
		return referencedObject;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.addValue(name)
				.addValue(parentObject.getType())
				.toString();
	}
}
