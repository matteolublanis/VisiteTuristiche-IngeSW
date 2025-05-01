package controller;

import archivio.ArchivioFacade;
import archivio.ArchivioFactory;
import main.App;
import utility.CostantiStruttura;
import utility.Credenziali;

public class Login {
	
	private App a;
	private ArchivioFacade archivio;
	//Precondizione> archivio != null
	public Login(App a, int tipoApp) {
 		this.a = a;
 		linkWithArchive(tipoApp);
 	}
 	
 	private void linkWithArchive(int tipoApp) {
 		archivio = ArchivioFactory.createArchivio(tipoApp);
 		
 	}
	
	
	//Precondizione: a != null
	//Postcondizione: utente loggato
	public void accesso() {
		boolean b = true;
		do {
			Credenziali credenziali;
			a.view("Inserisci le tue credenziali:");
			String username = a.richiediInput("username");
			String password = a.richiediInput("password");
			credenziali = new Credenziali(username, password);
			if(!checkCredenzialiCorrette(credenziali)) a.view("Credenziali errate, reinserirle.");
			else {
				configureHandlerUtente(archivio.makeConnection(credenziali));
				break;
			}
		} while (b);
		
	}
	//Precondizione: a != null
	//Postcondizione: fruitore registrato
	public void registrazione() {
		Credenziali credenziali = null;
		do {
			a.view("Inserisci le tue nuove credenziali:");
			String username = a.richiediInput("username (ESC per tornare indietro)");
			if (username.equalsIgnoreCase("esc")) avvio();
			String password = a.richiediInput("password");
			if (checkUsernameGiaPresente(username)) a.view("Username già in uso, reinserire le credenziali.");
			else {
				credenziali = new Credenziali(username, password);
				configureHandlerUtente(archivio.makeConnection(credenziali));
				break;
			}
		} while (true);
	}
	
	public boolean checkPrimoAvvio() {
		return archivio.checkPrimoAvvio();
	}
	
	public void avvio () {
		if (checkPrimoAvvio()) { 
			Credenziali c = archivio.getCredenzialiIniziali();
			a.view("PRIMO AVVIO, CREDENZIALI INIZIALI\nUsername: " + c.getUsername() + "\nPassword: " + c.getPassword());
			accesso();
		}
		else {
			if (a.chiediSioNo("Vuoi registrarti come nuovo utente?")) {
				registrazione();
			}
			else {
				accesso();
			}
		}
	}
	//Precondizione: username != null
	private boolean checkUsernameGiaPresente(String username) {
		return archivio.checkIfUserExists(username);
	}
	//Precondizione: c != null
	private boolean checkCredenzialiCorrette(Credenziali c) {
		return archivio.checkCredenzialiCorrette(c);
	}
	//Precondizione: username != null && username in Archivio && a != null
	private void configureHandlerUtente (String connectionCode){
		ControllerUtente gu = null;
		switch (archivio.getTipoUtente(connectionCode)) {
		case CostantiStruttura.CONFIGURATORE:
			if (connectionCode != null) gu = new HandlerConfiguratore(archivio, a, connectionCode);
			a.setGu(gu);
			gu.checkPrimoAccesso();
			break;
		case CostantiStruttura.VOLONTARIO:
			if (connectionCode != null) gu = new HandlerVolontario(archivio, a, connectionCode);
			a.setGu(gu);
			gu.checkPrimoAccesso();
			break;
		case CostantiStruttura.FRUITORE:
			if (connectionCode != null) gu = new HandlerFruitore(archivio, a, connectionCode);
			a.setGu(gu);
			gu.checkPrimoAccesso();
			break;
		default: 
			a.view("Problema setting gu");
			return;
		}
	}
}
