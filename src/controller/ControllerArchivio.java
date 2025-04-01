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
	
	private Archivio d;
	public ControllerArchivio (Archivio d) {
		this.d = d; 
	}

	public boolean checkPrimoAvvio () { //OK
		return d.checkPrimoAvvio();
	}
	
	public int getTipoUtente (String username) { //OK
		return d.getTipoUtente(username);
	}
	
	public boolean pubblicaPiano(String username) { //OK
		return d.tryPubblicaPiano(username);
	}
	
	public boolean chiudiRaccoltaDisponibilita (String username) { //OK
		return d.tryChiudiRaccoltaDisponibilita(username);
	}
	
	public boolean apriRaccoltaDisponibilita(String username) {  //OK
		return d.tryApriRaccoltaDisponibilita(username);
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String volontario, String tipoVisita) { //OK
		if (checkIfUserExists(volontario) && checkIfVisitTypeExists(tipoVisita)) {
			if (d.checkIfCanLinkVolontario(volontario, tipoVisita)) {
				return d.associaVolontarioEsistenteATipoVisitaEsistente(volontario, tipoVisita);
			}
			else {
				return false;
			}
		}
		else return false;
	}
	
	public boolean getPossibilitaDareDisponibilita() { 
		return d.getPossibileDareDisponibilita();
	}
	
    public boolean inserisciDisponibilita(String data, String username) { //OK
    	return d.inserisciDisponibilita(data, username);
	}
	
	public HashMap<String, List<String>> getDatePerDisponibilita(String username) {	 //OK
		return d.getDatePerDisponibilita(username);
	}

	public Set<String> getElencoTipiVisite () { //OK
 		return d.getElencoTipiVisite();
 	}

 	public List<String> getElencoTipiVisiteVolontario (String username) { 
 		return d.getElencoTipiVisiteVolontario(username);
 	}
	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		return d.tryImpostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, tipiVisitaNecessario);
	}
	
	public boolean rimuoviLuogo (String luogo, String username) {
		if (d.checkIfPlaceExists(luogo) && canAddOrRemove(username)) return d.rimuoviLuogo(luogo);
		else return false;
	}
	
	public boolean rimuoviVolontario (String volontario, String username) {
		if (d.checkIfUserExists(volontario) && d.getTipoUtente(volontario) == CostantiStruttura.VOLONTARIO && canAddOrRemove(username)) return d.rimuoviVolontario(volontario);
		else return false;
	}
	
	public boolean rimuoviTipo (String tipo, String username) {
		if (d.checkIfVisitTypeExists(tipo) && canAddOrRemove(username)) return d.rimuoviTipo(tipo);
		else return false;
	}
	
	public boolean canAddOrRemove(String username) {
		if (checkIfUserExists(username) && getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return d.canAddOrRemove();
		}
		else return false;
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String username) {
		if (d.checkIfUserExists(username) && d.getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return d.isPrimaConfigurazione();
		}
		else return false;
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
		if (!checkIfUserExists(username)) return false;
		else return (d.checkPrimoAccesso(username)); 
	}
	
	public boolean indicaDatePrecluse (String date) { //ok
		if (Time.isValidDate(date) && Time.isThisDateInMonthPlus3(date)) return d.indicaDatePrecluse(date);
		else return false;
	}
	
	public boolean rimuoviPrenotazione(String username, String codicePrenotazione) {
		return d.rimuoviPrenotazione(username, codicePrenotazione);
	}
	
	public List<VisitaDTO> visiteConfermateVolontario (String username) {
		return d.visiteConfermateVolontario(username);
	}
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String username) {
		return d.getElencoPrenotazioniFruitore(username);
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore() { //dovrebbe ritornare un oggetto con tutte le info da stampare
		return d.getElencoVisiteProposteConfermateCancellateFruitore();
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String username) {
		return d.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(username);
	}

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		return d.getElencoVisiteProposteCompleteConfermateCancellateEffettuate();
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date) {
		return d.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date);
	}
	
	public String inserisciPrenotazione (String username, PrenotazioneDTO prenotazione) {
		return d.inserisciPrenotazione(username, prenotazione);
	}
	
	public boolean cambiaCredenziali (String username, Credenziali c, ControllerUtente gu) {
		if (!checkIfUserExists(username)) return false;
		if (checkIfUserExists(c.getUsername())) return false;
		if (d.modificaCredenziali(username, c)) { //Problematico questa funzione, posso cambiare credenziali senza eseguire primo accesso
			d.primoAccessoEseguito(c.getUsername());
			gu.setUsername(c.getUsername());
			return true;
		}	
		return false;
	}
	
	public boolean checkIfPlaceExists (String luogo) {
		return d.checkIfPlaceExists(luogo);
	}
	
	public boolean aggiungiLuogo (String tag, String nome, String collocazione, Set<String> tipiVisitaVal) {
		return d.aggiungiLuogo(tag, nome, collocazione, tipiVisitaVal);
	}
	
	public void setPrimaConfigurazione() {
		d.setPrimaConfigurazione();
	}
	
	public boolean aggiungiTipoVisite (String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal) {
		return d.tryAggiungiVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabiliVal, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, volontariVal);
	}
	
	public boolean checkIfUserExists (String username) {
		return d.checkIfUserExists(username);
	}
	
	public boolean createNewFruitore(String username, String password) {
		return d.impostaCredenzialiNuovoFruitore(username, password);
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		return d.checkIfVisitTypeExists(tipo);
	}
	
	public void impostaAmbitoTerritoriale (String s, String username) {
		if (d.isPrimaConfigurazione() && getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) d.impostaAmbitoTerritoriale(s);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String usernameChiEsegue, String username, String password) {
		if (getTipoUtente(usernameChiEsegue) == CostantiStruttura.CONFIGURATORE) return d.impostaCredenzialiNuovoConfiguratore(username, password);
		else return false;
	}
	
	public boolean modificaMaxPrenotazione (String username, int max) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) return d.impostaMaxPrenotazione(max);
		else return false;
	}

	public Set<UserDTO> getListaUser(String username, int tipo_user) {
		return d.getListaUser(username, tipo_user);
	}
	
	public List<String> getElencoLuoghiVisitabili (String username) { 
		return d.getElencoLuoghiVisitabili(username);
	}
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String username) {
		return d.getElencoTipiVisiteLuogo(username);
	}
	
}
