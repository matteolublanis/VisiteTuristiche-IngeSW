package client.app;

import java.util.HashMap;

import client.controller_utente.ControllerUtente;
import client.login.Login;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ControllerMVC implements Observer {
	
	private Login gestoreLogin;
	private ControllerUtente controllerUtente;
	private App appUI;
	private Map<UiEventType, Consumer<UiEvent>> gestoriEvento = new HashMap<>();
	
	public ControllerMVC(App appUI, Login login) {
		this.appUI = appUI; 
		this.gestoreLogin = login; 
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
	}
                                
	private void start(UiEvent evento) {
		gestoreLogin.avvio();
	}
	
	private void stop(UiEvent evento) {
		gestoreLogin.stopConnection();
	}
	
	private void eseguiAzione(UiEvent evento) {
		
		String input = (String) evento.getPayload();
		
		try {
			int i = Integer.parseInt(input);
			boolean result = controllerUtente.eseguiAzione(String.valueOf(i - 1));
			if (result == false ) appUI.stop();
		}
		catch (NumberFormatException e) {
			boolean result = controllerUtente.eseguiAzione(input);
			if (result == false ) appUI.stop();
		}
		
	}
	
}
