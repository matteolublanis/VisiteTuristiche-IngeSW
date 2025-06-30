package client.controller_utente;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import archivio.ArchivioFactory;
import archivio.UserInfoManager;
import client.app.App;
import client.log_events.AppEvent;
import dto.DTO;
import dto.PrenotazioneDTO;
import dto.VisitaDTO;
import utility.MethodName;
import utility.Time;

public class HandlerFruitore extends ControllerUtente {
	//Precondizioni di tutti i metodi: param != null
	
	private UserInfoManager userInfo;

	
	public HandlerFruitore(App a, String connectionCode, int tipoApp) {
		super(tipoApp);
		this.a = a;
		a.setGu(this);
		this.connectionCode = connectionCode;
		userInfo = ArchivioFactory.createUserInfoManager(tipoApp);
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
		visualListVisitDTO(userInfo.getElencoVisiteProposteConfermateCancellateFruitore());
	}
	
	@MethodName("Visualizza tutte le visite proposte/confermate/cancellate/complete che hai prenotato")
	public void visualizzaVisiteProposteConfermateCancellateCompletePrenotate () {
		visualListVisitDTO(userInfo.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(connectionCode));
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
		if (userInfo.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date) != null) {
			visualListVisitDTO(userInfo.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date));
			String tipoVisita = a.richiediTipoVisitaEsistente();
			int numeroIscrizione = a.richiediNumeroConLimiteInferiore("per quante persone vuoi prenotare", 0);
			String codice = userInfo.inserisciPrenotazione(connectionCode, new PrenotazioneDTO(date, tipoVisita, numeroIscrizione));
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
		List<PrenotazioneDTO> prenotazioni = userInfo.getElencoPrenotazioniFruitore(connectionCode);
		Set<String> codiciPrenotazioni = new HashSet<>();
		for (DTO prenotazione : prenotazioni) codiciPrenotazioni.add(((PrenotazioneDTO) prenotazione).getCodice());
		return codiciPrenotazioni;
	}
	
	private void visualElencoPrenotazioni () {
		List<PrenotazioneDTO> prenotazioni = userInfo.getElencoPrenotazioniFruitore(connectionCode);
		if (prenotazioni != null) {
			a.viewListPrenotazioneDTO(prenotazioni);
		}
	}
	
	//Postcondizione: prenotazione rimossa da Archivio
	@MethodName("Disdisci una prenotazione")
	public void disdiciIscrizione () {
		if (userInfo.getElencoPrenotazioniFruitore(connectionCode) != null) {
			visualElencoPrenotazioni();
			String codicePrenotazioneDaEliminare = null;
			Set<String> k = prenotazioniLinkate();
			if (k != null)do {
				codicePrenotazioneDaEliminare = a.richiediInput("codice della prenotazione da eliminare (ESC per annullare)");
				if (codicePrenotazioneDaEliminare.equalsIgnoreCase("esc")) return;
				if (!k.contains(codicePrenotazioneDaEliminare)) a.catchEvent(AppEvent.WRONG_RESERVATION_CODE);
			} while (!k.contains(codicePrenotazioneDaEliminare));
			if (userInfo.rimuoviPrenotazione(connectionCode, codicePrenotazioneDaEliminare)) {
				a.catchEvent(AppEvent.RESERVATION_REMOVED);
			}
			else a.catchEvent(AppEvent.RESERVATION_NOT_REMOVED);
		}
		else a.catchEvent(AppEvent.NO_RESERVATION_MADE);

	}
	
}
