package controller;

import user.*;
import utility.CostantiStruttura;

public class ControllerLogin {
	
	private ControllerArchivio gdb;
	private Credenziali c;
	
	public ControllerLogin(ControllerArchivio gdb) {
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
	
	public Utente effettuaLogin () {
		ControllerUtente gu = new ControllerUtente(gdb, c.getUsername());
		switch (gdb.effettuaLogin(c)) {
		case CostantiStruttura.CONFIGURATORE:
			return new Configuratore(c.getUsername(), gu);
		case CostantiStruttura.VOLONTARIO:
			return new Volontario(c.getUsername(), gu);
		case CostantiStruttura.FRUITORE:
			return new Fruitore(c.getUsername(), gu);
		case -1:
			System.out.println("Errore login");
			return null;
		}
		
		return null;
	}
}
