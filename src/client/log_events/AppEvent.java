package client.log_events;

public enum AppEvent { //TODO ResourceBundle per traduzioni
    WRONG_CREDENTIALS("Credenziali errate", "ERR_CREDENTIALS", Level.WARNING),
    
    USERNAME_ALREADY_IN_USE("Username già in uso, reinserire le credenziali.", "USERNAME_ALREADY_USED", Level.WARNING),
    
    WEIRD_SETTING_USERHANDLER("Problema setting gu", "WEIRD_SETTING_USERHANDLER", Level.ERROR), 
        
    INVALID_CHOICE_ACTION("Inserire il numero dell'azione o ESC per uscire.", "INVALID_CHOICE_ACTION", Level.WARNING),
    
    VISITTYPE_NON_EXISTENT("Non esiste il tipo inserito, reinserisci i dati.", "VISITTYPE_NON_EXISTENT", Level.WARNING),
    
	NO_AVAILABILITY("I tipi di visita a te associati non richiedono nuove disponibilità."
			+ "o c'è un problema con l'archivio, contatta un configuratore.","NO_AVAILABILITY", Level.WARNING),
	
	ERROR_CATCHING_ACTIONS("Errore nel recupero del nome dell'azione.", "ERROR_CATCHING_ACTIONS", Level.ERROR),
	
	CHANGED_CREDENTIALS("Credenziali cambiate.", "CHANGED_CREDENTIALS", Level.INFO),
	
	NO_CONFIRMED_VISIT("Nessuna visita confermata.", "NO_CONFIRMED_VISIT", Level.INFO),
	
	NOT_INSERTED_DISPONIBILITY("La tua disponibilità non è stata inserita, assicurati che sia una data corretta.", "NOT_INSERTED_DISPONIBILITY", Level.INFO),
	
	INSERTED_DISPONIBILITY("La tua disponibilità è stata inserita.", "INSERTED_DISPONIBILITY", Level.INFO),
	
	CANT_ADD_DISPONIBILITY("Non puoi al momento comunicare le tue disponibilità.", "CANT_ADD_DISPONIBILITY", Level.INFO),
	
	NO_VISIT("Nessuna visita presente.", "NO_VISIT", Level.INFO),
	
	NO_VISIT_ON_DAY("Nessuna visita segnata per questo giorno.", "NO_VISIT_ON_DAY", Level.INFO),
	
	RESERVATION_REMOVED("Prenotazione rimossa.", "RESERVATION_REMOVED", Level.INFO),
	
	RESERVATION_NOT_REMOVED("Prenotazione non rimossa", "RESERVATION_NOT_REMOVED", Level.WARNING),
	
	WRONG_RESERVATION_CODE("Il codice inserito non è legato a nessuna prenotazione, reinserirlo.", "WRONG_RESERVATION_CODE", Level.INFO),
	
	NO_RESERVATION_MADE("Non hai prenotazioni.", "NO_RESERVATION_MADE", Level.INFO),
	
	RESERVATION_INSERTED("Prenotazione inserita, codice prenotazione: ", "RESERVATION_INSERTED", Level.INFO),
	
	RESERVATION_NOT_INSERTED("Prenotazione non inserita.", "RESERVATION_NOT_INSERTED", Level.WARNING),
	
	CANT_ADD_RESERVATION_ON_DAY("Non è possibile prenotare per questo giorno.", "CANT_ADD_RESERVATION_ON_DAY", Level.INFO),
	
	STARTING_ADDING_PLACES_NEW_TERRITORY("Inizio fase creazione luoghi dell'ambito territoriale.", "STARTING_ADDING_PLACES_NEW_TERRITORY", Level.INFO),
	
	NEW_VOLUNTEER_INSERTED("Inserito nuovo volontario.", "NEW_VOLUNTEER_INSERTED", Level.INFO),
	
	NOT_INSERTED_VOLUNTEER("Non è stato inserito il nuovo volontario, username già in uso.", "NOT_INSERTED_VOLUNTEER", Level.INFO),
	
