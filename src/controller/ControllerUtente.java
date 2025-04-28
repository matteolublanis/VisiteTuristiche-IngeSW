package controller;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import main.App;
import utility.Credenziali;
import utility.MethodName;

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
	
	protected void checkPrimoAccesso(App a) {
		
	}
	
	protected boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(this);
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
			if (a.chiediSioNo("Confermi le nuove credenziali?")) {
				b = cambiaCredenziali(c);
				if (!b) a.view("Credenziali non cambiate, username già presente.");
			}
			else b = false;
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
	
	protected void setUsername(String username) {
		this.username = username;
	}
	
	protected boolean cambiaCredenziali(Credenziali c) {
		return (gdb.cambiaCredenziali(this, c));
	}
	
	public List<String> getAzioniDisponibiliConNomi() {
	    List<String> nomiAzioni = new ArrayList<>();
	    List<Method> azioniDisponibili = getAzioniDisponibili();

	    for (int i = 0; i < azioniDisponibili.size(); i++) {
	        try {
	            Method metodo = azioniDisponibili.get(i);
	            MethodName annotation = metodo.getAnnotation(MethodName.class);
	            String nomeAzione = annotation.value();
	            nomiAzioni.add(nomeAzione);
	        } catch (Exception e) {
	            nomiAzioni.add("Errore nel recupero del nome dell'azione");
	        }
	    }

	    return nomiAzioni;
	}
	
	/*
	 * Precondizione: input != null && azioniDisponibili != null
	 * @param String input, List<Method> azioni
	 * @throw Exception e Qui vengono catturate tutte le eccezioni all'interno del programma, da cambiare nella raffinatura
	 */
	public boolean eseguiAzione (String input, App a) {
		try {
			List<Method> azioniDisponibili = getAzioniDisponibili();
			int scelta = Integer.parseInt(input);
			if (scelta > 0 && scelta <= azioniDisponibili.size()) {
				Method metodo = azioniDisponibili.get(scelta - 1);
				metodo.invoke(this, a);
			} 
			else {
				a.view("Scelta non valida.");
			}
		} catch (NumberFormatException e) { 
			if (input.equalsIgnoreCase("esc")) return false;
			else a.view("Inserire il numero dell'azione o ESC per uscire.");
		}
		catch (Exception e) { //TODO gestione migliore delle eccezioni
			System.err.println(e);
			return false;
		}
		
		return true;
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
