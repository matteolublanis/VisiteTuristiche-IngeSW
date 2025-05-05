package client.login;

import archivio.AppManager;
import archivio.CredenzialiManager;
import archivio.UserInfoManager;
import client.app.App;
import client.controller_utente.ControllerUtente;
import client.controller_utente.ControllerUtenteFactory;
import client.log_events.AppEvent;
import utility.Credenziali;

public class Login {
	
	private App a;
	private UserInfoManager userManager;
	private AppManager appManager;
	private CredenzialiManager credenzialiManager;
	private int tipoApp;
	
	//Precondizione> archivio != null
	public Login(App a, int tipoApp) {
 		this.a = a;
 		linkWithArchive(tipoApp);
 		this.tipoApp = tipoApp;
 	}
 	
 	private void linkWithArchive(int tipoApp) {
 		//archivio = ArchivioFactory.createArchivio(tipoApp);
 		
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
				inviaCredenziali(credenziali);
				break;
			}
		} while (b);
		
	}
	
	public void inviaCredenziali(Credenziali credenziali) {
		configureHandlerUtente(credenzialiManager.makeConnection(credenziali));
	}
	//Precondizione: a != null
	//Postcondizione: fruitore registrato
	public void registrazione() {
		do {
			Credenziali credenziali = a.richiediCredenziali();
			if (credenziali == null) avvio();
			if (checkUsernameGiaPresente(credenziali.getUsername()))  a.catchEvent(AppEvent.USERNAME_ALREADY_IN_USE); 
			else {
				inviaCredenziali(credenziali);
				break;
			}
		} while (true);
	}
	
	public boolean checkPrimoAvvio() {
		return appManager.checkPrimoAvvio();
	}
	
	public void avvio () {
		if (checkPrimoAvvio()) { 
			a.viewLogin(credenzialiManager.getCredenzialiIniziali());
		}
		else {
			a.viewLogin(null);
		}
	}
	//Precondizione: username != null
	private boolean checkUsernameGiaPresente(String username) {
		return credenzialiManager.checkIfUserExists(username);
	}
	//Precondizione: c != null
	private boolean checkCredenzialiCorrette(Credenziali c) {
		return credenzialiManager.checkCredenzialiCorrette(c);
	}
	
	//Precondizione: username != null && username in Archivio && a != null
	private void configureHandlerUtente (String connectionCode){
		if (connectionCode == null) {
			a.catchEvent(AppEvent.WEIRD_SETTING_USERHANDLER);
			return;
		}
		int tipoUtente = userManager.getTipoLinkato(connectionCode);
		ControllerUtente gu = ControllerUtenteFactory.createControllerUtente(tipoUtente, tipoApp,a, connectionCode);
		a.setGu(gu);
		gu.checkPrimoAccesso();
		
	}
}
