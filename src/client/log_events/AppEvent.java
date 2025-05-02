package client.log_events;

public enum AppEvent {
    WRONG_CREDENTIALS("Credenziali errate", "ERR_CREDENTIALS", Level.WARNING),
    
    USERNAME_ALREADY_IN_USE("Username già in uso, reinserire le credenziali.", "USERNAME_ALREADY_USED", Level.WARNING),
    
    WEIRD_SETTING_USERHANDLER("Problema setting gu", "WEIRD_SETTING_USERHANDLER", Level.ERROR), 
        
    INVALID_CHOICE_ACTION("Inserire il numero dell'azione o ESC per uscire.", "INVALID_CHOICE_ACTION", Level.WARNING),
    
    VISITTYPE_NON_EXISTENT("Non esiste il tipo inserito, reinserisci i dati.", "VISITTYPE_NON_EXISTENT", Level.WARNING),
    
	NO_AVAILABILITY("I tipi di visita a te associati non richiedono nuove disponibilità "
			+ "o c'è un problema con l'archivio, contatta un configuratore.","NO_AVAILABILITY", Level.WARNING),
	
	ERROR_CATCHING_ACTIONS("Errore nel recupero del nome dell'azione.", "ERROR_CATCHING_ACTIONS", Level.ERROR),
	
	CHANGED_CREDENTIALS("Credenziali cambiate.", "CHANGED_CREDENTIALS", Level.INFO),
	
	NO_CONFIRMED_VISIT("Nessuna visita confermata.", "NO_CONFIRMED_VISIT", Level.INFO);
	
	
	
	;
	
	
    private final String message;
    private final String code;
    private final Level severity;
    
    AppEvent(String message, String code, Level severity) {
        this.message = message;
        this.code = code;
        this.severity = severity;
    }

    public String getMessage() { 
    	// return "[" + code + "]"; for resourcebundle
    	return message; 
    }
    
    public String getCode() { 
    	return code; 
    }

	public Level getSeverity() {
		return severity;
	}
}
