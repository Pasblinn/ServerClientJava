package aa;

import java.sql.Connection;

public class Model {
	public static Model model;
	private final Database database;
	
	private Model() {
		this.database = new Database();
	}
	
	public static synchronized Model getInstance() {
		if (model == null) {
			model = new Model();
		}
		return model;
	}
	
	public Database getDatabase() {
		return database;
	}
}
