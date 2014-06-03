package de.so.ma.metarepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.SQLException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.tools.javac.util.Log;

import de.so.ma.util.StateHelper;

public class MetaRepo {
	// static
	private static MetaRepo instance = null;
	
	public static void initializeInstance(String repoFileString, Log log) {
		if (!StateHelper.usingJtreg()) {
			System.out.println("Using meta repository " + repoFileString);
		}
		
		File repoFile = new File(repoFileString);
		
		try {
			instance = readFromJson(repoFile);
			instance.setLog(log);
			instance.init();
		} catch (FileNotFoundException e) {
			log.error("sql.metarepo.not.available");
		}
	}
	
	private static MetaRepo readFromJson(File repoFile) throws FileNotFoundException {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Reader jsonFileReader = new BufferedReader(new FileReader(repoFile));
		MetaRepo metaRepo = gson.fromJson(jsonFileReader, MetaRepo.class);
		
		return metaRepo;
	}
	
	public static MetaRepo getInstance() {
		return instance;
	}
	
	public static boolean isInitialized() {
		return instance != null;
	}
	
	public static void clearInstance() {
		instance = null;
	}
	
	// non-static stuff
	private transient Log log;
	private DB db;
	
	public void init() {
		// try to read database information from live connection
		if (db.getLiveConnection() != null) {
			try {
				db.initFromLiveConnection();
			} catch (SQLException e) {
				log.error("sql.metarepo.no.live.connection");
			}
		}
	}

	private void setLog(Log log) {
		this.log = log;
	}
	
	public DB getDB() {
		return db;
	}
}
