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
	
	public boolean getPossibilitaDareDisponibilita() {
		return d.getPossibilitaDareDisponibilita();
	}
	
	public boolean inserisciDisponibilita(String data, String username) {
		return d.inserisciDisponibilita(data, username);
	}
	
	public String getDatePerDisponibilita(String username) {
		return d.getDatePerDisponibilita(username);
	}
	
	public String getElencoTipiVisite () {
 		return d.getElencoTipiVisite();
 	}
 
 	public String getElencoTipiVisiteVolontario (String username) {
 		return d.getElencoTipiVisiteVolontario(username);
 	}
	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, String tipi_visiteVal, boolean tipiVisitaNecessario) {
		return d.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, tipiVisitaNecessario);
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String username) {
		return d.checkPrimaConfigurazioneArchivio(username);
	}
	
	public Credenziali getCredenzialiIniziali () {
		if (checkPrimoAvvio()) {
			d.setPrimoAvvio();
			return (d.getCredenzialiConfIniziale()); 
		}
		else return null; 
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		d.setPossibilitaDareDisponibilitaVolontari(b);
	}
	
	public boolean isReleaseOrLaterDay() {
		return d.isReleaseOrLaterDay();
	}
	
	public boolean isPrimaPubblicazione () {
		return d.isPrimaPubblicazione();
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		return d.checkCredenzialiCorrette(c);
	}
	
	public boolean checkPrimoAccesso (String username) {
		return (d.checkPrimoAccesso(username) == true); 
	}
	
	public boolean indicaDatePrecluse (String date) {
		return d.indicaDatePrecluse(date);
	}
	
	public String getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		return d.getElencoVisiteProposteCompleteConfermateCancellateEffettuate();
	}
	
	public boolean cambiaCredenziali (String username, Credenziali c, ControllerUtente gu) {
		if (d.modificaCredenziali(username, c)) {
			d.primoAccessoEseguito(c.getUsername());
			gu.setUsername(c.getUsername());
			return true;
		}	
		return false;
	}
	
	public boolean checkIfPlaceExists (String luogo) {
		return d.checkIfPlaceExists(luogo);
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
	
	public boolean checkIfVolontarioExists (String volontario) {
		return d.checkIfVolontarioExists(volontario);
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		return d.checkIfVisitTypeExists(tipo);
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
