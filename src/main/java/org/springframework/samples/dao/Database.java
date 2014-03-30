package org.springframework.samples.dao;

public class Database {
	
	Database database = new Database();
	
	private Database () {};
	
	public Database getInstance() {
		return this.database;
	}
	
	public void connect() {
		
	}
	
	public void disconnect() {
		
	}

}
