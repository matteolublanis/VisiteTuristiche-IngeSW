package client.controller_utente;

import java.util.ArrayList;
import java.util.List;
import archivio.AppManager;
import archivio.ArchivioFactory;
import archivio.UserInfoManager;
import client.app.ViewInterface;
import client.app.UiEventType;
import client.app.UiRequest;
import client.log_events.AppEvent;
import dto.LuogoDTO;
import dto.TipoVisitaDTO;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.MethodName;
import utility.Time;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HandlerConfiguratore extends ControllerUtente {
    
    private AppManager appPlan;
    private UserInfoManager userInfo;
    
    public HandlerConfiguratore(ViewInterface view, String connectionCode, int tipoApp) {
        super(tipoApp, view);
        this.connectionCode = connectionCode;
        userInfo = ArchivioFactory.createUserInfoManager(tipoApp);
        appPlan = ArchivioFactory.createAppManager(tipoApp);
        doPrimaConfigurazioneIfNecessaria();
    }
    
    private void doPrimaConfigurazioneIfNecessaria() {
        if (checkPrimaConfigurazioneArchivio()) configuraArchivio();
    }
    
    private boolean checkPrimaConfigurazioneArchivio() {
        return archivio.checkPrimaConfigurazioneArchivio(connectionCode);
    }
    
    private void configuraArchivio() {
        String ambito = null;
        do {
            ambito = richiediInput("nome ambito territoriale");
        } while (!chiediSiNo("Confermi di voler chiamare l'ambito " + ambito + " ?"));
        impostaAmbitoTerritoriale(ambito);
        modificaMaxPrenotazione();
        catchEvent(AppEvent.STARTING_ADDING_PLACES_NEW_TERRITORY);
        aggiungiLuogo();
    }
    
    @MethodName("Rimuovi luogo")
    public void rimuoviLuogo() {
        if (canAddOrRemove()) {
            if (archivio.rimuoviLuogo(richiediInput("luogo da rimuovere"), connectionCode)) {
                catchEvent(AppEvent.PLACE_REMOVED_SUCCESFULLY);
            } else {
                catchEvent(AppEvent.PLACE_NOT_REMOVED);
            }
        }
    }
    
    @MethodName("Rimuovi volontario")
    public void rimuoviVolontario() {
        if (canAddOrRemove()) {
            if (userInfo.rimuoviVolontario(richiediInput("username del volontario da rimuovere"), connectionCode)) {
                catchEvent(AppEvent.VOLUNTEER_REMOVED);
            } else {
                catchEvent(AppEvent.VOLUNTEER_NOT_REMOVED);
            }
        }
    }
    
    @MethodName("Rimuovi tipo di visita")
    public void rimuoviTipo() {
        if (canAddOrRemove()) {
            if (archivio.rimuoviTipo(richiediInput("tipo da rimuovere"), connectionCode)) {
                catchEvent(AppEvent.VISIT_TYPE_REMOVED);
            } else {
                catchEvent(AppEvent.VISIT_TYPE_NOT_REMOVED);
            }
        }
    }
    
    private void impostaAmbitoTerritoriale(String s) {
        archivio.impostaAmbitoTerritoriale(s, connectionCode);
    }
    
    private boolean impostaMaxPrenotazione(int maxPrenotazione) {
        return archivio.modificaMaxPrenotazione(connectionCode, maxPrenotazione);
    }
    
    @MethodName("Modifica numero max prenotazione per fruitore")
    public void modificaMaxPrenotazione() {
        int max = 0;
        do {
            max = richiediInt("max prenotazione per fruitore");
            if (max < 1) {
                catchEvent(AppEvent.NOT_VALID_VALUE);
            } else if (chiediSiNo("Confermi?")) {
                break;
            }
        } while (true);
        
        if (impostaMaxPrenotazione(max)) {
            catchEvent(AppEvent.MAX_VALUE_SET);
        } else {
            catchEvent(AppEvent.MAX_VALUE_NOT_SET);
        }
    }
    
    @MethodName("Visualizza lista volontari")
    public void getListaVolontari() {
        view.mostraListaUtenti(userInfo.getListaUser(connectionCode, CostantiStruttura.VOLONTARIO));
    }
    
    @MethodName("Visualizza elenco luoghi visitabili")
    public void getElencoLuoghiVisitabili() {
        view.mostraListaLuoghi(archivio.getElencoLuoghiVisitabili(connectionCode));
    }
    
    @MethodName("Visualizza elenco tipi visite per luogo")
    public void getElencoTipiVisiteLuogo() {
        view.mostraListaLuoghi(archivio.getElencoTipiVisiteLuogo(connectionCode));
    }
    
    @MethodName("Pubblica il piano delle visite")
    public void pubblicaPianoVisite() {
        if (appPlan.isReleaseOrLaterDay(connectionCode)) {
            if (appPlan.isPrimaPubblicazione()) {
                catchEvent(AppEvent.PROJECT_STARTED);
                appPlan.pubblicaPiano(connectionCode);
            } else {
                if (appPlan.pubblicaPiano(connectionCode)) {
                    catchEvent(AppEvent.SCHEDULE_PUBLISHED);
                } else {
                    catchEvent(AppEvent.SCHEDULE_NOT_PUBLISHED);
                }
            }
        } else {
            catchEvent(AppEvent.CANT_PUBLISH_NOW);
        }
    }
    
    @MethodName("Indica date precluse del prossimo piano a quello successivo a questo")
    public void indicaDatePrecluse() {
        String data = null;
        do {
            data = richiediDataValida("data preclusa");
            if (chiediSiNo("Confermi di inserire " + data + " come data preclusa?")) {
                break;
            } else {
                return;
            }
        } while (true);
        
        if (archivio.indicaDatePrecluse(connectionCode, data)) {
            catchEvent(AppEvent.PRECLUDED_DATE_INSERTED);
        } else {
            catchEvent(AppEvent.PRECLUDED_DATE_NOT_INSERTED);
        }
    }
    
    @MethodName("Apri la raccolta delle disponibilità dei volontari")
    public void apriRaccoltaDisponibilita() {
        if (appPlan.apriRaccoltaDisponibilita(connectionCode)) {
            catchEvent(AppEvent.DISPONIBILITIES_OPENED);
        } else {
            catchEvent(AppEvent.DISPONIBILITIES_NOT_OPENED);
        }
    }
    
    @MethodName("Chiudi la raccolta delle disponibilità dei volontari")
    public void chiudiRaccoltaDisponibilita() {
        if (appPlan.chiudiRaccoltaDisponibilita(connectionCode)) {
            catchEvent(AppEvent.DISPONIBILITIES_CLOSED);
        } else {
            catchEvent(AppEvent.DISPONIBILITIES_NOT_CLOSED);
        }
    }
    
    private boolean aggiungiTipoVisitePartendoDaLuogo(String luogo) {
        TipoVisitaDTO tipoVisita = richiediTipoVisita(luogo);
        return archivio.aggiungiTipoVisite(tipoVisita, connectionCode);
    }
    
    @MethodName("Aggiungi tipo visite")
    public void aggiungiTipoVisite() {
        if (canAddOrRemove()) {
            String luogo = "";
            do {
                luogo = richiediInput("luogo della visita");
                if (!archivio.checkIfPlaceExists(luogo)) {
                    catchEvent(AppEvent.PLACE_DOESNT_EXIST);
                }
            } while (!archivio.checkIfPlaceExists(luogo));
            
            catchEvent(aggiungiTipoVisitePartendoDaLuogo(luogo) ? 
                AppEvent.VISIT_TYPE_ADDED : AppEvent.VISIT_TYPE_NOT_ADDED);
        }
    }
    
    @MethodName("Aggiungi credenziali nuovo configuratore")
    public void impostaCredenzialiNuovoConfiguratore() {
        Credenziali c = richiediCredenziali();
        if (credenzialiInfo.impostaCredenzialiNuovoConfiguratore(connectionCode, c)) {
            catchEvent(AppEvent.NEW_CONFIGURATOR_ADDED);
        } else {
            catchEvent(AppEvent.NEW_CONFIGURATOR_NOT_ADDED);
        }
    }
    
    @MethodName("Visualizza visite proposte, complete, confermate, cancellate e effettuate")
    public void getElencoVisiteProposteCompleteConfermateCancellateEffettuate() {
        view.mostraListaVisite(archivio.getElencoVisiteProposteCompleteConfermateCancellateEffettuate(connectionCode));
    }
    
    @MethodName("Aggiungi volontari ad un tipo di visita esistente")
    public void aggiungiVolontariATipiVisita() {
        if (canAddOrRemove()) {
            view.mostraListaTipiVisita(archivio.getElencoTipiVisite(connectionCode));
            String tipo = richiediVisitaEsistente();
            ArrayList<Credenziali> volontari = richiediVolontari();
            userInfo.associaVolontariATipoVisitaEsistente(connectionCode, volontari, tipo);
        }
    }
    
    private boolean canAddOrRemove() {
        if (userInfo.canAddOrRemove(connectionCode)) {
            return true;
        } else {
            catchEvent(AppEvent.CANT_ADD_OR_REMOVE);
            return false;
        }
    }
    
    @MethodName("Aggiungi luogo")
    public void aggiungiLuogo() {
        if (canAddOrRemove()) {
            do {
                LuogoDTO luogo = richiediLuogo();
                if (archivio.aggiungiLuogo(connectionCode, luogo)) {
                    catchEvent(AppEvent.PLACE_ADDED);
                    boolean aggiunto = false;
                    do {
                        aggiunto = aggiungiTipoVisitePartendoDaLuogo(luogo.getTag());
                        catchEvent(aggiunto ? AppEvent.VISIT_TYPE_ADDED : AppEvent.VISIT_TYPE_NOT_ADDED);
                        if (aggiunto) {
                            aggiunto = !chiediSiNo("Vuoi continuare con l'aggiunta di tipi di visite?");
                        }
                    } while (!aggiunto);
                } else {
                    catchEvent(AppEvent.PLACE_NOT_ADDED);
                }
            } while (chiediSiNo("Vuoi aggiungere un altro luogo?"));
        }
    }
    
    // Metodi per richieste specifiche tramite view
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
    
    private LuogoDTO richiediLuogo() {
        String requestId = UUID.randomUUID().toString();
        UiRequest request = new UiRequest(UiEventType.RICHIEDI_LUOGO, "luogo", requestId);
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        richiesteInCorso.put(requestId, future);
        
        view.richiediInput(request);
        
        try {
            return (LuogoDTO) future.get();
        } catch (Exception e) {
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<Credenziali> richiediVolontari() {
        String requestId = UUID.randomUUID().toString();
        UiRequest request = new UiRequest(UiEventType.RICHIEDI_VOLONTARI, "volontari", requestId);
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        richiesteInCorso.put(requestId, future);
        
        view.richiediInput(request);
        
        try {
            return (ArrayList<Credenziali>) future.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private String richiediOraValida(String messaggio) {
        String requestId = UUID.randomUUID().toString();
        UiRequest request = new UiRequest(UiEventType.RICHIEDI_ORA_VALIDA, messaggio, requestId);
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        richiesteInCorso.put(requestId, future);
        
        view.richiediInput(request);
        
        try {
            return (String) future.get();
        } catch (Exception e) {
            return null;
        }
    }
    
    private int richiediNumeroConLimite(String messaggio, int limite) {
        String requestId = UUID.randomUUID().toString();
        UiRequest request = new UiRequest(UiEventType.RICHIEDI_NUMERO_CON_LIMITE, messaggio, limite, requestId);
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        richiesteInCorso.put(requestId, future);
        
        view.richiediInput(request);
        
        try {
            return (Integer) future.get();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<Integer> richiediGiorniPrenotabili() {
        String requestId = UUID.randomUUID().toString();
        UiRequest request = new UiRequest(UiEventType.RICHIEDI_GIORNI_PRENOTABILI, "giorni prenotabili", requestId);
        
        CompletableFuture<Object> future = new CompletableFuture<>();
        richiesteInCorso.put(requestId, future);
        
        view.richiediInput(request);
        
        try {
            return (ArrayList<Integer>) future.get();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    public TipoVisitaDTO richiediTipoVisita(String luogo) {
        String tipoVisita = richiediNuovoTipoVisita();
        String titolo = richiediInput("titolo della visita");
        String descrizione = richiediInput("descrizione riassuntiva della visita");
        String puntoIncontro = richiediInput("punto di incontro della visita (locazione geografica)");
        String dataInizio = richiediDataValida("apertura del periodo della visita (dd-mm-yyyy)");
        String dataFine = "";
        do {
            dataFine = richiediDataValida("chiusura del periodo della visita (dd-mm-yyyy)");
            if (Time.comesBefore(dataFine, dataInizio)) {
                mostraMessaggio("Non può finire prima che inizi.");
            }
        } while (Time.comesBefore(dataFine, dataInizio));
        
        ArrayList<Integer> giorniPrenotabili = richiediGiorniPrenotabili();
        String oraInizio = richiediOraValida("ora d'inizio visita (hh-mm)");
        int durataVisita = richiediInt("durata della visita in minuti (ad esempio 120 sono 120 minuti, quindi 2 ore)");
        boolean ticket = chiediSiNo("è da acquistare o no un biglietto?");
        int minFruitore = richiediNumeroConLimite("minimo fruitori per confermare la visita", 0);
        int maxFruitore = richiediNumeroConLimite("massimo fruitori per completare la visita", minFruitore);
        ArrayList<Credenziali> volontari = richiediVolontari();
        
        return new TipoVisitaDTO(tipoVisita, titolo, luogo, descrizione, puntoIncontro, dataInizio,
                dataFine, giorniPrenotabili, oraInizio, durataVisita, ticket, minFruitore,
                maxFruitore, volontari);
    }
    
    private String richiediNuovoTipoVisita() {
        String tipoVisita;
        do {
            tipoVisita = richiediInput("nuovo tipo visita");
            if (checkIfVisitTypeExists(tipoVisita)) {
                catchEvent(AppEvent.TAG_ALREADY_EXISTS);
            }
        } while (checkIfVisitTypeExists(tipoVisita));
        
        return tipoVisita;
    }
    
    public String richiediVisitaEsistente() {
        String tipo;
        do {
            tipo = richiediInput("tipo visita esistente");
        } while (!checkIfVisitTypeExists(tipo));
        return tipo;
    }
}