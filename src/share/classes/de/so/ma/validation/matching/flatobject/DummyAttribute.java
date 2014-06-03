package de.so.ma.validation.matching.flatobject;

public class DummyAttribute extends FlatObjectAttribute {

	public DummyAttribute() {
		super(null, null, null);
	}

	@Override
	public int getCardinality() {
		return 1;
	}

	@Override
	public String toString() {
		return "DA";
	}
	
	

}
