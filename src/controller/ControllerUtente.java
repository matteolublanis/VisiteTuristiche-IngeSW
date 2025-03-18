package controller;

import java.lang.reflect.Method;
import java.util.ArrayList;

public abstract class ControllerUtente {
	
	protected ControllerArchivio gdb;
	protected String username;
	
	public ControllerUtente(ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
	}
	
	public boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(username);
	}
	
	public boolean checkCredenzialiCorrette(Credenziali c) {
		return gdb.checkCredenzialiCorrette(c);
	}
	
	public boolean cambiaCredenziali(String username, String password) {
		if (gdb.cambiaCredenziali(username, new Credenziali(username, password))) return true;
		return false;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	   public ArrayList<Method> getAzioniDisponibili() {
	        ArrayList<Method> metodiConcreti = new ArrayList<>();
	        
	        Method[] metodi = this.getClass().getDeclaredMethods();
	        
	        for (Method metodo : metodi) {
	            if (!metodo.getName().equals("getAzioniDisponibili")) {
	                metodiConcreti.add(metodo);
	            }
	        }

	        return metodiConcreti;
	    }
}
