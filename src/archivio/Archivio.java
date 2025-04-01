package archivio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.*;

import dto.*;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.JSONUtility;
import utility.Time;

public class Archivio {
	
	private PianoStoricoJSONManagement pianoStoricoJSONManager = new PianoStoricoJSONManagement();
	private DaPubblicareJSONManagement daPubblicareJSONManager = new DaPubblicareJSONManagement();
	private PrenotazioniJSONManagement prenotazioniJSONManager = new PrenotazioniJSONManagement();
	private UsersJSONManagement usersJSONManager = new UsersJSONManagement();
	private TipiVisiteJSONManagement tipiVisiteJSONManager = new TipiVisiteJSONManagement();
	private AmbitoTerritorialeJSONManagement ambitoJSONManager = new AmbitoTerritorialeJSONManagement();
	private PianoVisiteJSONManagement pianoVisiteJSONManager = new PianoVisiteJSONManagement();
	static int RELEASE_DAY = 16;
	
	private static final String[] CREDENZIALI_CONF_INIZIALE = new String[] {"admin", "admin"};

	public Archivio () {
		System.out.println("Creato archivio.");
		removeVisiteEffettuateCancellate(); //per come è strutturata l'app, ha senso mettere un controllo ogni volta che avvio
		setVisiteCancellateConfermate();
	}
	
	public boolean getPossibileDareDisponibilita () {
		return daPubblicareJSONManager.getPossibileDareDisponibilita();
	}
	
	public boolean apriRaccoltaDisponibilita() {
		return daPubblicareJSONManager.apriRaccoltaDisponibilita();
	}
	
	public int getUltimoMesePubblicazione() {
		return daPubblicareJSONManager.getUltimoMesePubblicazione();
	}
	
	public int getUltimoAnnoPubblicazione() {
		return daPubblicareJSONManager.getUltimoAnnoPubblicazione();
	}
	
	public boolean canAddOrRemove() {
		return daPubblicareJSONManager.canAddOrRemove();
	}
	
	public boolean chiudiRaccoltaDisponibilita() {
		return daPubblicareJSONManager.chiudiRaccoltaDisponibilita();
	}
	
	public boolean isPrimaConfigurazione () {
		return ambitoJSONManager.isPrimaConfigurazione();
	}
	
	public void setPrimaConfigurazione () {
		ambitoJSONManager.setPrimaConfigurazione();
	}
	
	public boolean checkPrimoAvvio () {
		return usersJSONManager.checkPrimoAvvio();
	}
	
	public void impostaAmbitoTerritoriale (String nome) {
		ambitoJSONManager.impostaAmbitoTerritoriale(nome);
	}
	
	public boolean checkIfUserExists(String user) {
		return usersJSONManager.checkIfUserExists(user);
	}
	
	public boolean rimuoviTipo (String k) {
		JSONArray volontariTipo = tipiVisiteJSONManager.getVolontariAssociatiTipoJSONArray(k);
		for (Object volontarioTipo : volontariTipo) {
			usersJSONManager.rimuoviTipoFromVolontario((String) volontarioTipo, k);
		}
		ambitoJSONManager.rimuoviTipoFromLuogo(tipiVisiteJSONManager.getLuogoAssociatoTipo(k), k);
		tipiVisiteJSONManager.rimuoviTipo(k);
		
		return true;
	}
	
	public boolean rimuoviLuogo (String k) {
		JSONArray tipiLuogo = ambitoJSONManager.getTipiLuogo(k);
		for (Object tipoDaRimuovere : tipiLuogo) {
			JSONArray volontariAssociati = tipiVisiteJSONManager.getVolontariAssociatiTipoJSONArray((String)tipoDaRimuovere);
			for (Object m : volontariAssociati) {
				usersJSONManager.rimuoviTipoFromVolontario((String)m, (String)tipoDaRimuovere);
			}
			tipiVisiteJSONManager.rimuoviTipo((String)tipoDaRimuovere);
		}
		ambitoJSONManager.rimuoviLuogo(k);
		return true;
	}
	
	public boolean rimuoviVolontario (String k) {
		JSONArray tipiVolontario = usersJSONManager.getTipiVisitaOfVolontario(k);
		for (Object tipoVolontario : tipiVolontario) { //ciclo sui suoi tipi
			 if (tipiVisiteJSONManager.rimuoviVolontarioDaTipo((String)tipoVolontario, k) == 0) rimuoviTipo((String)tipoVolontario);
		}
		usersJSONManager.rimuoviVolontario(k);
		return true;
	}
	
