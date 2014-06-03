package de.so.ma.data;

import java.util.Map;

import com.google.common.collect.Iterables;
import com.sun.tools.javac.code.Type;
import com.sun.tools.javac.comp.Attr;
import com.sun.tools.javac.comp.AttrContext;
import com.sun.tools.javac.comp.Env;
import com.sun.tools.javac.comp.Infer;
import com.sun.tools.javac.tree.JCTree.JCExpression;

import de.so.ma.codegenerator.reification.Reifier;
import de.so.ma.metarepo.Column;
import de.so.ma.types.TypesHelper;

public class Parameter {
	private String stringRepresentation;
	private JCExpression node;
	private Map<Column, Parameter> columnGetterMethods;
	
	private Type explicitType;

	public Parameter(String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
		this.node = null;
	}
	
	public Parameter(String stringRepresentation, Type explicitType) {
		this(stringRepresentation);
		this.explicitType = explicitType;
	}
	
	@Override
	public String toString() {
		return stringRepresentation;
	}
	
	public JCExpression getNode() {
		return node;
	}
	
	public void createNode(Attr attr, Env<AttrContext> env) {
		Reifier reifier = Reifier.getInstance();
		node = reifier.reifyExpression(stringRepresentation);
		
		Env<AttrContext> localEnv = env.dup(node, env.info.dup()); 
		attr.attribExpr(node, localEnv, Infer.anyPoly);
	}
	
	public Type getType() {
		if (node != null) {
			return node.type;
		} else {
			return explicitType;
		}
	}
	
	public boolean hasListType() {
		TypesHelper typesHelper = TypesHelper.getInstance();
		return typesHelper.isListType(getType());
	}
	
	public Type getObjectType() {
		if (hasListType()) {
			Type listType = getType();
			return Iterables.getOnlyElement(listType.allparams());
		} else {
			return getType();
		}
	}
	
	public String getStringRepresentation() {
		return stringRepresentation;
	}
	
	public void safePrependStringRepresentationWith(String prefix) {
		if (!stringRepresentation.startsWith(prefix)) {
			stringRepresentation = prefix + stringRepresentation;
		}
	}
	
	public Map<Column, Parameter> getColumnGetterMethods() {
		return columnGetterMethods;
	}

	public void setColumnGetterMethods(Map<Column, Parameter> columnGetterMethods) {
		this.columnGetterMethods = columnGetterMethods;
	}
}
