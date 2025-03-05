package controller;

import user.Credenziali;

public class GestoreUtente {
	
	private String username = "";
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
		return gdb.checkPrimoAccesso(username);
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		return gdb.checkCredenziali(c, this); 
	}
	
	public void cambiaCredenziali (Credenziali c) { 
		gdb.cambiaCredenziali(this, c);
	} 
	
	public void setUsername(String username) { 
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}

}
