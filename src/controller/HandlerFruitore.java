package controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dto.PrenotazioneDTO;
import dto.VisitaDTO;
import main.App;
import utility.MethodName;
import utility.Time;

public class HandlerFruitore extends ControllerUtente {

	public HandlerFruitore(ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
	}
	
	
	private void visualListVisitDTO (List<VisitaDTO> visite, App a) {
		if (visite != null) {
			for (VisitaDTO v : visite) {
				if (!v.getStato().equals("cancellata")) {
					a.view("-----------");
					a.view("Titolo: " +  v.getTitolo());
					a.view("Descrizione: " +  v.getDescrizione());
					a.view("Punto d'incontro: " +  v.getPuntoIncontro());
					a.view("Giorno: " +  v.getGiorno());
					a.view("Ora d'inizio: " +  v.getOraInizio());
					a.view("Da acquistare: " +  v.getDaAcquistare());
					a.view("Stato: " +  v.getStato());
					a.view("Tag: " +  v.getTag());
				}
				else {
					a.view("-----------");
					a.view("Titolo: " +  v.getTitolo());
					a.view("Giorno mancato svolgimento: " +  v.getGiorno());
					a.view("Stato: " +  v.getStato());
				}
			}
		}
	}
	
	@MethodName("Visualizza visite proposte, confermate e cancellate")
	public void getElencoVisiteProposteConfermateCancellate (App a) {
		visualListVisitDTO(gdb.getElencoVisiteProposteConfermateCancellateFruitore(), a);
	}
	
	@MethodName("Visualizza tutte le visite proposte/confermate/cancellate/complete che hai prenotato")
	public void visualizzaVisiteProposteConfermateCancellateCompletePrenotate (App a) {
		a.view("Ecco le visite a cui ti sei prenotato:");
		visualListVisitDTO(gdb.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(this), a);
	}
	
	@MethodName("Effettua iscrizione ad una visita proposta")
	public void effettuaIscrizione (App a) {
		String date = "";
		getElencoVisiteProposteConfermateCancellate(a);
		do {
			date = a.richiediDataValida("data in cui prenotare (dd-mm-yyyy)");
			if (Time.isThreeDaysOrLessBefore(Time.getActualDate(), date)) a.view("Non è possibile prenotare nei 3 giorni successivi a questo."); //LOGICA DI MODEL!!!
		} while (Time.isThreeDaysOrLessBefore(Time.getActualDate(), date)); //non voglio che sia nei prossimi tre giorni, non ci sono visite prenotabili
		if (gdb.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date) != null) {
			visualListVisitDTO(gdb.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date), a);
			String tipoVisita = richiediVisitaEsistente(a, "tag del tipo della visita");
			int numeroIscrizione = richiediIntMaggioreDiZero(a, "per quante persone vuoi prenotare");
			String codice = gdb.inserisciPrenotazione(this, new PrenotazioneDTO(date, tipoVisita, numeroIscrizione));
			a.view(codice != null ? "Prenotazione inserita, codice prenotazione: " + codice : "Prenotazione non inserita.");
		}
		else {
			a.view("Nessuna visita segnata per questo giorno.");
		}
		
	}
	
	private Set<String> prenotazioniLinkate () {
		List<PrenotazioneDTO> prenotazioni = gdb.getElencoPrenotazioniFruitore(this);
		Set<String> codiciPrenotazioni = new HashSet<>();
		for (PrenotazioneDTO prenotazione : prenotazioni) codiciPrenotazioni.add(prenotazione.getCodice());
		return codiciPrenotazioni;
	}
	
	private void visualElencoPrenotazioni (App a) {
		List<PrenotazioneDTO> prenotazioni = gdb.getElencoPrenotazioniFruitore(this);
		if (prenotazioni != null) {
			for (PrenotazioneDTO prenotazione : prenotazioni) {
				if (!Time.isThreeDaysOrLessBefore(Time.getActualDate(), prenotazione.getGiorno())) {
					a.view("-----------");
					a.view("Codice: " + prenotazione.getCodice() + ", giorno: " + prenotazione.getGiorno());
					a.view("Tag visita: " + prenotazione.getTag_visita()); //TODO meglio titolo
				}
			}
		}
	}
	
	@MethodName("Disdisci una prenotazione")
	public void disdiciIscrizione (App a) {
		if (gdb.getElencoPrenotazioniFruitore(this) != null) {
			visualElencoPrenotazioni(a);
			String codicePrenotazioneDaEliminare = null;
			Set<String> k = prenotazioniLinkate();
			if (k != null)do {
				codicePrenotazioneDaEliminare = a.richiediInput("codice della prenotazione da eliminare (ESC per annullare)");
				if (codicePrenotazioneDaEliminare.equalsIgnoreCase("esc")) return;
				if (!k.contains(codicePrenotazioneDaEliminare)) a.view("Il codice inserito non è legato a nessuna prenotazione, reinserirlo.");
			} while (!k.contains(codicePrenotazioneDaEliminare));
			a.view(gdb.rimuoviPrenotazione(this, codicePrenotazioneDaEliminare) ? "Prenotazione rimossa." : "Prenotazione non rimossa.");
		}
		else a.view("Non hai prenotazioni.");

	}
	
}
