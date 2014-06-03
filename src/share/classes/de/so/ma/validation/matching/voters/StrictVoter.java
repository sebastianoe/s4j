package de.so.ma.validation.matching.voters;

import de.so.ma.data.DummyProjectionItem;
import de.so.ma.data.ProjectionItem;
import de.so.ma.types.TypesHelper;
import de.so.ma.validation.matching.flatobject.DummyAttribute;
import de.so.ma.validation.matching.flatobject.FlatObjectAttribute;

public class StrictVoter implements Voter {
	private TypesHelper typesHelper;
	
	public StrictVoter() {
		typesHelper = TypesHelper.getInstance();
	}
	
	@Override
	public Integer calculateVoting(ProjectionItem projection, FlatObjectAttribute attribute) {
		// constellations with dummy projections or attributes get the lowest
		// voting score
		if (projection instanceof DummyProjectionItem || attribute instanceof DummyAttribute) {
			return 0;
		}

		int votings = 0;
		
		// if types aren't compatible, return a 0 voting
		if (!typesHelper.areTypesCompatible(attribute.getType(), projection.getTypeName())) {
			return 0;
		}

		// projection name == attribute name?
		if (projection.getName().equals(
				attribute.getPlainName())) {
			votings = 1;
		} else if ( // explicit concatenation of parent object's and attribute's name in projection)
				(attribute.getFullPlainName()).equalsIgnoreCase(
						projection.getName())) {
			return 5;
		} else {
			return testForeignKey(projection, attribute);
		}

		
		if ( // base table == object's parent attribute's name?
				!attribute.getParentObject().isRoot() && 
				attribute.getParentObject().getParentAttribute().getName().equalsIgnoreCase(
						projection.getBaseTable())) { 
			votings += 3;
		} else if ( // base table == object type name?
				attribute.getParentObject().getPlainTypeString().equalsIgnoreCase(
						projection.getBaseTable())) {
			votings += 2;
		} else if ( // original table == object type name?
				attribute.getParentObject().getPlainTypeString().equalsIgnoreCase( 
						projection.getOriginalTable())) {
			votings += 1;
		} else if ( // root attribute & projection has no table?
				projection.getBaseTable() == null &&
				attribute.getParentObject().isRoot()) {
			// nothing (votings += 0) but this preserves the current votings value 
		} else {
			return testForeignKey(projection, attribute);
		}
		
		return votings; 
	}
	
	private int testForeignKey(ProjectionItem projection, FlatObjectAttribute attribute) {
		if ( // fk-referencing projection name == attribute name?
				projection.refersToFk() && 
				projection.getColumn().getRefersTo().getName().equals(
						attribute.getPlainName()) &&
				projection.getColumn().getRefersTo().getParentTable().getName().equalsIgnoreCase(
						attribute.getParentObject().getPlainTypeString())) {
			return 1;
		} else {
			return 0;
		}
	}
}
