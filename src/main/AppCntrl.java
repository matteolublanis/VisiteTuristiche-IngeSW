package main;

import database.Database;

public class AppCntrl {
	
	private Database database;
	
	public AppCntrl() {
		this.database = new Database();
	}
	
	public void avviaApplicazione() {
		//da implementare
	}
	
	public void accesso(String username, String password) {
		if (database.usernameEsiste(username)) {
			if(database.passwordCorretta(username, password)) {
				if(database.primoAccessoEseguito(null))
			}
		}
	}
}
