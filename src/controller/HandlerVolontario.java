package controller;

import main.App;

public class HandlerVolontario extends ControllerUtente {
	
	public HandlerVolontario(ControllerArchivio gdb, String username, App a) {
		this.gdb = gdb;
		this.username = username;
		if (checkPrimoAccesso()) primoAccesso(a);
	}
	
}
