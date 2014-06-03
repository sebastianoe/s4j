package de.so.ma.exceptions;

import java.util.Set;

public class NoIdAssignmentException extends SqlTransformationException {
	private static final long serialVersionUID = 3604464727827613065L;

	private final Set<String> subObjects;
	
	public NoIdAssignmentException(Set<String> subObjects) {
		this.subObjects = subObjects;
	}

	public Set<String> getSubObjects() {
		return subObjects;
	}
}
