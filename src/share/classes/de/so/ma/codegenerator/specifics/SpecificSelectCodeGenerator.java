package de.so.ma.codegenerator.specifics;

import java.util.List;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.Lists;
import com.sun.tools.javac.code.Type;

import de.so.ma.codegenerator.MethodDeclarationCodeGenerator;
import de.so.ma.data.ProjectionItem;
import de.so.ma.querynodes.JCSqlQuery;
import de.so.ma.types.TypesHelper;
import de.so.ma.validation.matching.flatobject.FlatObject;
import de.so.ma.validation.matching.flatobject.FlatObjectAttribute;
import de.so.ma.validation.matching.flatobject.ObjectAttribute;
import de.so.ma.validation.matching.flatobject.SimpleAttribute;

public class SpecificSelectCodeGenerator extends SpecificCodeGenerator {
	public SpecificSelectCodeGenerator(JCSqlQuery query) {
		super(query);
	}

	@Override
	public String getMethodFooterTemplate(MethodDeclarationCodeGenerator methodDeclarationCodeGenerator) {
		String selectSpecificMethodTemplate = 
				"            java.sql.ResultSet rs = ps.executeQuery();\n" + 
				"\n" + 
				"            // convert to expected type\n" + 
				"            %s\n" +
				"            return result;";
		return String.format(selectSpecificMethodTemplate, 
				getTypeConversion(query.getExpectedType()));
	}
	
	private String getTypeConversion(Type expectedType) {
		TypesHelper typesHelper = TypesHelper.getInstance();
		
		if (typesHelper.isResultSet(expectedType)) {
			// expecting result set
			return "java.sql.ResultSet result = rs;";
		} else if(typesHelper.isMapType(expectedType)) {
			Type leftType = expectedType.allparams().get(0);
			Type rightType = expectedType.allparams().get(1);
			
			String mapConversionTmpl = 
					"%s result = new java.util.LinkedHashMap<%s, %s>();\n"
					+ "while (rs.next()) {\n"
					+ "    result.put(rs.get%s(1), rs.get%s(2));\n"
					+ "}\n";
			
			return String.format(mapConversionTmpl,
					expectedType,
					leftType,
					rightType,
					getJdbcTypeName(leftType),
					getJdbcTypeName(rightType));
		} else {
			String typeConversion;			
			Type rawType = typesHelper.getRawType(expectedType);
			
			// create access methods to create the required objects
			if (typesHelper.isSimpleType(rawType)) {
				typeConversion = getSimpleTypeConversion(rawType);
			} else {
				typeConversion = getObjectTypeConversion(rawType);
			}
			
			// wrap the required objects either in a single variable or in a list
			if (typesHelper.isListType(expectedType)) {
				typeConversion = wrapAsListTypeConversion(typeConversion, expectedType);
			} else {
				typeConversion = wrapAsSingleTypeConversion(typeConversion, expectedType);
			}
			
			return typeConversion;
		}
	}
	
	private String wrapAsSingleTypeConversion(String typeConversion, Type expectedType) {
		String convTemplate = "" +
				"%s result = %s;\n" + 
				"           if (rs.next()) {\n" + 
				"           	%s" + 
				"           	\n" + 
				"           	result = intermediateResult;\n" + 
				"           } else {\n" + 
				"           	throw new RuntimeException(\"DB returned an empty result.\");\n" + 
				"           }";
		
		return String.format(convTemplate, 
				expectedType,
				expectedType.isPrimitive() ? "0" : "null",
				typeConversion);
	}
	
	private String getSimpleTypeConversion(Type expectedType) {
		String convTemplate = "" +
				"			%s intermediateResult = rs.get%s(1);\n";
		
		String conversionString = String.format(convTemplate, 
				expectedType,
				getJdbcTypeName(expectedType)
		);
		
		return conversionString;
	}

	private String getObjectTypeConversion(Type expectedType) {
		BiMap<ProjectionItem, FlatObjectAttribute> mapping = 
				query.getProjectionsMapping();
		
		StringBuilder sb = new StringBuilder();
		
		FlatObject rootObject = query.getFlatObject();
		
		BiMap<FlatObjectAttribute, ProjectionItem> inverseMapping =
				mapping.inverse();
		
		
		List<FlatObject> requiredFlatObjects = 
				getRequiredFlatObjects(rootObject, inverseMapping);
		addObjectAttributes(rootObject, sb, inverseMapping, requiredFlatObjects, "intermediateResult");
		
		return sb.toString();
	}

	/**
	 * create a list of root objects whose references need to be inspected by the code generator
	 * @param rootObject
	 * @param inverseMapping
	 * @return
	 */
	private List<FlatObject> getRequiredFlatObjects(FlatObject rootObject,
			BiMap<FlatObjectAttribute, ProjectionItem> inverseMapping) {
		
		List<FlatObject> result = Lists.newArrayList();
		
		boolean addedCurrentRootObject = false;
		
		// iterate recursively through the attributes to find all required FlatObjects
		for (FlatObjectAttribute att : rootObject.getAttributes()) {
			if (att instanceof SimpleAttribute && inverseMapping.containsKey(att)) {
				// that simple object is in the mapping, so the parent root object needs to be added to the result
				if (!addedCurrentRootObject) {
					result.add(rootObject);
					addedCurrentRootObject = true;
				}
			} else if (att instanceof ObjectAttribute) {
				ObjectAttribute objectAtt = (ObjectAttribute) att;
				result.addAll(getRequiredFlatObjects(objectAtt.getReferencedObject(), inverseMapping));
			}
		}
		
		return result;
	}
	
	private void addObjectAttributes(FlatObject flatObject, StringBuilder sb,
			Map<FlatObjectAttribute, ProjectionItem> mapping, List<FlatObject> requiredFlatObjects, String objectName) {
		TypesHelper typesHelper = TypesHelper.getInstance();
		
		// object instance
		sb.append(String.format("			%s %s = new %s();\n",
				flatObject.getType(),
				objectName,
				flatObject.getType()));
		
		for (FlatObjectAttribute att : flatObject.getAttributes()) {
			// setter
			if (att instanceof SimpleAttribute) {
				SimpleAttribute simpleAtt = (SimpleAttribute) att;
				if (mapping.containsKey(simpleAtt)) {
					// the simple attribute is contained by the mapping, so we can and must use it
					String setAttributeString = 
							mapping.get(att).getBaseTable() == null ?
									String.format("			%s.set%s(rs.get%s(\"%s\"));\n",
											objectName,
											att.getName(),
											typesHelper.getJdbcTypeName(
													typesHelper.getBoxedType(att.getType()).toString()),
											mapping.get(att).getName()) :
									String.format("			%s.set%s(rs.get%s(\"%s.%s\"));\n",
											objectName,
											att.getName(),
											typesHelper.getJdbcTypeName(
													typesHelper.getBoxedType(att.getType()).toString()),
											mapping.get(att).getBaseTable(),
											mapping.get(att).getName());
					sb.append(setAttributeString);
				}
			} else if (att instanceof ObjectAttribute) {
				ObjectAttribute objectAtt = (ObjectAttribute) att;
				// process objects attribute
				if (requiredFlatObjects.contains(objectAtt.getReferencedObject())) {
					addObjectAttributes(
							objectAtt.getReferencedObject(), 
							sb, 
							mapping, 
							requiredFlatObjects,
							objectAtt.getFullPlainName());
					
					// set object attribute to current object
					sb.append(String.format("%s.set%s(%s);\n", 
							objectName,
							objectAtt.getName(),
							objectAtt.getFullPlainName()));
				}
			}
		}
	}
}
