package archivio.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import archivio.AmbitoManager;
import archivio.AppManager;
import archivio.CredenzialiManager;
import archivio.UserInfoManager;
import archivio.repository.AmbitoRepository;
import archivio.repository.UserRepository;
import archivio.repository.VisitsRepository;
import archivio.repository.json.ArchivioJSON;
import dto.*;
import utility.CodiceGenerator;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.Time;

public class ControllerArchivio implements AmbitoManager, AppManager, CredenzialiManager, UserInfoManager {
	//Precondizione per ogni metodo di modifica Archivio del configuratore: canAddOrRemove (a parte aggiungi Configuratore, imposta max prenotazione)
	//Precondizione per tutto: param != null
	
	private Map<String, String> usernameLinkati = new HashMap<>(); 
	private AmbitoRepository ambitoRep;
	private UserRepository userRep;
	private VisitsRepository visitRep;
	public ControllerArchivio (ArchivioJSON archivio) {
		ambitoRep = archivio;
		userRep = archivio;
		visitRep = archivio;
	}
	
	private String getLinkedUsername (String connectionCode) { return usernameLinkati.get(connectionCode); } 
	
	public String makeConnection(Credenziali c) {
		if (checkIfUserExists(c.getUsername())) if (!checkCredenzialiCorrette(c)) return null;
		if (!checkIfUserExists(c.getUsername())) {
			createNewFruitore(c);
		}
		if (usernameLinkati.values().contains(c.getUsername())) return null;
		else {
			while (true) {
				String connectionCode = CodiceGenerator.generateCode();
				if (!usernameLinkati.containsKey(connectionCode)) {
					usernameLinkati.put(connectionCode, c.getUsername());
					return connectionCode;
				}
			}

		}
	}

	public boolean checkPrimoAvvio () { //OK
		return ambitoRep.checkPrimoAvvio();
	}
	
	public int getTipoUtente (String username) { //OK
		return userRep.getTipoUtente(username);
	}
	
	@Override
	public int getTipoLinkato(String connectionCode) {
		return userRep.getTipoUtente(getLinkedUsername(connectionCode));
	}
	
	public boolean pubblicaPiano(String connectionCode) { 
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return visitRep.tryPubblicaPiano();
		else return false;
	}
	
	public boolean chiudiRaccoltaDisponibilita (String connectionCode) { //OK
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return visitRep.tryChiudiRaccoltaDisponibilita();
		else return false;
	}
	
	public boolean apriRaccoltaDisponibilita(String connectionCode) {  //OK
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return visitRep.tryApriRaccoltaDisponibilita();
		else return false;
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String connectionCode, String volontario, String tipoVisita) { //OK
		if ( getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE
				&& checkIfUserExists(volontario) && checkIfVisitTypeExists(tipoVisita)) {
			if (userRep.checkIfCanLinkVolontario(volontario, tipoVisita)) {
				return userRep.associaVolontarioEsistenteATipoVisitaEsistente(volontario, tipoVisita);
			}
			else {
				return false;
			}
		}
		else return false;
	}
	
	public boolean getPossibilitaDareDisponibilita() { 
		return visitRep.getPossibileDareDisponibilita();
	}
	