	STARTING_CREATING_VOLUNTEER("Inserisci le credenziali del nuovo volontario.", "STARTING_CREATING_VOLUNTEER", Level.INFO),
	
	PLACE_REMOVED_SUCCESFULLY("Luogo rimosso con successo, controllare conseguenze.", "PLACE_REMOVED_SUCCESFULLY", Level.INFO),
	
	PLACE_NOT_REMOVED("Luogo non rimosso, controllare di aver inserito i dati correttamente.", "PLACE_NOT_REMOVED", Level.INFO),
	
	VOLUNTEER_REMOVED("Volontario rimosso con successo, controllare conseguenze.", "VOLUNTEER_REMOVED", Level.INFO),
	
	VOLUNTEER_NOT_REMOVED("Volontario non rimosso, controllare di aver inserito i dati correttamente.", "VOLUNTEER_NOT_REMOVED", Level.INFO),
	
	VISIT_TYPE_REMOVED("Tipo visita rimosso con successo, controllare conseguenze.", "VISIT_TYPE_REMOVED", Level.INFO),
	
	VISIT_TYPE_NOT_REMOVED("Tipo visita non rimosso, controllare di aver inserito i dati correttamente.", "VISIT_TYPE_NOT_REMOVED", Level.INFO),
	
	NOT_VALID_VALUE("Valore inserito non valido.", "NOT_VALID_VALUE", Level.INFO),
	
	MAX_VALUE_SET("Modificato valore max prenotazione.","MAX_VALUE_SET", Level.INFO),
	
	MAX_VALUE_NOT_SET("Valore max prenotazione non modificato.","MAX_VALUE_NOT_SET", Level.WARNING),
	
	SCHEDULE_PUBLISHED("Piano pubblicato.", "SCHEDULE_PUBLISHED", Level.INFO),
	SCHEDULE_NOT_PUBLISHED("Piano non pubblicato.", "SCHEDULE_NOT_PUBLISHED", Level.WARNING),
	
	CANT_PUBLISH_NOW("Non è possibile pubblicare adesso il piano.","CANT_PUBLISH_NOW", Level.INFO),
	
	PROJECT_STARTED("Applicazione ufficialmente aperta.", "PROJECT_STARTED", Level.INFO),
	
	CANT_ADD_OR_REMOVE("Al momento non puoi aggiungere o rimuovere nulla.", "CANT_ADD_OR_REMOVE", Level.INFO),
	
	PRECLUDED_DATE_INSERTED("La data preclusa è stata inserita.", "PRECLUDED_DATE_INSERTED", Level.INFO),
	
	PRECLUDED_DATE_NOT_INSERTED("La data preclusa non è stata inserita, assicurarsi che sia nel periodo corretto o di aver pubblicato l'app.", "PRECLUDED_DATE_NOT_INSERTED", Level.INFO),
	
	DISPONIBILITIES_OPENED("La raccolta delle disponibilità è stata aperta.", "DISPONIBILITES_OPENED", Level.INFO),
	
	DISPONIBILITIES_NOT_OPENED("La raccolta delle disponibilità non è stata aperta, bisogna prima pubblicare il piano.","DISPONIBILITES_NOT_OPENED", Level.INFO),
	
	DISPONIBILITIES_CLOSED("La raccolta delle disponibilità è stata chiusa.", "DISPONIBILITIES_CLOSED", Level.INFO),
	
	DISPONIBILITIES_NOT_CLOSED("La raccolta delle disponibilità non è stata chiusa, non è ancora il momento.", "DISPONIBILITIES_NOT_CLOSED", Level.INFO),
	
	TAG_ALREADY_EXISTS("Il tag inserito esiste già.", "TAG_ALREADY_EXISTS", Level.INFO),
	
	
	
	;
	
	
    private final String message;
    private final String code;
    private final Level severity;
    
    AppEvent(String message, String code, Level severity) {
        this.message = message;
        this.code = code;
        this.severity = severity;
    }
    
    public String getMessage(String attachment) {
    	return (message + " " + attachment);
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
