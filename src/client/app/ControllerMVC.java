package client.app;

import java.util.AbstractMap;
import java.util.HashMap;

import client.controller_utente.ControllerUtente;
import client.login.Login;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ControllerMVC implements Observer {
	
    private ControllerUtente controllerUtente;
    private ViewInterface view;
    private Map<UiEventType, Consumer<UiEvent>> gestoriEvento = new HashMap<>();
    private Login gestoreLogin;
    
    public ControllerMVC(ViewInterface view) {
        this.view = view; 
        popolaHashMapEventi();
    }

    @Override
    public void update(UiEvent evento) {
        Consumer<UiEvent> handler = gestoriEvento.get(evento.getTipo());
        if (handler != null) {
            handler.accept(evento);
        } 
    }
    
    private void popolaHashMapEventi() {
        gestoriEvento.put(UiEventType.START, this::start);
        gestoriEvento.put(UiEventType.STOP, this::stop);
        gestoriEvento.put(UiEventType.ESEGUI_AZIONE, this::eseguiAzione);
        gestoriEvento.put(UiEventType.INPUT_RESPONSE, this::gestisciRispostaInput);
    }
                                
    private void start(UiEvent evento) {
    	if (gestoreLogin != null) {
            gestoreLogin.avvio();
            loopAzioni();
        }
    }
    
    private void loopAzioni() {
        if (controllerUtente != null) {
            view.mostraElencoAzioni(controllerUtente.getAzioniDisponibiliConNomi());
        }
    }
    
    private void stop(UiEvent evento) {
    	if (gestoreLogin != null) {
            gestoreLogin.stopConnection();
        }
    }
    
    private void eseguiAzione(UiEvent evento) {
        String input = (String) evento.getPayload();
        
        try {
            int i = Integer.parseInt(input);
            boolean result = controllerUtente.eseguiAzione(String.valueOf(i - 1));
            if (!result) {
                view.stop();
            } else {
                loopAzioni();
            }
        } catch (NumberFormatException e) {
            boolean result = controllerUtente.eseguiAzione(input);
            if (!result) {
                view.stop();
            } else {
                loopAzioni();
            }
        }
    }
    
    private void gestisciRispostaInput(UiEvent evento) {
        @SuppressWarnings("unchecked")
        AbstractMap.SimpleEntry<String, Object> entry = 
            (AbstractMap.SimpleEntry<String, Object>) evento.getPayload();
        
        String requestId = entry.getKey();
        Object risultato = entry.getValue();
        
        // Invia la risposta al controller utente appropriato
        if (controllerUtente != null) {
            controllerUtente.gestisciRispostaInput(requestId, risultato);
        }
        
        // Invia la risposta anche al login se necessario
        if (gestoreLogin != null) {
            gestoreLogin.gestisciRispostaInput(requestId, risultato);
        }
    }
    
    public void setControllerUtente(ControllerUtente controllerUtente) {
        this.controllerUtente = controllerUtente;
    }
    
    public void setLogin(Login login) {
        this.gestoreLogin = login;
    }
    
}
