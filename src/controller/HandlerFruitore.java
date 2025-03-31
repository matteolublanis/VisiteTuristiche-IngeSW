package controller;

import dto.VisitaDTO;
import main.App;
import utility.MethodName;

public class HandlerFruitore extends ControllerUtente {

	public HandlerFruitore(ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
	}
	
	@MethodName("Visualizza visite proposte, confermate e cancellate")
	public void getElencoVisiteProposteConfermateCancellate (App a) {
		for (VisitaDTO v : gdb.getElencoVisiteProposteConfermateCancellateFruitore()) {
			if (!v.getStato().equals("cancellata")) {
				a.view("-----------");
				a.view("Titolo: " +  v.getTitolo());
				a.view("Descrizione: " +  v.getDescrizione());
				a.view("Punto d'incontro: " +  v.getPuntoIncontro());
				a.view("Giorno: " +  v.getGiorno());
				a.view("Ora d'inizio: " +  v.getOraInizio());
				a.view("Da acquistare: " +  v.getDaAcquistare());
				a.view("Stato: " +  v.getStato());
				a.view("-----------");
			}
			else {
				a.view("-----------");
				a.view("Titolo: " +  v.getTitolo());
				a.view("Giorno mancato svolgimento: " +  v.getGiorno());
				a.view("Stato: " +  v.getStato());
				a.view("-----------");
			}
		}
	}
	
	@MethodName("Visualizza tutte le visite proposte/confermate/cancellate/complete che hai prenotato")
	public void visualizzaVisiteProposteConfermateCancellateCompletePrenotate (App a) {
		a.view("Ecco le visite a cui ti sei prenotato:");
		for (VisitaDTO v : gdb.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(username)) {
			if (!v.getStato().equals("cancellata")) {
				a.view("-----------");
				a.view("Titolo: " +  v.getTitolo());
				a.view("Descrizione: " +  v.getDescrizione());
				a.view("Punto d'incontro: " +  v.getPuntoIncontro());
				a.view("Giorno: " +  v.getGiorno());
				a.view("Ora d'inizio: " +  v.getOraInizio());
				a.view("Da acquistare: " +  v.getDaAcquistare());
				a.view("Stato: " +  v.getStato());
				a.view("-----------");
			}
			else {
				a.view("-----------");
				a.view("Titolo: " +  v.getTitolo());
				a.view("Giorno mancato svolgimento: " +  v.getGiorno());
				a.view("Stato: " +  v.getStato());
				a.view("-----------");
			}
		}
	}
	
	@MethodName("Effettua iscrizione ad una visita proposta")
	public void effettuaIscrizione (App a) {
		//1 richiedi giorno prenotazione
		//2 richiedi visita da prenotare (ha senso ristamparle)
		//3 richiedi numero di persone da prenotare
		//4 se non supera/cancellata, inserisci prenotazione e comunica codice
	}
	
	@MethodName("Disdisci una prenotazione")
	public void disdiciIscrizione (App a) {
		//1 mostra elenco prenotazioni
		//2 richiedi quale eliminare
		//3 se esiste elimina, se no lascia stare
	}
	
}
