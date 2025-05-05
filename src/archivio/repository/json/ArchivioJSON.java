package archivio.repository.json;

import java.util.ArrayList;
import java.util.List;
import org.json.*;

import archivio.repository.AmbitoRepository;
import archivio.repository.UserRepository;
import archivio.repository.VisitsRepository;
import dto.*;
import utility.Credenziali;
import utility.Time;

public class ArchivioJSON implements AmbitoRepository, UserRepository, VisitsRepository{ //appelle-moi si tu te perds
	
	private PianoStoricoJSONManagement pianoStoricoJSONManager = new PianoStoricoJSONManagement();
	private DaPubblicareJSONManagement daPubblicareJSONManager = new DaPubblicareJSONManagement();
	private PrenotazioniJSONManagement prenotazioniJSONManager = new PrenotazioniJSONManagement();
	private UsersJSONManagement usersJSONManager = new UsersJSONManagement();
	private TipiVisiteJSONManagement tipiVisiteJSONManager = new TipiVisiteJSONManagement();
	private AmbitoTerritorialeJSONManagement ambitoJSONManager = new AmbitoTerritorialeJSONManagement();
	private PianoVisiteJSONManagement pianoVisiteJSONManager = new PianoVisiteJSONManagement();
	protected static int RELEASE_DAY = 16;
	private static final String[] CREDENZIALI_CONF_INIZIALE = new String[] {"admin", "admin"};

