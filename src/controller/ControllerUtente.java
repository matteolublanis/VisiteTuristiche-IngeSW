package controller;

import user.Credenziali;
import utility.CostantiStruttura;
import main.AppCntrl;
import user.*;

public class ControllerUtente {
	
	private AppCntrl app;
	private Utente user; 
	private ControllerArchivio gdb;	

	public ControllerUtente (ControllerArchivio gdb) {
		this.gdb = gdb;
	}
	
	public void setAppCntrl (AppCntrl app) {
		this.app = app;
	}
	
	public String comunicaAzioniDisponibili () { 
		
		
		return "";
	}
	
	public String comunicaCredenzialiIniziali () {
		return gdb.comunicaCredenzialiIniziali();
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
		return gdb.checkCredenziali(c, this); 
	}
	
	public void cambiaCredenziali (Credenziali c) { 
		gdb.cambiaCredenziali(this, c);
	} 
	
	public void setUser(String username, int tipo) { 
		switch (tipo) {
		case 1:
			user = new Configuratore(username, CostantiStruttura.CONFIGURATORE, this);
			break;
		case 2:
			user = new Volontario(username, CostantiStruttura.VOLONTARIO, this);
			break;
		case 3:
			user = new Fruitore(username, CostantiStruttura.FRUITORE, this);
			break;
		}
	}
	
	public void setUsername(String username) {
		user.setUsername(username);
	}
	
	public String getUsername() {
		return user.getUsername();
	}
	
	public void visualListaUser(int tipo_user) {
		 app.stampa(gdb.getListaUser(tipo_user));
	}
	
	public void getElencoLuoghiVisitabili() {
		app.stampa(gdb.getElencoLuoghiVisitabili());
	}
	
	public void getElencoTipiVisiteLuogo() {
		app.stampa(gdb.getElencoTipiVisiteLuogo());
	}
	
	public void pubblicaPiano() {
		//TODO ControllerArchivio deve pubblica il nuovo piano
	}

}
