package de.so.ma.exceptions;

public class AmbiguousCoalesceException extends SqlTransformationException {
	private static final long serialVersionUID = -9121808751831631519L;

	private String coalesceExpression;
	private String type1;
	private String type2;
	
	public AmbiguousCoalesceException(String coalesceExpression, String type1, String type2) {
		this.coalesceExpression = coalesceExpression;
		this.type1 = type1;
		this.type2 = type2;
	}

	public String getCoalesceExpression() {
		return coalesceExpression;
	}

	public String getType1() {
		return type1;
	}

	public String getType2() {
		return type2;
	}
	
	
}
