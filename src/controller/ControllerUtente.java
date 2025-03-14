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
	
	public boolean cambiaCredenziali(Credenziali c) {
		if (gdb.cambiaCredenziali(username, c)) return true;
		return false;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	public ArrayList<Method> getAzioniDisponibili() {
		Method methods[] = this.getClass().getDeclaredMethods();
		
		ArrayList<Method> listaMetodi = new ArrayList<>();
        
        for (Method metodo : methods) {
            listaMetodi.add(metodo);
        }
        
        return listaMetodi;
	}
	
}