    public boolean inserisciDisponibilita(String connectionCode, String data) { //OK
    	 if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.VOLONTARIO) 
    		 return userRep.inserisciDisponibilita(data, usernameLinkati.get(connectionCode));
    	 else return false;
	}
	
	public List<DataDisponibilitaDTO> getDatePerDisponibilita(String connectionCode) {	 //OK
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.VOLONTARIO) 
			return userRep.getDatePerDisponibilita(usernameLinkati.get(connectionCode));
		else return null;
	}

	public List<TipoVisitaDTO> getElencoTipiVisite (String connectionCode) { //OK
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return ambitoRep.getElencoTipiVisite();
		else return null;
 	}

 	public List<TipoVisitaDTO> getElencoTipiVisiteVolontario (String connectionCode) { 
 		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.VOLONTARIO) 
 			return userRep.getElencoTipiVisiteVolontario(usernameLinkati.get(connectionCode));
 		else return null;
 	}
	
	public boolean impostaCredenzialiNuovoVolontario (String connectionCode, String username, String password, List<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return userRep.tryImpostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, tipiVisitaNecessario);
		else return false;
	}
	
	public boolean rimuoviLuogo (String luogo, String connectionCode) {
		if (ambitoRep.checkIfPlaceExists(luogo) && canAddOrRemoveCheckUsername(getLinkedUsername(connectionCode))) return ambitoRep.rimuoviLuogo(luogo);
		else return false;
	}
	
	public boolean rimuoviVolontario (String volontario, String connectionCode) {
		if (userRep.checkIfUserExists(volontario) && userRep.getTipoUtente(volontario) == CostantiStruttura.VOLONTARIO && canAddOrRemoveCheckUsername(getLinkedUsername(connectionCode))) return userRep.rimuoviVolontario(volontario);
		else return false;
	}
	
	public boolean rimuoviTipo (String tipo, String connectionCode) {
		if (ambitoRep.checkIfVisitTypeExists(tipo) && canAddOrRemoveCheckUsername(getLinkedUsername(connectionCode))) return ambitoRep.rimuoviTipo(tipo);
		else return false;
	}
	
	public boolean canAddOrRemove(String connectionCode) {
		if (checkIfUserExists(getLinkedUsername(connectionCode)) && getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
			return userRep.canAddOrRemove();
		}
		else return false;
	}
	
	private boolean canAddOrRemoveCheckUsername(String username) {
		if (checkIfUserExists(username) && getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return userRep.canAddOrRemove();
		}
		else return false;
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String connectionCode) {
		if (userRep.getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
			return ambitoRep.isPrimaConfigurazione();
		}
		else return false;
	}
	
	public Credenziali getCredenzialiIniziali () {
		if (checkPrimoAvvio()) {
			ambitoRep.setPrimoAvvio();
			return (userRep.getCredenzialiConfIniziale()); 
		}
		else return null; 
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		visitRep.setPossibilitaDareDisponibilitaVolontari(b);
	}
	
	public boolean isReleaseOrLaterDay(String connectionCode) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return visitRep.isReleaseOrLaterDay();
		else return false;
	}
	
	public boolean isPrimaPubblicazione () {
		return visitRep.isPrimaPubblicazione();
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		if (!checkIfUserExists(c.getUsername())) return false;
		return userRep.checkCredenzialiCorrette(c);
	}
	
	public boolean checkPrimoAccesso (String connectionCode) {
		if (!checkIfUserExists(getLinkedUsername(connectionCode))) return false;
		else return (userRep.checkPrimoAccesso(getLinkedUsername(connectionCode))); 
	}
	
	//Precondizione: pianoPubblicato prima volta
	public boolean indicaDatePrecluse (String connectionCode, String date) { //ok
		if (visitRep.isPrimaPubblicazione() || getTipoUtente(getLinkedUsername(connectionCode)) != CostantiStruttura.CONFIGURATORE) return false;
		if (!Time.isValidDate(date)) return false;
		if (Time.isThisDateInMonthPlus3(date)) return visitRep.indicaDatePrecluse(date);
		else return false;
	}
	
	public boolean rimuoviPrenotazione(String connectionCode, String codicePrenotazione) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.FRUITORE)
			return userRep.rimuoviPrenotazione(usernameLinkati.get(connectionCode), codicePrenotazione);
		else return false;
	}
	
	public List<VisitaDTO> visiteConfermateVolontario (String connectionCode) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.VOLONTARIO) 
			return userRep.visiteConfermateVolontario(usernameLinkati.get(connectionCode));
		else return null;
	}
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String connectionCode) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.FRUITORE)
			return userRep.getElencoPrenotazioniFruitore(usernameLinkati.get(connectionCode));
		else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore() { 
		return userRep.getElencoVisiteProposteConfermateCancellateFruitore();
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String connectionCode) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.FRUITORE)
			return userRep.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(usernameLinkati.get(connectionCode));
		else return null;
	}

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (String connectionCode) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return visitRep.getElencoVisiteProposteCompleteConfermateCancellateEffettuate();
		else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date) {
		return userRep.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date);
	}
	
	public String inserisciPrenotazione (String connectionCode, PrenotazioneDTO prenotazione) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.FRUITORE) 
			return userRep.inserisciPrenotazione(usernameLinkati.get(connectionCode), prenotazione);
		else return null;
	}
	
	public boolean cambiaCredenziali (String connectionCode, Credenziali c) {
		if (!checkIfUserExists(getLinkedUsername(connectionCode))) return false; //non necessario?
		if (checkIfUserExists(c.getUsername())) return false;
		if (userRep.modificaCredenziali(usernameLinkati.get(connectionCode), c)) { 
			userRep.primoAccessoEseguito(c.getUsername());
			usernameLinkati.put(connectionCode, c.getUsername());
			return true;
		}	
		return false;
	}
	
	public boolean checkIfPlaceExists (String luogo) {
		return ambitoRep.checkIfPlaceExists(luogo);
	}
	
	public boolean aggiungiLuogo (String connectionCode, LuogoDTO luogo) {
		if (canAddOrRemoveCheckUsername(getLinkedUsername(connectionCode)))
			return ambitoRep.aggiungiLuogo(luogo);
		else return false;
	}
	//Usato da impostaAmbito, può essere riusato
	public void setPrimaConfigurazione() {
		ambitoRep.setPrimaConfigurazione();
	}
	
	public boolean aggiungiTipoVisite (TipoVisitaDTO tipoVisita, String connectionCode) {
		if (canAddOrRemoveCheckUsername(getLinkedUsername(connectionCode)))
			return ambitoRep.tryAggiungiVisite(tipoVisita);
		else return false;
	}
	
	public boolean checkIfUserExists (String username) {
		return userRep.checkIfUserExists(username);
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		return ambitoRep.checkIfVisitTypeExists(tipo);
	}
	
	public void impostaAmbitoTerritoriale (String s, String connectionCode) {
		if (ambitoRep.isPrimaConfigurazione() 
				&& getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			ambitoRep.impostaAmbitoTerritoriale(s);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String connectionCode, String username, String password) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return userRep.impostaCredenzialiNuovoConfiguratore(username, password);
		else return false;
	}
	
	public boolean modificaMaxPrenotazione (String connectionCode, int max) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return ambitoRep.impostaMaxPrenotazione(max);
		else return false;
	}

	public List<UserDTO> getListaUser(String connectionCode, int tipo_user) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE)
			return userRep.getListaUser(tipo_user);
		else return null;
	}
	
	public List<LuogoDTO> getElencoLuoghiVisitabili (String connectionCode) { 
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE)
			return ambitoRep.getElencoLuoghiVisitabili();
		else return null;
	}
	
	public List<LuogoDTO> getElencoTipiVisiteLuogo (String connectionCode) {
		if (getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE)
		return ambitoRep.getElencoTipiVisiteLuogo();
		else return null;
	}

	@Override
	public boolean createNewFruitore(Credenziali c) {
		return userRep.impostaCredenzialiNuovoFruitore(c.getUsername(), c.getPassword());
	}

	@Override
	public boolean associaVolontariATipoVisitaEsistente(String connectionCode, List<Credenziali> volontari,
			String tipoVisita) {
		if (canAddOrRemoveCheckUsername(getLinkedUsername(connectionCode))) 
			return userRep.associaVolontariATipoVisita(volontari, tipoVisita);
		else return false;
	}
	
}
