package client.controller_utente;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import archivio.ArchivioFactory;
import archivio.UserInfoManager;
import client.app.ViewInterface;
import client.log_events.AppEvent;
import dto.DTO;
import dto.PrenotazioneDTO;
import dto.VisitaDTO;
import utility.MethodName;
import utility.Time;

public class HandlerFruitore extends ControllerUtente {
    
    private UserInfoManager userInfo;
    
    public HandlerFruitore(ViewInterface view, String connectionCode, int tipoApp) {
        super(tipoApp, view);
        this.connectionCode = connectionCode;
        userInfo = ArchivioFactory.createUserInfoManager(tipoApp);
    }
    
    private void visualListVisitDTO(List<VisitaDTO> visite) {
        if (visite != null) {
            view.mostraListaVisite(visite);
        } else {
            catchEvent(AppEvent.NO_VISIT);
        }
    }
    
    @MethodName("Visualizza visite proposte, confermate e cancellate")
    public void getElencoVisiteProposteConfermateCancellate() {
        visualListVisitDTO(userInfo.getElencoVisiteProposteConfermateCancellateFruitore());
    }
    
    @MethodName("Visualizza tutte le visite proposte/confermate/cancellate/complete che hai prenotato")
    public void visualizzaVisiteProposteConfermateCancellateCompletePrenotate() {
        visualListVisitDTO(userInfo.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(connectionCode));
    }
    
    @MethodName("Effettua iscrizione ad una visita proposta")
    public void effettuaIscrizione() {
        String date = "";
        getElencoVisiteProposteConfermateCancellate();
        
        do {
            date = richiediDataValida("data in cui prenotare");
            if (Time.isThreeDaysOrLessBefore(Time.getActualDate(), date)) {
                catchEvent(AppEvent.CANT_ADD_RESERVATION_ON_DAY);
            }
        } while (Time.isThreeDaysOrLessBefore(Time.getActualDate(), date));
        
        if (userInfo.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date) != null) {
            visualListVisitDTO(userInfo.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date));
            String tipoVisita = richiediTipoVisitaEsistente();
            int numeroIscrizione = richiediNumeroConLimite("per quante persone vuoi prenotare", 0);
            String codice = userInfo.inserisciPrenotazione(connectionCode, 
                new PrenotazioneDTO(date, tipoVisita, numeroIscrizione));
            
            if (codice != null) {
                catchEvent(AppEvent.RESERVATION_INSERTED, codice);
            } else {
                catchEvent(AppEvent.RESERVATION_NOT_INSERTED);
            }
        } else {
            catchEvent(AppEvent.NO_VISIT_ON_DAY);
        }
    }
    
    private Set<String> prenotazioniLinkate() {
        List<PrenotazioneDTO> prenotazioni = userInfo.getElencoPrenotazioniFruitore(connectionCode);
        Set<String> codiciPrenotazioni = new HashSet<>();
        for (DTO prenotazione : prenotazioni) {
            codiciPrenotazioni.add(((PrenotazioneDTO) prenotazione).getCodice());
        }
        return codiciPrenotazioni;
    }
    
    private void visualElencoPrenotazioni() {
        List<PrenotazioneDTO> prenotazioni = userInfo.getElencoPrenotazioniFruitore(connectionCode);
        if (prenotazioni != null) {
            view.mostraListaPrenotazioni(prenotazioni);
        }
    }
    
    @MethodName("Disdisci una prenotazione")
    public void disdiciIscrizione() {
        if (userInfo.getElencoPrenotazioniFruitore(connectionCode) != null) {
            visualElencoPrenotazioni();
            String codicePrenotazioneDaEliminare = null;
            Set<String> k = prenotazioniLinkate();
            
            if (k != null) {
                do {
                    codicePrenotazioneDaEliminare = richiediInput("codice della prenotazione da eliminare (ESC per annullare)");
                    if (codicePrenotazioneDaEliminare.equalsIgnoreCase("esc")) return;
                    if (!k.contains(codicePrenotazioneDaEliminare)) {
                        catchEvent(AppEvent.WRONG_RESERVATION_CODE);
                    }
                } while (!k.contains(codicePrenotazioneDaEliminare));
                
                if (userInfo.rimuoviPrenotazione(connectionCode, codicePrenotazioneDaEliminare)) {
                    catchEvent(AppEvent.RESERVATION_REMOVED);
                } else {
                    catchEvent(AppEvent.RESERVATION_NOT_REMOVED);
                }
            }
        } else {
            catchEvent(AppEvent.NO_RESERVATION_MADE);
        }
    }
    
    private String richiediTipoVisitaEsistente() {
        String tipo;
        do {
            tipo = richiediInput("tipo visita esistente");
            if (!checkIfVisitTypeExists(tipo)) {
                catchEvent(AppEvent.VISITTYPE_NON_EXISTENT);
            }
        } while (!checkIfVisitTypeExists(tipo));
        return tipo;
    }
    
    private int richiediNumeroConLimite(String messaggio, int limite) {
        int n;
        do {
            n = richiediInt(messaggio);
            if (n <= limite) {
                mostraMessaggio("Non può essere più piccolo o uguale di " + limite + ".");
            }
        } while (n <= limite);
        return n;
    }
}