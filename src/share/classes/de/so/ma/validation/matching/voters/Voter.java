package de.so.ma.validation.matching.voters;

import de.so.ma.data.ProjectionItem;
import de.so.ma.validation.matching.flatobject.FlatObjectAttribute;

public interface Voter {
	public Integer calculateVoting(ProjectionItem projection, FlatObjectAttribute attribute);
}