	public ArchivioJSON (int RELEASE_DAY) {
		removeVisiteEffettuateCancellate(); 
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
	//Precondizione: username di un volontario
	public boolean rimuoviVolontario (String volontario) {
		JSONArray tipiVolontario = usersJSONManager.getTipiVisitaOfVolontario(volontario);
		for (Object tipoVolontario : tipiVolontario) { //ciclo sui suoi tipi
			 if (tipiVisiteJSONManager.rimuoviVolontarioDaTipo((String)tipoVolontario, volontario) == 0) rimuoviTipo((String)tipoVolontario);
		}
		return usersJSONManager.rimuoviVolontario(volontario);

	}
	
	public List<UserDTO> getListaUser (int tipo_user) {
		return usersJSONManager.getListaUser(tipo_user);

	}
	
	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
	    List<VisitaDTO> visiteList = pianoVisiteJSONManager.getElencoVisiteProposteCompleteConfermateCancellateEffettuate(tipiVisiteJSONManager.getTipiVisitaTitoli());
		visiteList.addAll(pianoStoricoJSONManager.getElencoVisiteProposteCompleteConfermateCancellateEffettuate());
		return visiteList;
	}

	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String username) {
		JSONArray codiciPrenotazione = usersJSONManager.getElencoPrenotazioniFruitore(username);
	    List<VisitaDTO> visiteList = new ArrayList<>();
	    for (Object codicePrenotazione : codiciPrenotazione) {
	    	VisitaDTO visita = pianoVisiteJSONManager.getVisitaProposteConfermateCancellatePrenotateDalFruitore(prenotazioniJSONManager.getGiornoPrenotazione((String)codicePrenotazione),
	    			prenotazioniJSONManager.getTipoVisitaPrenotazione((String)codicePrenotazione), 
	    			tipiVisiteJSONManager);

	    	if (visita != null) visiteList.add(visita);
	    }
	    return visiteList;
	}

	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String username) {
	    List<PrenotazioneDTO> prenotazioneList = new ArrayList<>();
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
	

	public List<VisitaDTO> visiteConfermateVolontario (String username) {
		return pianoVisiteJSONManager.visiteConfermateVolontario(username, prenotazioniJSONManager.prenotazioniNIscritti(), tipiVisiteJSONManager);
	}

	public String inserisciPrenotazione(String username, PrenotazioneDTO prenotazione) {
			if (pianoVisiteJSONManager.prenotazioneInseribile(username, prenotazione, ambitoJSONManager.getMaxPrenotazione(), tipiVisiteJSONManager.getMaxFruitoreVisita(prenotazione.getTag_visita()))) {
				String codicePrenotazione = prenotazioniJSONManager.inserisciPrenotazione(prenotazione, username);
				pianoVisiteJSONManager.inserisciPrenotazione(username, prenotazione, tipiVisiteJSONManager.getMaxFruitoreVisita(prenotazione.getTag_visita()), codicePrenotazione);
				usersJSONManager.inserisciPrenotazioneFruitore(username, codicePrenotazione);		
				return codicePrenotazione;
			}
			else return null;

	}

	public boolean rimuoviPrenotazione (String username, String codicePrenotazione) {
			pianoVisiteJSONManager.rimuoviPrenotazione( 
					codicePrenotazione, 
					prenotazioniJSONManager.getGiornoPrenotazione(codicePrenotazione), 
					prenotazioniJSONManager.getTipoVisitaPrenotazione(codicePrenotazione),
					prenotazioniJSONManager.getNIscrittiPrenotazione(codicePrenotazione));
			usersJSONManager.rimuoviPrenotazioneFruitore(username, codicePrenotazione);
			prenotazioniJSONManager.rimuoviPrenotazione(codicePrenotazione);
			return true;

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
	
	public List<LuogoDTO> getElencoLuoghiVisitabili () { 	
		return ambitoJSONManager.getElencoLuoghiVisitabili();

	}

	public List<LuogoDTO> getElencoTipiVisiteLuogo () {	
		return ambitoJSONManager.getElencoTipiVisiteLuogo(tipiVisiteJSONManager.getTipiVisitaTitoli());

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
		pianoVisiteJSONManager.setVisiteCancellateConfermate(tipiVisiteJSONManager.getTipiVisitaMinFruitori());
	}
	
	public void removeVisiteEffettuateCancellate () {
		pianoVisiteJSONManager.removeVisiteEffettuateCancellate(pianoStoricoJSONManager, prenotazioniJSONManager, usersJSONManager);
		
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

	public boolean pubblicaPiano() {
		JSONObject disponibilita = daPubblicareJSONManager.getDisponibilita();
		pianoVisiteJSONManager.pubblicaPiano(disponibilita, tipiVisiteJSONManager.getTipiVisitaLuoghi());
		daPubblicareJSONManager.resetDopoPubblicazione();
		return true;
	}
	
	public List<TipoVisitaDTO> getElencoTipiVisite () {
		return tipiVisiteJSONManager.getElencoTipiVisite();
	}
	
	public boolean primoAccessoEseguito (String user) {
		return usersJSONManager.primoAccessoEseguito(user);
	}
	
	public boolean isReleaseOrLaterDay() {
		return (RELEASE_DAY <= Time.getActualDateValue(Time.DAY));
	}

	public boolean tryPubblicaPiano() { //OK
		if(isPrimaPubblicazione()) return setPrimaPubblicazione(); 
		if (Time.getActualDateValue(Time.DAY) >= RELEASE_DAY && //SE ULTIMO PIANO PUBBLICATO MESE SCORSO PUBBLICA
				isUltimaPubblicazioneMeseScorso() && !getPossibileDareDisponibilita()) {
			return pubblicaPiano();
		}
		else return false;
	}
	
	public boolean isUltimaPubblicazioneMeseScorso () {
		return ((getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) - 1 && getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR)) ||
				(getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) - 1 + 12 && getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR) - 1));
	}

	public List<DataDisponibilitaDTO> getDatePerDisponibilita(String username) {	 //OK
		List<DataDisponibilitaDTO> result =
				tipiVisiteJSONManager.getDatePerDisponibilitaFromTipiVisite(username, getTipiVisitaOfVolontario(username));
		result.add(new DataDisponibilitaDTO("Date precluse", getDatePrecluse()));
		return result;
		
	}
	
	public boolean aggiungiLuogo (LuogoDTO luogo) {
		JSONArray tipiVisita = new JSONArray();
		if (luogo.getTipiAssociati() != null) {
			if (!checkIfTypeAlreadyExistsInSet(luogo.getTipiAssociati(), tipiVisita)) return false;
	    } 
		return ambitoJSONManager.aggiungiLuogo(luogo);
	}

	public boolean checkIfTypeAlreadyExistsInSet (List<String> tipiVisitaVal, JSONArray tipiVisita) {
    	for (String tipo : tipiVisitaVal) {
	    	if (!checkIfVisitTypeExists(tipo) && !tipo.equals("")) return false;
	    	else {
	    		if (!tipo.equals(""))tipiVisita.put(tipo);
	    	}
	    }
    	return true;
	}
	
	public boolean tryImpostaCredenzialiNuovoVolontario (String username, String password, List<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (checkIfUserExists(username)) return false; 
		JSONArray tipiVisite = new JSONArray();
	    if (tipi_visiteVal != null) {
	    	if (!checkIfTypeAlreadyExistsInSet(tipi_visiteVal, tipiVisite)) return false;
	    }
	    if (tipiVisitaNecessario && tipiVisite.length() == 0) return false;
		return impostaCredenzialiNuovoVolontario(username, password, tipiVisite, tipiVisitaNecessario);
	}
	
	public boolean tryAggiungiVisite (TipoVisitaDTO tipoVisita) {
		/*
		 * String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal
		 */

		JSONArray giorniPrenotabili = tipiVisiteJSONManager.returnGiorniPrenotabili(tipoVisita.getGiorniPrenotabiliVal());
	    if (tipiVisiteJSONManager.intersectVisitTypeSamePlace (ambitoJSONManager.getTipiLuogo(tipoVisita.getLuogo()),
	    		tipoVisita.getDataInizio(), tipoVisita.getDataFine(), tipoVisita.getOraInizio(), tipoVisita.getDurataVisita(), giorniPrenotabili.toString())) return false; //da rimuovere volontari nuovi
	    
		
	    ambitoJSONManager.aggiungiTipoALuogo(tipoVisita.getLuogo(), tipoVisita.getTag());
	    tipiVisiteJSONManager.aggiungiTipoVisite(tipoVisita);
		
	    associaVolontariATipoVisita(tipoVisita.getVolontariVal(), tipoVisita.getTag());
		return true;
	}
	
	public boolean intersectOtherEventSamePlace (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONArray tipiLuogo) {
		return tipiVisiteJSONManager.intersectVisitTypeSamePlace(tipiLuogo, dateStart1, dateFinish1, hour1, duration1, days1);
	}

	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		daPubblicareJSONManager.setPossibilitaDareDisponibilitaVolontari(b);
	}
	
	public List<TipoVisitaDTO> getElencoTipiVisiteVolontario(String volontario) {
		return usersJSONManager.getElencoTipiVisiteVolontario(volontario, tipiVisiteJSONManager.getJSONTipiVisite());
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
	
	public boolean checkIfLuogoHasNoVisitType (String luogo) {	
		return ambitoJSONManager.checkIfLuogoHasNoVisitType(luogo);
	}
	
	public boolean checkIfVolontarioHasNoVisitType (String username) { 
		return usersJSONManager.checkIfVolontarioHasNoVisitType(username);
	}

	public boolean inserisciDisponibilita(String data, String username) { 
		return daPubblicareJSONManager.inserisciDisponibilita(data, username, getDatePerDisponibilita(username));
	}
	

	public Credenziali getCredenzialiConfIniziale() { 
		return new Credenziali(CREDENZIALI_CONF_INIZIALE[0], CREDENZIALI_CONF_INIZIALE[1]);
	}

	@Override
	public boolean associaVolontariATipoVisita(List<Credenziali> volontari, String tipoVisita) {
		int i = 0;
		for (Credenziali volontario : volontari) {
			if (!usersJSONManager.checkIfUserExists(volontario.getUsername())) {
				usersJSONManager.impostaCredenzialiNuovoVolontario(volontario.getUsername(), volontario.getPassword(), new JSONArray());
				associaVolontarioEsistenteATipoVisitaEsistente(volontario.getUsername(), tipoVisita);
				i++;
			}
			else {
				if (checkIfCanLinkVolontario(volontario.getUsername(), tipoVisita)) {
					associaVolontarioEsistenteATipoVisitaEsistente(volontario.getUsername(), tipoVisita);
					i++;
				}
			}
		}
		if (i == volontari.size()) return true;
		else return false;
	}

	@Override
	public PrenotazioneDTO getPrenotazione(String codicePrenotazione) {
		return prenotazioniJSONManager.getPrenotazione(codicePrenotazione);
	}

	@Override
	public String linkedUserToPrenotazione(String codicePrenotazione) {
		return prenotazioniJSONManager.getLinkedFruitore(codicePrenotazione);
	}

}
