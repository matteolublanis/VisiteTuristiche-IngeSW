package client.controller_utente;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import archivio.AmbitoManager;
import archivio.ArchivioFactory;
import archivio.CredenzialiManager;
import client.app.App;
import client.app.AzioniProvider;
import client.log_events.AppEvent;
import utility.MethodName;

public abstract class ControllerUtente implements AzioniProvider {
	
	protected AmbitoManager archivio;
	protected CredenzialiManager credenzialiInfo;
	protected String connectionCode;
	protected App a;
	
	public ControllerUtente (int tipoApp) {
		this.archivio = ArchivioFactory.createAmbitoManager(tipoApp);
		this.credenzialiInfo = ArchivioFactory.createCredenzialiManager(tipoApp);
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
	        	a.log("Incriminated method: " + azioniDisponibili.get(i).getName());
	        	a.catchEvent(AppEvent.ERROR_CATCHING_ACTIONS);
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
			if (scelta >= 0 && scelta < azioniDisponibili.size()) {
				Method metodo = azioniDisponibili.get(scelta);
				metodo.invoke(this);
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
		return credenzialiInfo.checkIfUsernameExists(username); //user info?
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
            		 && (Modifier.isPublic(metodo.getModifiers()))
            		 && !metodo.getName().equals("checkPrimoAccesso")) { 
             metodiConcreti.add(metodo);
         		}
    	}
         
        metodiConcreti.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));
    	return metodiConcreti;
    }
}
