package client.login;

import archivio.ArchivioFacade;
import archivio.ArchivioFactory;
import client.app.App;
import client.controller_utente.ControllerUtente;
import client.controller_utente.ControllerUtenteFactory;
import client.log_events.AppEvent;
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
			Credenziali credenziali = a.richiediCredenziali();
			if (credenziali == null) avvio();
			if(!checkCredenzialiCorrette(credenziali)) a.catchEvent(AppEvent.WRONG_CREDENTIALS);
			else {
				configureHandlerUtente(archivio.makeConnection(credenziali));
				break;
			}
		} while (b);
		
	}
	//Precondizione: a != null
	//Postcondizione: fruitore registrato
	public void registrazione() {
		do {
			Credenziali credenziali = a.richiediCredenziali();
			if (credenziali == null) avvio();
			if (checkUsernameGiaPresente(credenziali.getUsername()))  a.catchEvent(AppEvent.USERNAME_ALREADY_IN_USE); 
			else {
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
			a.viewLogin(archivio.getCredenzialiIniziali());
		}
		else {
			a.viewLogin(null);
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
		if (connectionCode == null) {
			a.catchEvent(AppEvent.WEIRD_SETTING_USERHANDLER);
			return;
		}
		int tipoUtente = archivio.getTipoLinkato(connectionCode);
		ControllerUtente gu = ControllerUtenteFactory.createControllerUtente(tipoUtente, archivio, a, connectionCode);
		a.setGu(gu);
		gu.checkPrimoAccesso();
		
	}
}
