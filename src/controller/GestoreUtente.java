package controller;

import user.Credenziali;
import user.*;

public class GestoreUtente {

	private Utente user; //uso classe utente, determina cosa posso fare e info personali
	private GestoreArchivio gdb;	
	
	public GestoreUtente (GestoreArchivio gdb) {
		this.gdb = gdb;	
	}
	
	public String comunicaAzioniDisponibili () { //arraylist di string?
	
		/*
		 * Questo metodo controlla il tipo dell'utente -> GestoreArchivio
		 * Comunica una stringa (potremo modificare) con dentro la lista d'azioni disponibili
		 * Fondamentale il controllo del tipo utente nel gestore archivio quando comunicheremo
		 * l'azione voluta
		 */
		
		return "";
	}
	
	public String comunicaCredenzialiIniziali () {
		return gdb.comunicaCredenzialiIniziali();
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
			user = new Configuratore(username);
			break;
		case 2:
			user = new Volontario(username);
			break;
		case 3:
			user = new Fruitore(username);
			break;
		}
	}
	
	public void setUsername(String username) {
		user.setUsername(username);
	}
	
	public String getUsername() {
		return user.getUsername();
	}

}
