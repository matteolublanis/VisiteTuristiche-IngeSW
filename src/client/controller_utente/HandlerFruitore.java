package client.controller_utente;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import archivio.ArchivioFacade;
import client.app.App;
import client.log_events.AppEvent;
import dto.DTO;
import dto.PrenotazioneDTO;
import dto.VisitaDTO;
import utility.MethodName;
import utility.Time;

public class HandlerFruitore extends ControllerUtente {
	//Precondizioni di tutti i metodi: param != null
	
	public HandlerFruitore(ArchivioFacade gdb, App a, String connectionCode) {
		this.archivio = gdb;
		this.a = a;
		this.connectionCode = connectionCode;
	}
	
	@Override
	public void checkPrimoAccesso() {
		
	}

	
	private void visualListVisitDTO (List<VisitaDTO> visite) {
		if (visite != null) {
			a.viewListVisitaDTO(visite);
		}
		else {
			a.catchEvent(AppEvent.NO_VISIT);
		}
	}
	
	@MethodName("Visualizza visite proposte, confermate e cancellate")
	public void getElencoVisiteProposteConfermateCancellate () {
		visualListVisitDTO(archivio.getElencoVisiteProposteConfermateCancellateFruitore());
	}
	
	@MethodName("Visualizza tutte le visite proposte/confermate/cancellate/complete che hai prenotato")
	public void visualizzaVisiteProposteConfermateCancellateCompletePrenotate () {
		visualListVisitDTO(archivio.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(connectionCode));
	}
	
	//Postcondizione: prenotazione in Archivio
	@MethodName("Effettua iscrizione ad una visita proposta")
	public void effettuaIscrizione () {
		String date = "";
		getElencoVisiteProposteConfermateCancellate();
		do {
			date = a.richiediDataValida("data in cui prenotare");
			if (Time.isThreeDaysOrLessBefore(Time.getActualDate(), date)) a.catchEvent(AppEvent.CANT_ADD_RESERVATION_ON_DAY); 
		} while (Time.isThreeDaysOrLessBefore(Time.getActualDate(), date)); 
		if (archivio.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date) != null) {
			visualListVisitDTO(archivio.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date));
			String tipoVisita = a.richiediTipoVisitaEsistente();
			int numeroIscrizione = a.richiediNumeroConLimiteInferiore("per quante persone vuoi prenotare", 0);
			String codice = archivio.inserisciPrenotazione(connectionCode, new PrenotazioneDTO(date, tipoVisita, numeroIscrizione));
			if (codice != null) {
				a.catchEvent(AppEvent.RESERVATION_INSERTED, codice);
			}
			else {
				a.catchEvent(AppEvent.RESERVATION_NOT_INSERTED);
			}
		}
		else {
			a.catchEvent(AppEvent.NO_VISIT_ON_DAY);
		}
		
	}
	
	private Set<String> prenotazioniLinkate () {
		List<PrenotazioneDTO> prenotazioni = archivio.getElencoPrenotazioniFruitore(connectionCode);
		Set<String> codiciPrenotazioni = new HashSet<>();
		for (DTO prenotazione : prenotazioni) codiciPrenotazioni.add(((PrenotazioneDTO) prenotazione).getCodice());
		return codiciPrenotazioni;
	}
	
	private void visualElencoPrenotazioni (App a) {
		List<PrenotazioneDTO> prenotazioni = archivio.getElencoPrenotazioniFruitore(connectionCode);
		if (prenotazioni != null) {
			a.viewListPrenotazioneDTO(prenotazioni);
		}
	}
	
	//Postcondizione: prenotazione rimossa da Archivio
	@MethodName("Disdisci una prenotazione")
	public void disdiciIscrizione (App a) {
		if (archivio.getElencoPrenotazioniFruitore(connectionCode) != null) {
			visualElencoPrenotazioni(a);
			String codicePrenotazioneDaEliminare = null;
			Set<String> k = prenotazioniLinkate();
			if (k != null)do {
				codicePrenotazioneDaEliminare = a.richiediInput("codice della prenotazione da eliminare (ESC per annullare)");
				if (codicePrenotazioneDaEliminare.equalsIgnoreCase("esc")) return;
				if (!k.contains(codicePrenotazioneDaEliminare)) a.catchEvent(AppEvent.WRONG_RESERVATION_CODE);
			} while (!k.contains(codicePrenotazioneDaEliminare));
			if (archivio.rimuoviPrenotazione(connectionCode, codicePrenotazioneDaEliminare)) {
				a.catchEvent(AppEvent.RESERVATION_REMOVED);
			}
			else a.catchEvent(AppEvent.RESERVATION_NOT_REMOVED);
		}
		else a.catchEvent(AppEvent.NO_RESERVATION_MADE);

	}
	
}
