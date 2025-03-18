package controller;

import utility.CostantiStruttura;

public class Login {
	
	private ControllerArchivio gdb;
	
	public Login(ControllerArchivio gdb) {
		this.gdb = gdb;
	}
	
	public ControllerUtente accesso(String username, String password) {
		if (checkPrimoAvvio()) System.out.println(getCredenzialiIniziali());
		Credenziali credenziali = new Credenziali(username, password);
		if(checkCredenzialiCorrette(credenziali)) {
			return configureHandlerUtente(credenziali);
		}
		else return null;
	}
	
	public String getCredenzialiIniziali() {
		if (checkPrimoAvvio()) return (gdb.getCredenzialiIniziali());
		else return "";
	}
	
	public boolean checkPrimoAvvio () {
		return gdb.checkPrimoAvvio();
	}
	
	private boolean checkCredenzialiCorrette(Credenziali c) {
		return gdb.checkCredenzialiCorrette(c);
	}
	
	private ControllerUtente configureHandlerUtente (Credenziali c){
		switch (gdb.effettuaLogin(c)) {
		case CostantiStruttura.CONFIGURATORE:
			return new HandlerConfiguratore(gdb, c.getUsername());
		case CostantiStruttura.VOLONTARIO:
			return new HandlerVolontario(gdb, c.getUsername());
		case CostantiStruttura.FRUITORE:
			return new HandlerFruitore(gdb, c.getUsername());
		default: 
			return null;

		}
	}
}
