package client.controller_utente;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import archivio.ArchivioFacade;
import client.app.App;
import client.log_events.AppEvent;
import utility.Credenziali;
import utility.MethodName;

public abstract class ControllerUtente {
	
	protected ArchivioFacade archivio;
	protected String connectionCode;
	protected App a;
	
	public ControllerUtente () {
		//i was made to love you, can't you tell?
	}
	
	public void checkPrimoAccesso() {
		if (archivio.checkPrimoAccesso(connectionCode)) primoAccesso();
		else ; //A livello grafico, potrebbe chiudere schermata di Login e aprire una nuova roba, il controller dà comando
	}
	
	//Precondizione: isPrimoAccesso == true
	//Post condizione: credenziali modificate
	protected void primoAccesso() {
		a.viewPrimoAccesso();
		do {
			Credenziali c = a.richiediCredenziali();
			if (cambiaCredenziali(c)) a.catchEvent(AppEvent.USERNAME_ALREADY_IN_USE);
			else break;
		} while (true);
		a.catchEvent(AppEvent.CHANGED_CREDENTIALS);
	}
	
	protected boolean cambiaCredenziali(Credenziali c) {
		return (archivio.cambiaCredenziali(connectionCode, c));
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
	        	a.catchEvent(AppEvent.ERROR_CATCHING_ACTIONS);
	        	a.log("Incriminated method: " + azioniDisponibili.get(i).getName());
	        }
	    }

	    return nomiAzioni;
	}
	
	/*
	 * Precondizione: input != null && azioniDisponibili != null
	 * @param String input, List<Method> azioni
	 * @throw Exception e Qui vengono catturate tutte le eccezioni all'interno del programma, da cambiare nella raffinatura
	 */
	public boolean eseguiAzione (String input) {
		try {
			List<Method> azioniDisponibili = getAzioniDisponibili();
			int scelta = Integer.parseInt(input);
			if (scelta > 0 && scelta <= azioniDisponibili.size()) {
				Method metodo = azioniDisponibili.get(scelta - 1);
				metodo.invoke(this, a);
			} 
			else {
				a.catchEvent(AppEvent.INVALID_CHOICE_ACTION);
			}
		} catch (NumberFormatException e) { 
			if (input.equalsIgnoreCase("esc")) return false;
			else a.catchEvent(AppEvent.INVALID_CHOICE_ACTION);
		}
		catch (Exception e) { //TODO gestione migliore delle eccezioni
			System.err.println(e);
			return false;
		}
		
		return true;
	}
	
	public boolean checkIfUserExists(String username) {
		return archivio.checkIfUserExists(username);
	}
	
	public boolean checkIfVisitTypeExists(String tipoVisita) {
	    return archivio.checkIfVisitTypeExists(tipoVisita);
	}
	
    private List<Method> getAzioniDisponibili() {
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
