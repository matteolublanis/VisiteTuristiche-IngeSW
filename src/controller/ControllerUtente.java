package controller;

import java.lang.reflect.Method;
import java.util.LinkedList;

public abstract class ControllerUtente {
	
	protected ControllerArchivio gdb;
	protected String username;
	
	public ControllerUtente () {
		//do something
	}
	
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
		if (gdb.cambiaCredenziali(this.username, new Credenziali(username, password))) return true;
		return false;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
    public LinkedList<Method> getAzioniDisponibili() {
    	
    	LinkedList<Method> metodiConcreti = new LinkedList<>();
        
         Method[] metodi = this.getClass().getDeclaredMethods();
        
         for (Method metodo : metodi) {
             if (!metodo.getName().equals("getAzioniDisponibili")
            		 && !metodo.getName().startsWith("lambda$")) { 
             metodiConcreti.add(metodo);
         		}
    	}
         
        metodiConcreti.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));
    	return metodiConcreti;
    	/*
    	 * 
    	Metodo più generico per usare più strutture, serve però ordine quindi usiamo linkedlist
    	return Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(metodo -> !metodo.getName().equals("getAzioniDisponibili")) //aggiungere altri controlli
                .toList();
                
        */
    }
}
