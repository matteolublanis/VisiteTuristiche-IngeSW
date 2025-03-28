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
			a.view("Titolo: " +  v.getTitolo());
			a.view("Descrizione: " +  v.getDescrizione());
			a.view("Punto d'incontro: " +  v.getPuntoIncontro());
			a.view("Giorno: " +  v.getGiorno());
			a.view("Ora d'inizio: " +  v.getOraInizio());
			a.view("Da acquistare: " +  v.getDaAcquistare());
			a.view("Stato: " +  v.getStato());
		}
	}
	
}
