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
	
	public boolean pubblicaPiano(ControllerUtente gu) { 
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.tryPubblicaPiano();
		else return false;
	}
	
	public boolean chiudiRaccoltaDisponibilita (ControllerUtente gu) { //OK
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.tryChiudiRaccoltaDisponibilita();
		else return false;
	}
	
	public boolean apriRaccoltaDisponibilita(ControllerUtente gu) {  //OK
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.tryApriRaccoltaDisponibilita();
		else return false;
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(ControllerUtente gu, String volontario, String tipoVisita) { //OK
		if ( getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE
				&& checkIfUserExists(volontario) && checkIfVisitTypeExists(tipoVisita)) {
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
	
    public boolean inserisciDisponibilita(ControllerUtente gu, String data, String username) { //OK
    	 if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.VOLONTARIO) 
    		 return archivio.inserisciDisponibilita(data, username);
    	 else return false;
	}
	
	public Map<String, List<String>> getDatePerDisponibilita(ControllerUtente gu) {	 //OK
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.VOLONTARIO) 
			return archivio.getDatePerDisponibilita(usernameLinkati.get(gu));
		else return null;
	}

	public Set<String> getElencoTipiVisite (ControllerUtente gu) { //OK
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.getElencoTipiVisite();
		else return null;
 	}

 	public List<String> getElencoTipiVisiteVolontario (ControllerUtente gu) { 
 		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.VOLONTARIO) 
 			return archivio.getElencoTipiVisiteVolontario(usernameLinkati.get(gu));
 		else return null;
 	}
	
	public boolean impostaCredenzialiNuovoVolontario (ControllerUtente gu, String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.tryImpostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, tipiVisitaNecessario);
		else return false;
	}
	
	public boolean rimuoviLuogo (String luogo, ControllerUtente gu) {
		if (archivio.checkIfPlaceExists(luogo) && canAddOrRemove(usernameLinkati.get(gu))) return archivio.rimuoviLuogo(luogo);
		else return false;
	}
	
	public boolean rimuoviVolontario (String volontario, ControllerUtente gu) {
		if (archivio.checkIfUserExists(volontario) && archivio.getTipoUtente(volontario) == CostantiStruttura.VOLONTARIO && canAddOrRemove(usernameLinkati.get(gu))) return archivio.rimuoviVolontario(volontario);
		else return false;
	}
	
	public boolean rimuoviTipo (String tipo, ControllerUtente gu) {
		if (archivio.checkIfVisitTypeExists(tipo) && canAddOrRemove(usernameLinkati.get(gu))) return archivio.rimuoviTipo(tipo);
		else return false;
	}
	
	public boolean canAddOrRemove(String username) {
		if (checkIfUserExists(username) && getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return archivio.canAddOrRemove();
		}
		else return false;
	}
	
	public boolean checkPrimaConfigurazioneArchivio (ControllerUtente gu) {
		if (archivio.getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) {
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
	
	public boolean isReleaseOrLaterDay(ControllerUtente gu) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.isReleaseOrLaterDay();
		else return false;
	}
	
	public boolean isPrimaPubblicazione () {
		return archivio.isPrimaPubblicazione();
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		if (!checkIfUserExists(c.getUsername())) return false;
		return archivio.checkCredenzialiCorrette(c);
	}
	
	public boolean checkPrimoAccesso (ControllerUtente gu) {
		if (!checkIfUserExists(usernameLinkati.get(gu))) return false;
		else return (archivio.checkPrimoAccesso(usernameLinkati.get(gu))); 
	}
	
	public boolean indicaDatePrecluse (ControllerUtente gu, String date) { //ok
		if (archivio.isPrimaPubblicazione() || getTipoUtente(usernameLinkati.get(gu)) != CostantiStruttura.CONFIGURATORE) return false;
		if (!Time.isValidDate(date)) return false;
		if (Time.isThisDateInMonthPlus3(date)) return archivio.indicaDatePrecluse(date);
		else return false;
	}
	
	public boolean rimuoviPrenotazione(ControllerUtente gu, String codicePrenotazione) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.FRUITORE)
			return archivio.rimuoviPrenotazione(usernameLinkati.get(gu), codicePrenotazione);
		else return false;
	}
	
	public List<VisitaDTO> visiteConfermateVolontario (ControllerUtente gu) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.VOLONTARIO) 
			return archivio.visiteConfermateVolontario(usernameLinkati.get(gu));
		else return null;
	}
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (ControllerUtente gu) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.FRUITORE)
			return archivio.getElencoPrenotazioniFruitore(usernameLinkati.get(gu));
		else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore() { 
		return archivio.getElencoVisiteProposteConfermateCancellateFruitore();
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (ControllerUtente gu) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.FRUITORE)
			return archivio.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(usernameLinkati.get(gu));
		else return null;
	}

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (ControllerUtente gu) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.getElencoVisiteProposteCompleteConfermateCancellateEffettuate();
		else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date) {
		return archivio.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date);
	}
	
	public String inserisciPrenotazione (ControllerUtente gu, PrenotazioneDTO prenotazione) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.FRUITORE) 
			return archivio.inserisciPrenotazione(usernameLinkati.get(gu), prenotazione);
		else return null;
	}
	
	public boolean cambiaCredenziali (ControllerUtente gu, Credenziali c) {
		if (!checkIfUserExists(usernameLinkati.get(gu))) return false; //non necessario?
		if (checkIfUserExists(c.getUsername())) return false;
		if (archivio.modificaCredenziali(usernameLinkati.get(gu), c)) { 
			archivio.primoAccessoEseguito(c.getUsername());
			usernameLinkati.put(gu, c.getUsername());
			gu.setUsername(c.getUsername());
			return true;
		}	
		return false;
	}
	
	public boolean checkIfPlaceExists (String luogo) {
		return archivio.checkIfPlaceExists(luogo);
	}
	
	public boolean aggiungiLuogo (ControllerUtente gu, String tag, String nome, String descrizione, String collocazione, Set<String> tipiVisitaVal) {
		if (canAddOrRemove(usernameLinkati.get(gu)))
			return archivio.aggiungiLuogo(tag, nome, descrizione, collocazione, tipiVisitaVal);
		else return false;
	}
	
	public void setPrimaConfigurazione() {
		archivio.setPrimaConfigurazione();
	}
	
	public boolean aggiungiTipoVisite (ControllerUtente gu, String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal) {
		if (canAddOrRemove(usernameLinkati.get(gu)))
			return archivio.tryAggiungiVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, 
					dataInizio, dataFine, giorniPrenotabiliVal, oraInizio, durataVisita, daAcquistare, 
					minFruitore, maxFruitore, volontariVal);
		else return false;
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
	
	public void impostaAmbitoTerritoriale (String s, ControllerUtente gu) {
		if (archivio.isPrimaConfigurazione() && getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) archivio.impostaAmbitoTerritoriale(s);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(ControllerUtente gu, String username, String password) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) return archivio.impostaCredenzialiNuovoConfiguratore(username, password);
		else return false;
	}
	
	public boolean modificaMaxPrenotazione (ControllerUtente gu, int max) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE) return archivio.impostaMaxPrenotazione(max);
		else return false;
	}

	public Set<UserDTO> getListaUser(ControllerUtente gu, int tipo_user) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE)
			return archivio.getListaUser(tipo_user);
		else return null;
	}
	
	public List<String> getElencoLuoghiVisitabili (ControllerUtente gu) { 
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE)
			return archivio.getElencoLuoghiVisitabili();
		else return null;
	}
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (ControllerUtente gu) {
		if (getTipoUtente(usernameLinkati.get(gu)) == CostantiStruttura.CONFIGURATORE)
		return archivio.getElencoTipiVisiteLuogo();
		else return null;
	}
	
}
