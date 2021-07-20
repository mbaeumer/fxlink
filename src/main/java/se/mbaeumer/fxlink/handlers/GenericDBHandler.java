package se.mbaeumer.fxlink.handlers;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class GenericDBHandler {
	private static final GenericDBHandler INSTANCE = new GenericDBHandler();

	private GenericDBHandler() {}

	public static GenericDBHandler getInstance() {
		return INSTANCE;
	}

	private Connection connection;
	
	public Connection getConnection() {
		if (this.connection != null){
			return connection;
		}
		return this.connect();
	}

	
	/**
	 * Connects to the database
	 * 
	 * @return
	 */
	public Connection connect() {
		File f = new File("db/fxlink");
		try {
			System.out.println("CONNECTING TO: " + f.getCanonicalPath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			try {
				try {
					Class.forName("org.hsqldb.jdbcDriver").newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			this.connection = DriverManager.getConnection(
					"jdbc:hsqldb:file:db/fxlink", "SA", "");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return this.connection;
	}

	/**
	 * Disconnects from the database
	 */
	public void disconnect() {
		try {
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}