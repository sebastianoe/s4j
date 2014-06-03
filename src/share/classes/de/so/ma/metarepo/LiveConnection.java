package de.so.ma.metarepo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiveConnection {
	private String url;
	private String user;
	private String password;

	public Connection getDBConnection() throws SQLException {
		Connection conn = DriverManager.getConnection(url, user, password);

		return conn;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

}
