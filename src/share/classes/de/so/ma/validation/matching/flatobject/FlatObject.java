package de.so.ma.validation.matching.flatobject;

import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.sun.tools.javac.code.Scope;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.code.Symbol.MethodSymbol;
import com.sun.tools.javac.code.Symbol.VarSymbol;
import com.sun.tools.javac.code.Type;

import de.so.ma.types.TypesHelper;

public class FlatObject {
	private List<FlatObjectAttribute> attributes;
	private ObjectAttribute parentAttribute;
	private Type type;

	public FlatObject(Type expectedType) {
		this(expectedType, null);
	}
	
	public FlatObject(Type expectedType, ObjectAttribute parentAttribute) {
		this.type = expectedType;
		this.parentAttribute = parentAttribute;
		
		this.attributes = Lists.newArrayList();
			
		buildUpFlatObject();
	}

	private void buildUpFlatObject() {
		TypesHelper typesHelper = TypesHelper.getInstance();
		
		Scope members = type.tsym.members();
	
		for (Symbol member : members.getElements()) {
			if (member instanceof MethodSymbol) {
				MethodSymbol methodMember = (MethodSymbol) member;
	
				// try to identify setters: must start with set
				if (methodMember.name.toString().startsWith("set")) {
					VarSymbol setterParam = Iterables.getOnlyElement(methodMember.getParameters());
					String setterName = member.name.toString().replace("set", "");
	
					if (typesHelper.isSimpleType(setterParam.type)) {
						attributes.add(new SimpleAttribute(setterParam.type, setterName, this));
					} else if (typesHelper.isListType(setterParam.type)) {
						// do nothing but ignore the list type
					} else {						
						attributes.add(new ObjectAttribute(setterParam.type, setterName, this));
					}
				}
			}
		}
	}
	
	public int getAttributeCardinality() {
		int sum = 0;
		
		for (FlatObjectAttribute foa : attributes) {
			sum += foa.getCardinality();
		}
		
		return sum;
	}

	public List<FlatObjectAttribute> getAttributes() {
		return attributes;
	}
	
	public List<FlatObjectAttribute> getFlatAttributes() {
		List<FlatObjectAttribute> flatAttributes = Lists.newArrayList();
		
		for (FlatObjectAttribute att : getAttributes()) {
			if (att instanceof SimpleAttribute || att instanceof DummyAttribute) {
				flatAttributes.add(att);
			} else if (att instanceof ObjectAttribute) {
				FlatObject referencedObject = ((ObjectAttribute) att).getReferencedObject(); 
				flatAttributes.addAll(referencedObject.getFlatAttributes());
			}
		}
		
		return flatAttributes;
	}
	
	public boolean isRoot() {
		return parentAttribute == null;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getPlainTypeString() {
		return Iterables.getLast(
				Splitter
					.on(".")
					.split(getType().toString())
			).toLowerCase();
	}

	public ObjectAttribute getParentAttribute() {
		return parentAttribute;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.addValue(type)
				.toString();
	}

	public String getFullPath() {
		if (isRoot()) {
			return "root";
		} else {
			return parentAttribute.getFullPath();
		}
	}
}
