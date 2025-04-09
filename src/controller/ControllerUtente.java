package controller;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import main.App;
import utility.Credenziali;

public abstract class ControllerUtente {
	
	protected ControllerArchivio gdb;
	protected String username;
	
	public ControllerUtente () {
		//i was made to love you, can't you tell?
	}
	
	public ControllerUtente(ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
	}
	
	protected int getTipoUtente () {
		return gdb.getTipoUtente(username);
	}
	
	protected boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(username);
	}
	
	//Precondizione: isPrimoAccesso == true
	//Post condizione: credenziali modificate
	protected void primoAccesso(App a) {
		a.view("Primo accesso eseguito.");
		boolean b = false;
		do {
			a.view("Cambia le tue credenziali:");
			String username = a.richiediInput("username");
			String password = a.richiediInput("password");
			Credenziali c = new Credenziali(username, password);
			b = cambiaCredenziali(c);
			if (!b) a.view("Credenziali non cambiate, username già presente.");
		} while (!b);
		a.view("Credenziali cambiate.");
	}
	
	protected int richiediIntMaggioreDiZero(App a, String messaggio) {
		int result = 0;
		do {
			result = a.richiediInt(messaggio);
			if (result < 1) a.view("Almeno uno.");
		} while (result < 1);
		return result;
	}
	
	protected String richiediVisitaEsistente(App a, String messaggio) {
	    String tipo;
	    do {
	        tipo = a.richiediInput(messaggio);
	        if (!gdb.checkIfVisitTypeExists(tipo)) {
	            a.view("Non esiste il tipo inserito, reinserisci i dati.");
	        }
	    } while (!gdb.checkIfVisitTypeExists(tipo));
	    return tipo;
	}
	
	protected boolean cambiaCredenziali(Credenziali c) {
		return (gdb.cambiaCredenziali(this.username, c, this));
	}
	
	protected void setUsername(String username) {
		this.username = username;
	}
	
	protected String getUsername() {
		return username;
	}
	
    public List<Method> getAzioniDisponibili() {
    	List<Method> metodiConcreti = new LinkedList<>();
        
         Method[] metodi = this.getClass().getDeclaredMethods();
        
         for (Method metodo : metodi) {
             if (!metodo.getName().equals("getAzioniDisponibili")
            		 && !metodo.getName().startsWith("lambda$")
            		 && (Modifier.isPublic(metodo.getModifiers()))) { 
             metodiConcreti.add(metodo);
         		}
    	}
         
        metodiConcreti.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));
    	return metodiConcreti;
    }
}
