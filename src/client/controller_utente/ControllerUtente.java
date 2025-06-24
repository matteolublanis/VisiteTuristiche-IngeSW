package client.controller_utente;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import archivio.AmbitoManager;
import archivio.ArchivioFactory;
import archivio.CredenzialiManager;
import client.app.ViewInterface;
import client.app.UiEventType;
import client.app.UiRequest;
import client.log_events.AppEvent;
import utility.MethodName;
import java.util.concurrent.CompletableFuture;

public abstract class ControllerUtente{
	
	 protected AmbitoManager archivio;
	    protected CredenzialiManager credenzialiInfo;
	    protected String connectionCode;
	    protected ViewInterface view;
	    protected Map<String, CompletableFuture<Object>> richiesteInCorso = new HashMap<>();
	    
	    public ControllerUtente(int tipoApp, ViewInterface view) {
	        this.archivio = ArchivioFactory.createAmbitoManager(tipoApp);
	        this.credenzialiInfo = ArchivioFactory.createCredenzialiManager(tipoApp);
	        this.view = view;
	    }
	    
	    // Metodi per interagire con la view senza accoppiamento diretto
	    protected String richiediInput(String messaggio) {
	        String requestId = UUID.randomUUID().toString();
	        UiRequest request = new UiRequest(UiEventType.RICHIEDI_INPUT, messaggio, requestId);
	        
	        CompletableFuture<Object> future = new CompletableFuture<>();
	        richiesteInCorso.put(requestId, future);
	        
	        view.richiediInput(request);
	        
	        try {
	            return (String) future.get(); // Bloccante fino alla risposta
	        } catch (Exception e) {
	            return null;
	        }
	    }
	    
	    protected boolean chiediSiNo(String messaggio) {
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
	    
	    protected String richiediDataValida(String messaggio) {
	        String requestId = UUID.randomUUID().toString();
	        UiRequest request = new UiRequest(UiEventType.RICHIEDI_DATA_VALIDA, messaggio, requestId);
	        
	        CompletableFuture<Object> future = new CompletableFuture<>();
	        richiesteInCorso.put(requestId, future);
	        
	        view.richiediInput(request);
	        
	        try {
	            return (String) future.get();
	        } catch (Exception e) {
	            return null;
	        }
	    }
	    
	    protected int richiediInt(String messaggio) {
	        String requestId = UUID.randomUUID().toString();
	        UiRequest request = new UiRequest(UiEventType.RICHIEDI_INT, messaggio, requestId);
	        
	        CompletableFuture<Object> future = new CompletableFuture<>();
	        richiesteInCorso.put(requestId, future);
	        
	        view.richiediInput(request);
	        
	        try {
	            return (Integer) future.get();
	        } catch (Exception e) {
	            return 0;
	        }
	    }
	    
	    // Metodi per visualizzazione tramite view
	    protected void mostraMessaggio(String messaggio) {
	        view.mostraMessaggio(messaggio);
	    }
	    
	    protected void catchEvent(AppEvent evento) {
	        view.mostraMessaggio(evento.getMessage());
	    }
	    
	    protected void catchEvent(AppEvent evento, String attachment) {
	        view.mostraMessaggio(evento.getMessage(attachment));
	    }
	    
	    // Gestione risposta asincrona
	    public void gestisciRispostaInput(String requestId, Object risultato) {
	        CompletableFuture<Object> future = richiesteInCorso.remove(requestId);
	        if (future != null) {
	            future.complete(risultato);
	        }
	    }
	    
	    // Resto dei metodi rimane uguale
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
	                mostraMessaggio("Errore nel caricamento dell'azione: " + azioniDisponibili.get(i).getName());
	            }
	        }

	        return nomiAzioni;
	    }
	    
	    public boolean eseguiAzione(String input) {
	        try {
	            List<Method> azioniDisponibili = getAzioniDisponibili();
	            int scelta = Integer.parseInt(input);
	            if (scelta >= 0 && scelta < azioniDisponibili.size()) {
	                Method metodo = azioniDisponibili.get(scelta);
	                metodo.invoke(this);
	            } else {
	                catchEvent(AppEvent.INVALID_CHOICE_ACTION);
	            }
	        } catch (NumberFormatException e) { 
	            if (input.equalsIgnoreCase("esc")) return false;
	            else catchEvent(AppEvent.INVALID_CHOICE_ACTION);
	        } catch (Exception e) {
	            System.err.println(e);
	            return false;
	        }
	        
	        return true;
	    }
	    
	    public boolean checkIfUserExists(String username) {
	        return credenzialiInfo.checkIfUsernameExists(username);
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
