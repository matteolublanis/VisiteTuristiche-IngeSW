package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import archivio.Archivio;
import dto.*;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.Time;

public class ControllerArchivio {
	
	private Map<ControllerUtente, String> usernameLinkati = new HashMap<>(); 
	private Archivio archivio;
	public ControllerArchivio (Archivio archivio) {
		this.archivio = archivio; 
	}
	
	protected boolean addControllerUtente(ControllerUtente gu, String username) {
		if (usernameLinkati.values().contains(username)) return false;
		else {
			usernameLinkati.put(gu, username);
			return true;
		}
	}

	public boolean checkPrimoAvvio () { //OK
		return archivio.checkPrimoAvvio();
	}
	
	public int getTipoUtente (String username) { //OK
		return archivio.getTipoUtente(username);
	}
	
	public boolean pubblicaPiano(String username) { //OK
		return archivio.tryPubblicaPiano(username);
	}
	
	public boolean chiudiRaccoltaDisponibilita (String username) { //OK
		return archivio.tryChiudiRaccoltaDisponibilita(username);
	}
	
	public boolean apriRaccoltaDisponibilita(String username) {  //OK
		return archivio.tryApriRaccoltaDisponibilita(username);
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String volontario, String tipoVisita) { //OK
		if (checkIfUserExists(volontario) && checkIfVisitTypeExists(tipoVisita)) {
			if (archivio.checkIfCanLinkVolontario(volontario, tipoVisita)) {
				return archivio.associaVolontarioEsistenteATipoVisitaEsistente(volontario, tipoVisita);
			}
			else {
				return false;
			}
		}
		else return false;
	}
	
	public boolean getPossibilitaDareDisponibilita() { 
		return archivio.getPossibileDareDisponibilita();
	}
	
    public boolean inserisciDisponibilita(String data, String username) { //OK
    	return archivio.inserisciDisponibilita(data, username);
	}
	
	public Map<String, List<String>> getDatePerDisponibilita(String username) {	 //OK
		return archivio.getDatePerDisponibilita(username);
	}

	public Set<String> getElencoTipiVisite () { //OK
 		return archivio.getElencoTipiVisite();
 	}

