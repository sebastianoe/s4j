package de.so.ma.validation.matching.flatobject;

import com.sun.tools.javac.code.Type;
import com.google.common.base.Objects;

public class SimpleAttribute extends FlatObjectAttribute {
	public SimpleAttribute(Type type, String name, FlatObject parentObject) {
		super(type, name, parentObject);
	}

	@Override
	public int getCardinality() {
		return 1;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper("SA")
				.addValue(name)
				.addValue(getParentObject().isRoot() ? "root" : getParentObject().getParentAttribute().getName())
				.toString();
	}
}