	public Set<UserDTO> getListaUser (String username, int tipo_user) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return usersJSONManager.getListaUser(username, tipo_user);
		}
		else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
	    List<VisitaDTO> visiteList = pianoVisiteJSONManager.getElencoVisiteProposteCompleteConfermateCancellateEffettuate(tipiVisiteJSONManager);
		visiteList.addAll(pianoStoricoJSONManager.getElencoVisiteProposteCompleteConfermateCancellateEffettuate());
		return visiteList;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String username) {
		if (getTipoUtente(username) != CostantiStruttura.FRUITORE) return null;
		JSONArray codiciPrenotazione = usersJSONManager.getElencoPrenotazioniFruitore(username);
	    return pianoVisiteJSONManager.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(codiciPrenotazione, tipiVisiteJSONManager, prenotazioniJSONManager);
	}
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String username) {
	    List<PrenotazioneDTO> prenotazioneList = new ArrayList<>();
	    if (getTipoUtente(username) != CostantiStruttura.FRUITORE) return null;
	    for (Object codicePrenotazione : usersJSONManager.getElencoPrenotazioniFruitore(username)) {
                prenotazioneList.add(new PrenotazioneDTO((String)codicePrenotazione, 
                		prenotazioniJSONManager.getGiornoPrenotazione((String)codicePrenotazione), 
                		prenotazioniJSONManager.getTipoVisitaPrenotazione((String)codicePrenotazione), 
                		prenotazioniJSONManager.getNIscrittiPrenotazione((String)codicePrenotazione)));
        }
	    return prenotazioneList;
	    
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore () {	
	    return pianoVisiteJSONManager.getElencoVisiteProposteConfermateCancellateFruitore(tipiVisiteJSONManager);
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date) {	
	    return pianoVisiteJSONManager.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date, tipiVisiteJSONManager);
	}
	
	public String inserisciPrenotazione(String username, PrenotazioneDTO prenotazione) {
		if (getTipoUtente(username) == CostantiStruttura.FRUITORE) {
			return pianoVisiteJSONManager.inserisciPrenotazione(username, prenotazione, ambitoJSONManager, tipiVisiteJSONManager, usersJSONManager, prenotazioniJSONManager);
		}
		else return null;
	}
	
	public List<VisitaDTO> visiteConfermateVolontario (String username) {
		return pianoVisiteJSONManager.visiteConfermateVolontario(username, prenotazioniJSONManager, tipiVisiteJSONManager);
	}
	
	public boolean rimuoviPrenotazione (String username, String codicePrenotazione) {
		if (!prenotazioniJSONManager.containsCodicePrenotazione(codicePrenotazione)) return false;
		if (getTipoUtente(username) == CostantiStruttura.FRUITORE && prenotazioniJSONManager.getLinkedFruitore(codicePrenotazione).equals(username)) {
			pianoVisiteJSONManager.rimuoviPrenotazione(username, 
					codicePrenotazione, 
					prenotazioniJSONManager.getGiornoPrenotazione(codicePrenotazione), 
					prenotazioniJSONManager.getTipoVisitaPrenotazione(codicePrenotazione),
					prenotazioniJSONManager.getNIscrittiPrenotazione(codicePrenotazione));
			usersJSONManager.rimuoviPrenotazioneFruitore(username, codicePrenotazione);
			prenotazioniJSONManager.aggiornaJsonPrenotazioni();
			return true;
		}
		else return false;
	}
	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, JSONArray tipi_visite, boolean tipiVisitaNecessari) {
			tipiVisiteJSONManager.inserisciNuovoVolontarioAssociatoAVisite(tipi_visite, username);
		    usersJSONManager.impostaCredenzialiNuovoVolontario(username, password, tipi_visite);
			return true;
	}
	
	public List<String> getDatePrecluse () {
		return daPubblicareJSONManager.getDatePrecluse();
	}
	
	public boolean checkIfCanLinkVolontario (String volontario, String tipoVisita) {
		JSONArray tipi = usersJSONManager.getTipiVisitaOfVolontario(volontario);
		return tipiVisiteJSONManager.otherVisitTypesIntersect(tipoVisita, tipi);
	}
	
	public boolean volontarioAlreadyLinkedForThatDay (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONArray tipiVisitaVolontario) {
		return tipiVisiteJSONManager.visitTypeIntersectsOtherVisitTypes(dateStart1, dateFinish1, hour1, duration1, days1, tipiVisitaVolontario);
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String volontario, String tipoVisita) {
		usersJSONManager.associaVolontarioEsistenteATipoVisitaEsistente(volontario, tipoVisita);
		tipiVisiteJSONManager.aggiungiVolontarioATipo(tipoVisita, volontario);
		return true;
	}
	
	public boolean impostaCredenzialiNuovoFruitore (String username, String password) {
		return usersJSONManager.impostaCredenzialiNuovoFruitore(username, password);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password) {
		return usersJSONManager.impostaCredenzialiNuovoConfiguratore(username, password);
	}
	
	public List<String> getElencoLuoghiVisitabili (String username) { 	
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return ambitoJSONManager.getElencoLuoghiVisitabili(username);
		}
		else return null;
	}
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String username) {	
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return ambitoJSONManager.getElencoTipiVisiteLuogo(username, tipiVisiteJSONManager);
		}
		else return null;
	}
	
	public boolean impostaMaxPrenotazione (int max) {
		return ambitoJSONManager.impostaMaxPrenotazione(max);

	}
	
	public void setPrimoAvvio () {
		usersJSONManager.setPrimoAvvio();
	}

	public int getTipoUtente (String username) {
		return usersJSONManager.getTipoUtente(username);
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		return usersJSONManager.checkCredenzialiCorrette(c);
	}
	
	public boolean modificaCredenziali (String username, Credenziali c) {
		return usersJSONManager.modificaCredenziali(username, c);
	}
	
	public boolean isUltimoPianoPubblicato () {
		return daPubblicareJSONManager.isUltimoPianoPubblicato();
	}
	
	public void setVisiteCancellateConfermate () {
		pianoVisiteJSONManager.setVisiteCancellateConfermate(tipiVisiteJSONManager);
	}
	
	public void removeVisiteEffettuateCancellate () {
		pianoVisiteJSONManager.removeVisiteEffettuateCancellate(pianoStoricoJSONManager);
	}
	
	public void setTodayLastCheckPianoVisite () {
		pianoVisiteJSONManager.setTodayLastCheckPianoVisite();
	}
	
	public boolean isTodayLastCheckPianoVisite () {
		return pianoVisiteJSONManager.isTodayLastCheckPianoVisite();
	}
	
	public boolean indicaDatePrecluse (String date) {
		return daPubblicareJSONManager.indicaDatePrecluse(date);
	}
	
	public boolean checkPrimoAccesso (String username) {
		return usersJSONManager.checkPrimoAccesso(username);
	}
	
	//what a glorious set of stairs we make
	public boolean pubblicaPiano() {
		JSONObject disponibilita = daPubblicareJSONManager.getDisponibilita();
		pianoVisiteJSONManager.pubblicaPiano(disponibilita, tipiVisiteJSONManager);
		daPubblicareJSONManager.resetDopoPubblicazione();
		return true;
	}
	
	public Set<String> getElencoTipiVisite () {
		return tipiVisiteJSONManager.getElencoTipiVisite();
	}
	
	public boolean primoAccessoEseguito (String user) {
		return usersJSONManager.primoAccessoEseguito(user);
	}
	
	public boolean isReleaseOrLaterDay() {
		return (RELEASE_DAY <= Time.getActualDateValue(Time.DAY));
	}
	
	public boolean tryApriRaccoltaDisponibilita(String username) {  //OK
		
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE && !isPrimaPubblicazione() && Time.getActualDateValue(Time.DAY) >= RELEASE_DAY&&
				((getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) && getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR)))) { //aggiornato quando pubblicato
			if (!isUltimoPianoPubblicato() || getPossibileDareDisponibilita()) return false; //SE ULTIMO PIANO NON PUBBLICATO O GIA' APERTA RITORNA FALSO
			else return apriRaccoltaDisponibilita();
		}
		else {
			return false;
		}
	}
	
	public boolean tryPubblicaPiano(String username) { //OK
		if(isPrimaPubblicazione()) return setPrimaPubblicazione(); 
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE && Time.getActualDateValue(Time.DAY) >= RELEASE_DAY && //SE ULTIMO PIANO PUBBLICATO MESE SCORSO PUBBLICA
				isUltimaPubblicazioneMeseScorso() && !getPossibileDareDisponibilita()) {
			return pubblicaPiano();
		}
		else return false;
	}
	
	public boolean tryChiudiRaccoltaDisponibilita (String username) { //OK
		
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE && 
				!isPrimaPubblicazione() && Time.getActualDateValue(Time.DAY) >= RELEASE_DAY &&
						isUltimaPubblicazioneMeseScorso()) {
			if (getPossibileDareDisponibilita()) return chiudiRaccoltaDisponibilita(); 
			else return false;
		}
		else {
			return false;
		}
	}
	
	private boolean isUltimaPubblicazioneMeseScorso () {
		
		return ((getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) - 1 && getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR)) ||
				(getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) - 1 + 12 && getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR) - 1));
	}
	
	public HashMap<String, List<String>> getDatePerDisponibilita(String username) {	 //OK
		if (getTipoUtente(username) == CostantiStruttura.VOLONTARIO) {
			return tipiVisiteJSONManager.getDatePerDisponibilitaFromTipiVisite(username, getTipiVisitaOfVolontario(username));
		}
		else return null;
	}
	
	public boolean aggiungiLuogo (String tag, String nome, String collocazione, Set<String> tipiVisitaVal) {
		JSONArray tipiVisita = new JSONArray();
		if (tipiVisitaVal != null) {
			if (!checkIfTypeAlreadyExistsInSet(tipiVisitaVal, tipiVisita)) return false;
	    } 
		return ambitoJSONManager.aggiungiLuogo(tag, nome, collocazione, tipiVisita);
	}
	
	public boolean checkIfTypeAlreadyExistsInSet (Set<String> tipiVisitaVal, JSONArray tipiVisita) {
    	for (String tipo : tipiVisitaVal) {
	    	if (!checkIfVisitTypeExists(tipo) && !tipo.equals("")) return false;
	    	else {
	    		if (!tipo.equals(""))tipiVisita.put(tipo);
	    	}
	    }
    	return true;
	}
	
	public boolean tryImpostaCredenzialiNuovoVolontario (String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (checkIfUserExists(username)) return false; //OK
		JSONArray tipiVisite = new JSONArray();
	    if (tipi_visiteVal != null) {
	    	if (!checkIfTypeAlreadyExistsInSet(tipi_visiteVal, tipiVisite)) return false;
	    }
	    if (tipiVisitaNecessario && tipiVisite.length() == 0) return false;
		return impostaCredenzialiNuovoVolontario(username, password, tipiVisite, tipiVisitaNecessario);
	}
	
	public boolean tryAggiungiVisite (String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal) {	
		return (tipiVisiteJSONManager.tryAggiungiVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabiliVal, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, volontariVal, usersJSONManager, ambitoJSONManager));
	}
	
	public boolean intersectOtherEventSamePlace (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONArray tipiLuogo) {
		return tipiVisiteJSONManager.intersectVisitTypeSamePlace(tipiLuogo, dateStart1, dateFinish1, hour1, duration1, days1);
	}
	
	public JSONObject setNewVisitType (String luogo,String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, JSONArray giorniPrenotabili, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, JSONArray volontari) {
		return tipiVisiteJSONManager.setNewVisitType(luogo, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabili, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, volontari);
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		daPubblicareJSONManager.setPossibilitaDareDisponibilitaVolontari(b);
	}
	
	public List<String> getElencoTipiVisiteVolontario(String volontario) {
		return usersJSONManager.getElencoTipiVisiteVolontario(volontario);
 	}
	
	public boolean setPrimaPubblicazione() {
		return daPubblicareJSONManager.setPrimaPubblicazione();
	}
	
	public boolean isPrimaPubblicazione() {
		return daPubblicareJSONManager.isPrimaPubblicazione();
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		return tipiVisiteJSONManager.checkIfVisitTypeExists(tipo);
	}
	
	public boolean checkIfPlaceExists(String luogo) {
		return ambitoJSONManager.checkIfPlaceExists(luogo);
	}
	
	public JSONArray getTipiVisitaOfVolontario (String username) {
		return usersJSONManager.getTipiVisitaOfVolontario(username);
	}
	
	public JSONArray getGiorniPrenotabiliJSONArray (JSONObject tipo) {
		return tipiVisiteJSONManager.getGiorniPrenotabiliJSONArray(tipo);
	}
	
	public boolean checkIfLuogoHasNoVisitType (String luogo) {	
		return ambitoJSONManager.checkIfLuogoHasNoVisitType(luogo);
	}
	
	public boolean checkIfVolontarioHasNoVisitType (String username) { 
		return usersJSONManager.checkIfVolontarioHasNoVisitType(username);
	}
	
	public boolean inserisciDisponibilita(String data, String username) { //ok
		return daPubblicareJSONManager.inserisciDisponibilita(data, username, getDatePerDisponibilita(username));
	}

	public boolean checkValueExistance (String key, String path) { //ok
		JSONObject json = JSONUtility.readJsonFile(path);
		try {
			return !(json.get(key).equals(null)); 
		}
		catch (Exception e) {
			return false;
		}
	}

	public Credenziali getCredenzialiConfIniziale() { //ok
		return new Credenziali(CREDENZIALI_CONF_INIZIALE[0], CREDENZIALI_CONF_INIZIALE[1]);
	}

}
