package client.login;

import archivio.ArchivioFactory;
import archivio.CredenzialiManager;
import client.app.ViewInterface;
import client.app.ControllerMVC;
import client.controller_utente.ControllerUtenteFactory;
import client.log_events.AppEvent;
import utility.Credenziali;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.HashMap;
import java.util.Map;
import client.app.UiEventType;
import client.app.UiRequest;

public class Login {
	
	private ViewInterface view;
	private CredenzialiManager credenzialiManager;
	private ControllerMVC controllerMVC;
	private int tipoApp;
	private String connectionCode;
	private int tipoUtente;
	private Map<String, CompletableFuture<Object>> richiesteInCorso = new HashMap<>();
	
	public Login(ViewInterface view, int tipoApp, ControllerMVC controllerMVC) {
 		this.view = view;
 		this.controllerMVC = controllerMVC;
 		linkWithArchive(tipoApp);
 		this.tipoApp = tipoApp;
 	}
 	
	public void stopConnection() {
		if (connectionCode != null) {
			credenzialiManager.stopConnection(connectionCode);
		}
	}
	
 	private void linkWithArchive(int tipoApp) {
 		credenzialiManager = ArchivioFactory.createCredenzialiManager(tipoApp);
 	}

	// Metodi per interagire con la view senza accoppiamento diretto
    private Credenziali richiediCredenziali() {
        String requestId = UUID.randomUUID().toString();
        UiRequest request = new UiRequest(UiEventType.RICHIEDI_CREDENZIALI, "credenziali", requestId);
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        richiesteInCorso.put(requestId, future);
        
        view.richiediInput(request);
        
        try {
            return (Credenziali) future.get();
        } catch (Exception e) {
            return null;
        }
    }
    
    private boolean chiediSiNo(String messaggio) {
        String requestId = UUID.randomUUID().toString();
        UiRequest request = new UiRequest(UiEventType.RICHIEDI_SI_NO, messaggio, requestId);
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        richiesteInCorso.put(requestId, future);
        
        view.richiediInput(request);
        
        try {
            return (Boolean) future.get();
        } catch (Exception e) {
            return false;
        }
    }
    
    private void mostraMessaggio(String messaggio) {
        view.mostraMessaggio(messaggio);
    }
    
    private void catchEvent(AppEvent evento) {
        view.mostraMessaggio(evento.getMessage());
    }
    
    private void catchEvent(AppEvent evento, String attachment) {
        view.mostraMessaggio(evento.getMessage(attachment));
    }
    
    // Gestione risposta asincrona
    public void gestisciRispostaInput(String requestId, Object risultato) {
        CompletableFuture<Object> future = richiesteInCorso.remove(requestId);
        if (future != null) {
            future.complete(risultato);
        }
    }
	
	public void accesso() {
		boolean continua = true;
		do {
			Credenziali credenziali = richiediCredenziali();
			if (credenziali == null) {
				avvio();
				return;
			}
			if(!checkCredenzialiCorrette(credenziali)) {
				catchEvent(AppEvent.WRONG_CREDENTIALS);
			} else {
				inviaCredenziali(credenziali);
				break;
			}
		} while (continua);
	}
	
	public void inviaCredenziali(Credenziali credenziali) {
		configureHandlerUtente(credenzialiManager.makeConnection(credenziali));
	}
	
	public void registrazione() {
		do {
			Credenziali credenziali = richiediCredenziali();
			if (credenziali == null) {
				avvio();
				return;
			}
			if (checkUsernameGiaPresente(credenziali.getUsername())) {
				catchEvent(AppEvent.USERNAME_ALREADY_IN_USE);
			} else {
				inviaCredenziali(credenziali);
				break;
			}
		} while (true);
	}
	
	public boolean checkPrimoAvvio() {
		return credenzialiManager.checkPrimoAvvio();
	}
	
	public void avvio() {
		if (checkPrimoAvvio()) { 
			accesso();
		} else {
			if(chiediSiNo("Vuoi registrarti come nuovo utente?")) {
				registrazione();
			} else {
				accesso();
			}
		}
	}
	
	private boolean checkUsernameGiaPresente(String username) {
		return credenzialiManager.checkIfUsernameExists(username);
	}
	
	private boolean checkCredenzialiCorrette(Credenziali c) {
		return credenzialiManager.checkCredenzialiCorrette(c);
	}
	
	private void configureHandlerUtente(String connectionCode){
		if (connectionCode == null) {
			catchEvent(AppEvent.WEIRD_SETTING_USERHANDLER);
			return;
		}
		tipoUtente = credenzialiManager.getTipoLinkato(connectionCode);
		this.connectionCode = connectionCode;
		checkPrimoAccesso();
	}
	
	protected void checkPrimoAccesso() {
		if (credenzialiManager.checkPrimoAccesso(connectionCode)) {
			primoAccesso();
		} else {
			setupGestoreUtente();
		}
	}
	
	protected void primoAccesso() {
		mostraMessaggio("Primo accesso rilevato. È necessario cambiare le credenziali.");
		do {
			Credenziali c = richiediCredenziali();
			if (c == null) break;
			if (cambiaCredenziali(c)) {
				catchEvent(AppEvent.USERNAME_ALREADY_IN_USE);
			} else {
				break;
			}
		} while (true);
		catchEvent(AppEvent.CHANGED_CREDENTIALS);
		setupGestoreUtente();
	}
	
	private void setupGestoreUtente() {
		controllerMVC.setControllerUtente(
			ControllerUtenteFactory.createControllerUtente(tipoUtente, tipoApp, view, connectionCode)
		);
	}
	
	protected boolean cambiaCredenziali(Credenziali c) {
		return (credenzialiManager.cambiaCredenziali(connectionCode, c));
	}
}