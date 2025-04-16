package controller;

import main.App;
import utility.CostantiStruttura;
import utility.Credenziali;

public class Login {
	
	private ControllerArchivio gdb;
	
	public Login(ControllerArchivio gdb) {
		this.gdb = gdb;
	}
	
	public void accesso(App a) {
		Credenziali credenziali;
		boolean b = true;
		do {
			a.view("Inserisci le tue credenziali:");
			String username = a.richiediInput("username");
			String password = a.richiediInput("password");
			credenziali = new Credenziali(username, password);
			b = !checkCredenzialiCorrette(credenziali);
			if (b) a.view("Credenziali errate, reinserirle.");
		} while (b);
		configureHandlerUtente(credenziali.getUsername(), a);
	}
	
	public void registrazione(App a) {
		Credenziali credenziali = null;
		do {
			a.view("Inserisci le tue nuove credenziali:");
			String username = a.richiediInput("username (ESC per tornare indietro)");
			if (username.equalsIgnoreCase("esc")) avvio(a);
			String password = a.richiediInput("password");
			if (checkUsernameGiaPresente(username)) a.view("Username già in uso, reinserire le credenziali.");
			else {
				credenziali = new Credenziali(username, password);
				gdb.createNewFruitore(username, password);
				break;
			}
		} while (true);
		configureHandlerUtente(credenziali.getUsername(), a);
	}
	
	public boolean checkPrimoAvvio() {
		return gdb.checkPrimoAvvio();
	}

	public void avvio (App a) {
		if (checkPrimoAvvio()) { 
			Credenziali c = gdb.getCredenzialiIniziali();
			a.view("PRIMO AVVIO, CREDENZIALI INIZIALI\nUsername: " + c.getUsername() + "\nPassword: " + c.getPassword());
			accesso(a);
		}
		else {
			if (a.chiediSioNo("Vuoi registrarti come nuovo utente?")) {
				registrazione(a);
			}
			else {
				accesso(a);
			}
		}
	}
	
	private boolean checkUsernameGiaPresente(String username) {
		return gdb.checkIfUserExists(username);
	}
	
	private boolean checkCredenzialiCorrette(Credenziali c) {
		return gdb.checkCredenzialiCorrette(c);
	}
	
	private void configureHandlerUtente (String username, App a){
		ControllerUtente gu;
		switch (gdb.getTipoUtente(username)) {
		case CostantiStruttura.CONFIGURATORE:
			gu = new HandlerConfiguratore(gdb, username, a);
			gdb.addControllerUtente(gu, username);
			a.setGu(gu);
			gu.checkPrimoAccesso(a);
			break;
		case CostantiStruttura.VOLONTARIO:
			gu = new HandlerVolontario(gdb, username, a);
			gdb.addControllerUtente(gu, username);
			a.setGu(gu);
			if (gu.checkPrimoAccesso()) {
				gu.primoAccesso(a);
			}
			break;
		case CostantiStruttura.FRUITORE:
			gu = new HandlerFruitore(gdb, username);
			gdb.addControllerUtente(gu, username);
			a.setGu(gu);
			break;
		default: 
			a.view("Problema setting gu");
			return;
		}
	}
}
