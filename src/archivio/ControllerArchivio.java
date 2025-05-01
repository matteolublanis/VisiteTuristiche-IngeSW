package archivio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import archivio.repository.Archivio;
import dto.*;
import utility.CodiceGenerator;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.Time;

public class ControllerArchivio implements ArchivioFacade {
	//Precondizione per ogni metodo di modifica Archivio del configuratore: canAddOrRemove (a parte aggiungi Configuratore, imposta max prenotazione)
	//Precondizione per tutto: param != null
	
	private Map<String, String> usernameLinkati = new HashMap<>(); 
	private Archivio archivio;
	public ControllerArchivio (Archivio archivio) {
		this.archivio = archivio; 
	}
	
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
		return archivio.checkPrimoAvvio();
	}
	
	public int getTipoUtente (String connectionCode) { //OK
		return archivio.getTipoUtente(usernameLinkati.get(connectionCode));
	}
	
	public boolean pubblicaPiano(String connectionCode) { 
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.tryPubblicaPiano();
		else return false;
	}
	
	public boolean chiudiRaccoltaDisponibilita (String connectionCode) { //OK
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.tryChiudiRaccoltaDisponibilita();
		else return false;
	}
	
	public boolean apriRaccoltaDisponibilita(String connectionCode) {  //OK
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.tryApriRaccoltaDisponibilita();
		else return false;
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String connectionCode, String volontario, String tipoVisita) { //OK
		if ( getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE
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
	
    public boolean inserisciDisponibilita(String connectionCode, String data) { //OK
    	 if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.VOLONTARIO) 
    		 return archivio.inserisciDisponibilita(data, usernameLinkati.get(connectionCode));
    	 else return false;
	}
	
	public Map<String, List<String>> getDatePerDisponibilita(String connectionCode) {	 //OK
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.VOLONTARIO) 
			return archivio.getDatePerDisponibilita(usernameLinkati.get(connectionCode));
		else return null;
	}

	public Set<String> getElencoTipiVisite (String connectionCode) { //OK
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.getElencoTipiVisite();
		else return null;
 	}

 	public List<String> getElencoTipiVisiteVolontario (String connectionCode) { 
 		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.VOLONTARIO) 
 			return archivio.getElencoTipiVisiteVolontario(usernameLinkati.get(connectionCode));
 		else return null;
 	}
	
	public boolean impostaCredenzialiNuovoVolontario (String connectionCode, String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.tryImpostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, tipiVisitaNecessario);
		else return false;
	}
	
	public boolean rimuoviLuogo (String luogo, String connectionCode) {
		if (archivio.checkIfPlaceExists(luogo) && canAddOrRemove(usernameLinkati.get(connectionCode))) return archivio.rimuoviLuogo(luogo);
		else return false;
	}
	
	public boolean rimuoviVolontario (String volontario, String connectionCode) {
		if (archivio.checkIfUserExists(volontario) && archivio.getTipoUtente(volontario) == CostantiStruttura.VOLONTARIO && canAddOrRemove(usernameLinkati.get(connectionCode))) return archivio.rimuoviVolontario(volontario);
		else return false;
	}
	
	public boolean rimuoviTipo (String tipo, String connectionCode) {
		if (archivio.checkIfVisitTypeExists(tipo) && canAddOrRemove(usernameLinkati.get(connectionCode))) return archivio.rimuoviTipo(tipo);
		else return false;
	}
	
	public boolean canAddOrRemove(String connectionCode) {
		if (checkIfUserExists(usernameLinkati.get(connectionCode)) && getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
			return archivio.canAddOrRemove();
		}
		else return false;
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String connectionCode) {
		if (archivio.getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
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
	
	public boolean isReleaseOrLaterDay(String connectionCode) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
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
	
	public boolean checkPrimoAccesso (String connectionCode) {
		if (!checkIfUserExists(usernameLinkati.get(connectionCode))) return false;
		else return (archivio.checkPrimoAccesso(usernameLinkati.get(connectionCode))); 
	}
	
	//Precondizione: pianoPubblicato prima volta
	public boolean indicaDatePrecluse (String connectionCode, String date) { //ok
		if (archivio.isPrimaPubblicazione() || getTipoUtente(usernameLinkati.get(connectionCode)) != CostantiStruttura.CONFIGURATORE) return false;
		if (!Time.isValidDate(date)) return false;
		if (Time.isThisDateInMonthPlus3(date)) return archivio.indicaDatePrecluse(date);
		else return false;
	}
	
	public boolean rimuoviPrenotazione(String connectionCode, String codicePrenotazione) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.FRUITORE)
			return archivio.rimuoviPrenotazione(usernameLinkati.get(connectionCode), codicePrenotazione);
		else return false;
	}
	
	public List<VisitaDTO> visiteConfermateVolontario (String connectionCode) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.VOLONTARIO) 
			return archivio.visiteConfermateVolontario(usernameLinkati.get(connectionCode));
		else return null;
	}
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String connectionCode) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.FRUITORE)
			return archivio.getElencoPrenotazioniFruitore(usernameLinkati.get(connectionCode));
		else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore() { 
		return archivio.getElencoVisiteProposteConfermateCancellateFruitore();
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String connectionCode) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.FRUITORE)
			return archivio.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(usernameLinkati.get(connectionCode));
		else return null;
	}

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (String connectionCode) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return archivio.getElencoVisiteProposteCompleteConfermateCancellateEffettuate();
		else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date) {
		return archivio.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date);
	}
	
	public String inserisciPrenotazione (String connectionCode, PrenotazioneDTO prenotazione) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.FRUITORE) 
			return archivio.inserisciPrenotazione(usernameLinkati.get(connectionCode), prenotazione);
		else return null;
	}
	
	public boolean cambiaCredenziali (String connectionCode, Credenziali c) {
		if (!checkIfUserExists(usernameLinkati.get(connectionCode))) return false; //non necessario?
		if (checkIfUserExists(c.getUsername())) return false;
		if (archivio.modificaCredenziali(usernameLinkati.get(connectionCode), c)) { 
			archivio.primoAccessoEseguito(c.getUsername());
			usernameLinkati.put(connectionCode, c.getUsername());
			return true;
		}	
		return false;
	}
	
	public boolean checkIfPlaceExists (String luogo) {
		return archivio.checkIfPlaceExists(luogo);
	}
	
	public boolean aggiungiLuogo (String connectionCode, String tag, String nome, String descrizione, String collocazione, Set<String> tipiVisitaVal) {
		if (canAddOrRemove(usernameLinkati.get(connectionCode)))
			return archivio.aggiungiLuogo(tag, nome, descrizione, collocazione, tipiVisitaVal);
		else return false;
	}
	//Usato da impostaAmbito, può essere riusato
	public void setPrimaConfigurazione() {
		archivio.setPrimaConfigurazione();
	}
	
	public boolean aggiungiTipoVisite (String connectionCode, String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal) {
		if (canAddOrRemove(usernameLinkati.get(connectionCode)))
			return archivio.tryAggiungiVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, 
					dataInizio, dataFine, giorniPrenotabiliVal, oraInizio, durataVisita, daAcquistare, 
					minFruitore, maxFruitore, volontariVal);
		else return false;
	}
	
	public boolean checkIfUserExists (String username) {
		return archivio.checkIfUserExists(username);
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		return archivio.checkIfVisitTypeExists(tipo);
	}
	
	public void impostaAmbitoTerritoriale (String s, String connectionCode) {
		if (archivio.isPrimaConfigurazione() && getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) archivio.impostaAmbitoTerritoriale(s);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String connectionCode, String username, String password) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) return archivio.impostaCredenzialiNuovoConfiguratore(username, password);
		else return false;
	}
	
	public boolean modificaMaxPrenotazione (String connectionCode, int max) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE) return archivio.impostaMaxPrenotazione(max);
		else return false;
	}

	public Set<UserDTO> getListaUser(String connectionCode, int tipo_user) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE)
			return archivio.getListaUser(tipo_user);
		else return null;
	}
	
	public List<String> getElencoLuoghiVisitabili (String connectionCode) { 
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE)
			return archivio.getElencoLuoghiVisitabili();
		else return null;
	}
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String connectionCode) {
		if (getTipoUtente(usernameLinkati.get(connectionCode)) == CostantiStruttura.CONFIGURATORE)
		return archivio.getElencoTipiVisiteLuogo();
		else return null;
	}

	@Override
	public boolean createNewFruitore(Credenziali c) {
		return archivio.impostaCredenzialiNuovoFruitore(c.getUsername(), c.getPassword());
	}
	
}
