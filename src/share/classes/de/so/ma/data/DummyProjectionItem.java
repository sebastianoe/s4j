package de.so.ma.data;

public class DummyProjectionItem extends ProjectionItem {
	public DummyProjectionItem() {
		super(null, null);
	}

	@Override
	public int hashCode() {
		return -1;
	}
	
	@Override
	public boolean equals(Object obj) {
		return false;
	}
	
	@Override
	public String toString() {
		return "PI-Dummy";
	}
}
