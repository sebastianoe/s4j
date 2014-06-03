package de.so.ma.validation.matching.flatobject;

import com.sun.tools.javac.code.Type;

import de.so.ma.types.TypesHelper;

public abstract class FlatObjectAttribute {
	protected String name;
	protected Type type;
	protected FlatObject parentObject;

	public FlatObjectAttribute(Type type, String name, FlatObject parentObject) {
		this.name = name;
		this.type = type;
		this.parentObject = parentObject;
	}

	public abstract int getCardinality();

	public String getFullPlainName() {
		return (parentObject.getParentAttribute() == null) ? name.toLowerCase() : (parentObject.getParentAttribute().getFullPlainName() + name.toLowerCase());
	}
	
	public String getPlainName() {
		return name.toLowerCase();
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return TypesHelper.getInstance().getBoxedType(type);
	}

	public FlatObject getParentObject() {
		return parentObject;
	}

	public int getDepth() {
		if (parentObject.getParentAttribute() == null) {
			return 1;
		} else {
			return 1 + parentObject.getParentAttribute().getDepth();
		}
	}

	public String getFullPath() {
		return parentObject.getFullPath() + "." + name;
	}
}
