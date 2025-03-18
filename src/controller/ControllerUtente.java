package controller;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public abstract class ControllerUtente {
	
	protected ControllerArchivio gdb;
	protected String username;
	
	public ControllerUtente(ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
	}
	
	public int getTipoUtente () {
		return gdb.getTipoUtente(username);
	}
	
	public boolean checkPrimaConfigurazioneArchivio () {
		return gdb.checkPrimaConfigurazioneArchivio(username);
	}
	
	public boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(username);
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
	
    public List<Method> getAzioniDisponibili() {
    	/*
         ArrayList<Method> metodiConcreti = new ArrayList<>();
        
         Method[] metodi = this.getClass().getDeclaredMethods();
        
         for (Method metodo : metodi) {
             if (!metodo.getName().equals("getAzioniDisponibili")) { 
             metodiConcreti.add(metodo);
         		}
    	}
     
    	return metodiConcreti;
    	 */
    	return Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(metodo -> !metodo.getName().equals("getAzioniDisponibili")) //aggiungere altri controlli
                .toList();
    }
}
