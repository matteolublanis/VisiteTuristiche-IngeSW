package controller;

import user.Credenziali;
import utility.CostantiStruttura;
import main.App;
import user.*;

public class ControllerUtente {
	
	private App app;
	private Utente user; 
	private ControllerArchivio gdb;	

	public ControllerUtente (ControllerArchivio gdb) {
		this.gdb = gdb;
	}
	
	public void setApp (App app) {
		this.app = app;
	}
	
	//TODO discuterne con gli altri
	public void primaConfigurazione () {
		((Configuratore)user).impostaAmbitoTerritoriale();
		((Configuratore)user).modificaMaxPrenotazione();
		gdb.setPrimaConfigurazione();
	}
	
	public void impostaAmbitoTerritoriale(String msg, int tipo) {
		String s = (String) richiediVal(msg, tipo);
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	public void modificaMaxPrenotazione(String msg, int tipo) {
		int maxPrenotazione = (int) richiediVal(msg, tipo);
		gdb.modificaMaxPrenotazione(maxPrenotazione);
	}
	
	public String comunicaAzioniDisponibili () { 
		return (user.metodiEseguibili()); 
	}
	
	public void eseguiMetodo (String method) {
		user.eseguiMetodo(method);
	}
	
	public String comunicaCredenzialiIniziali () {
		return (gdb.comunicaCredenzialiIniziali());
	}
	
	public Object richiediVal(String msg, int tipo) {
		return app.richiediVal(msg, tipo);
	}
	
	public boolean checkPrimoAvvio () {
		return gdb.checkPrimoAvvio();
	}
	
	public boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(user.getUsername());
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		return gdb.checkCredenzialiCorrette(c); 
	}
	
	public boolean effettuaLogin (Credenziali c) {
		switch (gdb.effettuaLogin(c)) {
		case CostantiStruttura.CONFIGURATORE:
			user = new Configuratore(c.getUsername(), this);
			return true;
		case CostantiStruttura.VOLONTARIO:
			user = new Volontario(c.getUsername(), this);
			return true;
		case CostantiStruttura.FRUITORE:
			user = new Fruitore(c.getUsername(), this);
			return true;
		case -1:
			System.out.println("Errore login");
			return false;
		}
		return false;
	}
	
	public boolean cambiaCredenziali (Credenziali c) { 
		return (gdb.cambiaCredenziali(user.getUsername(), c));
	} 
	
	public void setUsername(String username) {
		user.setUsername(username);
	}
	
	public String getUsername() {
		return user.getUsername();
	}
	
	public String visualListaUser(int tipo_user) {
		 return gdb.getListaUser(tipo_user);
	}
	
	public String getElencoLuoghiVisitabili() {
		return gdb.getElencoLuoghiVisitabili();
	}
	
	public String getElencoTipiVisiteLuogo() {
		return gdb.getElencoTipiVisiteLuogo();
	}
	
	public void pubblicaPiano() {
		
	}
	
	public void aggiungiTipoVisite() {
		
	}

}
