package controller;

import archivio.Archivio;
import utility.Credenziali;

public class ControllerArchivio {

	private Archivio d;
	
	public ControllerArchivio (Archivio d) {
		this.d = d; 
	}

	public boolean checkPrimoAvvio () {
		return d.checkPrimoAvvio();
	}
	
	public int getTipoUtente (String username) {
		return d.getTipoUtente(username);
	}

	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, String tipi_visiteVal) {
		return d.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal);
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String username) {
		return d.checkPrimaConfigurazioneArchivio(username);
	}
	
	public String getCredenzialiIniziali () {
		if (checkPrimoAvvio()) {
			d.setPrimoAvvio();
			return new String(d.getCredenzialiConfIniziale()); //TODO: problema logico, la Stringa dovrebbe essere del DB
		}
		else return ""; 
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		return d.checkCredenzialiCorrette(c);
	}
	
	public boolean checkPrimoAccesso (String username) {
		return (d.checkPrimoAccesso(username) == true); //se true è il primo accesso
	}
	
	public boolean indicaDatePrecluse (String date) {
		return d.indicaDatePrecluse(date);
	}
	
	public String getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		return d.getElencoVisiteProposteCompleteConfermateCancellateEffettuate();
	}
	
	public boolean cambiaCredenziali (String username, Credenziali c) {
		if (d.modificaCredenziali(username, c)) {
			d.primoAccessoEseguito(c.getUsername());	
			return true;
		}	
		return false;
	}
	
	public boolean aggiungiLuogo (String tag, String nome, String collocazione, String tipiVisitaVal) {
		return d.aggiungiLuogo(tag, nome, collocazione, tipiVisitaVal);
	}
	
	public void setPrimaConfigurazione() {
		d.setPrimaConfigurazione();
	}
	
	public boolean aggiungiTipoVisite (String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, String giorniPrenotabili, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, String volontari) {
		return d.aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabili, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, volontari);
	}
	
	public void impostaAmbitoTerritoriale (String s) {
		d.impostaAmbitoTerritoriale(s);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password) {
		return d.impostaCredenzialiNuovoConfiguratore(username, password);
	}
	
	public boolean modificaMaxPrenotazione (int max) {
		return d.impostaMaxPrenotazione(max);
	}
	
	public String getListaUser(int tipo_user) {
		return d.getElencoUser(tipo_user);
	}
	
	public String getElencoLuoghiVisitabili () {
		return d.getElencoLuoghiVisitabili();
	}
	
	public String getElencoTipiVisiteLuogo () {
		return d.getElencoTipiVisiteLuogo();
	}
	
}
