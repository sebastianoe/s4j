package businessobjects;

public class Product2 {
	private long id;
	private String name;
	private Storage storage;
	private Storage anotherStorage;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	public Storage getAnotherStorage() {
		return anotherStorage;
	}

	public void setAnotherStorage(Storage anotherStorage) {
		this.anotherStorage = anotherStorage;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", storage=" + storage + "]";
	}

}
