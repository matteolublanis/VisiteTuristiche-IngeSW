package controller;

import utility.CostantiStruttura;

public class Login {
	
	private ControllerArchivio gdb;
	private Credenziali c;
	
	public Login(ControllerArchivio gdb) {
		this.gdb = gdb;
	}
	
	public void inserisciCredenziali (String user, String pw) {
		this.c = new Credenziali(user, pw);
	}
	
	public String getCredenzialiIniziali() {
		if (checkPrimoAvvio()) return (gdb.getCredenzialiIniziali());
		else return "";
	}
	
	public boolean checkPrimoAvvio () {
		return gdb.checkPrimoAvvio();
	}
	
	public boolean checkCredenzialiCorrette() {
		return gdb.checkCredenzialiCorrette(c);
	}
	
	public String getUsername() {
		return c.getUsername();
	}
	
	public ControllerUtente configureHandlerUtente (){
		switch (gdb.effettuaLogin(c)) {
		case CostantiStruttura.CONFIGURATORE:
			return new HandlerConfiguratore(gdb, getUsername());
		case CostantiStruttura.VOLONTARIO:
			return new HandlerVolontario(gdb, getUsername());
		case CostantiStruttura.FRUITORE:
			return new HandlerFruitore(gdb, getUsername());
		default: 
			return null;

		}
	}
}