 	public List<String> getElencoTipiVisiteVolontario (String username) { 
 		return archivio.getElencoTipiVisiteVolontario(username);
 	}
	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		return archivio.tryImpostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, tipiVisitaNecessario);
	}
	
	public boolean rimuoviLuogo (String luogo, String username) {
		if (archivio.checkIfPlaceExists(luogo) && canAddOrRemove(username)) return archivio.rimuoviLuogo(luogo);
		else return false;
	}
	
	public boolean rimuoviVolontario (String volontario, String username) {
		if (archivio.checkIfUserExists(volontario) && archivio.getTipoUtente(volontario) == CostantiStruttura.VOLONTARIO && canAddOrRemove(username)) return archivio.rimuoviVolontario(volontario);
		else return false;
	}
	
	public boolean rimuoviTipo (String tipo, String username) {
		if (archivio.checkIfVisitTypeExists(tipo) && canAddOrRemove(username)) return archivio.rimuoviTipo(tipo);
		else return false;
	}
	
	public boolean canAddOrRemove(String username) {
		if (checkIfUserExists(username) && getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return archivio.canAddOrRemove();
		}
		else return false;
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String username) {
		if (archivio.checkIfUserExists(username) && archivio.getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return archivio.isPrimaConfigurazione();
		}
		else return false;
	}
	
	public Credenziali getCredenzialiIniziali () {
		if (checkPrimoAvvio()) {
			archivio.setPrimoAvvio();
			return (archivio.getCredenzialiConfIniziale()); 
		}
		else return null; 
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		archivio.setPossibilitaDareDisponibilitaVolontari(b);
	}
	
	public boolean isReleaseOrLaterDay() {
		return archivio.isReleaseOrLaterDay();
	}
	
	public boolean isPrimaPubblicazione () {
		return archivio.isPrimaPubblicazione();
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		if (!checkIfUserExists(c.getUsername())) return false;
		return archivio.checkCredenzialiCorrette(c);
	}
	
	public boolean checkPrimoAccesso (String username) {
		if (!checkIfUserExists(username)) return false;
		else return (archivio.checkPrimoAccesso(username)); 
	}
	
	public boolean indicaDatePrecluse (String date) { //ok
		if (!Time.isValidDate(date)) return false;
		if (Time.isThisDateInMonthPlus3(date)) return archivio.indicaDatePrecluse(date);
		else return false;
	}
	
	public boolean rimuoviPrenotazione(String username, String codicePrenotazione) {
		return archivio.rimuoviPrenotazione(username, codicePrenotazione);
	}
	
	public List<VisitaDTO> visiteConfermateVolontario (String username) {
		return archivio.visiteConfermateVolontario(username);
	}
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String username) {
		return archivio.getElencoPrenotazioniFruitore(username);
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore() { //dovrebbe ritornare un oggetto con tutte le info da stampare
		return archivio.getElencoVisiteProposteConfermateCancellateFruitore();
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String username) {
		return archivio.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(username);
	}

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		return archivio.getElencoVisiteProposteCompleteConfermateCancellateEffettuate();
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date) {
		return archivio.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date);
	}
	
	public String inserisciPrenotazione (String username, PrenotazioneDTO prenotazione) {
		return archivio.inserisciPrenotazione(username, prenotazione);
	}
	
	public boolean cambiaCredenziali (String username, Credenziali c, ControllerUtente gu) {
		if (!checkIfUserExists(username)) return false;
		if (checkIfUserExists(c.getUsername())) return false;
		if (archivio.modificaCredenziali(username, c)) { //Problematico questa funzione, posso cambiare credenziali senza eseguire primo accesso
			archivio.primoAccessoEseguito(c.getUsername());
			gu.setUsername(c.getUsername());
			return true;
		}	
		return false;
	}
	
	public boolean checkIfPlaceExists (String luogo) {
		return archivio.checkIfPlaceExists(luogo);
	}
	
	public boolean aggiungiLuogo (String tag, String nome, String descrizione, String collocazione, Set<String> tipiVisitaVal) {
		return archivio.aggiungiLuogo(tag, nome, descrizione, collocazione, tipiVisitaVal);
	}
	
	public void setPrimaConfigurazione() {
		archivio.setPrimaConfigurazione();
	}
	
	public boolean aggiungiTipoVisite (String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal) {
		return archivio.tryAggiungiVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabiliVal, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, volontariVal);
	}
	
	public boolean checkIfUserExists (String username) {
		return archivio.checkIfUserExists(username);
	}
	
	public boolean createNewFruitore(String username, String password) {
		return archivio.impostaCredenzialiNuovoFruitore(username, password);
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		return archivio.checkIfVisitTypeExists(tipo);
	}
	
	public void impostaAmbitoTerritoriale (String s, String username) {
		if (archivio.isPrimaConfigurazione() && getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) archivio.impostaAmbitoTerritoriale(s);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String usernameChiEsegue, String username, String password) {
		if (getTipoUtente(usernameChiEsegue) == CostantiStruttura.CONFIGURATORE) return archivio.impostaCredenzialiNuovoConfiguratore(username, password);
		else return false;
	}
	
	public boolean modificaMaxPrenotazione (String username, int max) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) return archivio.impostaMaxPrenotazione(max);
		else return false;
	}

	public Set<UserDTO> getListaUser(String username, int tipo_user) {
		return archivio.getListaUser(username, tipo_user);
	}
	
	public List<String> getElencoLuoghiVisitabili (String username) { 
		return archivio.getElencoLuoghiVisitabili(username);
	}
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String username) {
		return archivio.getElencoTipiVisiteLuogo(username);
	}
	
}
