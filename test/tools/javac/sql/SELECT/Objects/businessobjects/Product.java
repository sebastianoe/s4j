package businessobjects;

import java.util.List;

public class Product {
	private long id;
	private String name;
	private Storage storage;
	// should be ignored and not be flattened
	private List<Storage> storageList;

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
	
	// should be ignored and not be flattened
	public List<Storage> getStorageList() {
		return storageList;
	}
	
	// should be ignored and not be flattened
	public void setStorageList(List<Storage> storageList) {
		this.storageList = storageList;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", storage=" + storage + "]";
	}

}
