package archivio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
	
	private static final String LAST_CHECK = "last-check";
	private static final String PRENOTAZIONI = "prenotazioni";
	static final String NUMERO_ISCRITTI = "numero-iscritti";
	public static final String PROPOSTA = "proposta", CANCELLATA = "cancellata", CONFERMATA = "confermata", COMPLETA = "completa", EFFETTUATA = "effettuata";
	static int RELEASE_DAY = 16;

	public static final String 
			VOLONTARI2 = "volontari", DESCRIPTION = "descrizione", COLLOCAZIONE = "collocazione",
			MAX_PRENOTAZIONE = "max_prenotazione", STATO_VISITA = "stato", TITOLO = "titolo", LUOGHI = "luoghi",
			TIPO_VISITA = "tipo-visita", 
			PRIMA_CONFIGURAZIONE = "prima_configurazione", NAME = "nome", LUOGO = "luogo",
			
			
			
			PATH_VISITE = "src/archivio/piano_visite.json",
			PATH_AMBITO = "src/archivio/ambito_territoriale.json";
	
	private JSONObject jsonAmbitoTerritoriale = JSONUtility.readJsonFile(PATH_AMBITO);
	private JSONObject jsonPianoVisite = JSONUtility.readJsonFile(PATH_VISITE); 
	public static final String[] GIORNISETTIMANA = new String[] {"lun","mar","mer","gio","ven","sab","dom"};
	private static final String[] CREDENZIALI_CONF_INIZIALE = new String[] {"admin", "admin"};
	private static final int RIGHE_USERS = 5;

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
		return jsonAmbitoTerritoriale.getBoolean(PRIMA_CONFIGURAZIONE);
	}
	
	public void setPrimaConfigurazione () {
		jsonAmbitoTerritoriale.put(PRIMA_CONFIGURAZIONE, false);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
	}
	
	public boolean checkPrimoAvvio () {
		return usersJSONManager.checkPrimoAvvio();
	}
	
	public void impostaAmbitoTerritoriale (String nome) {
		jsonAmbitoTerritoriale.put(NAME, nome);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
	}
	
	public boolean checkIfUserExists(String user) {
		return usersJSONManager.checkIfUserExists(user);
	}
	
	public boolean rimuoviTipo (String k) {
		JSONArray volontariTipo = tipiVisiteJSONManager.getVolontariAssociatiTipoJSONArray(k);
		for (Object volontarioTipo : volontariTipo) {
			usersJSONManager.rimuoviTipoFromVolontario((String) volontarioTipo, k);
		}
		JSONObject luogo = jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(k));
		JSONArray tipiLuogo = luogo.getJSONArray(TIPO_VISITA);
		for (int i = 0 ; i < tipiLuogo.length() ; i++) {
			if (tipiLuogo.getString(i).equals(k)) {
				tipiLuogo.remove(i); //rimuove reference
				break;
			}
		}
		if (checkIfPlaceExists(tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(k)))
			if (checkIfLuogoHasNoVisitType(tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(k))) rimuoviLuogo(tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(k));
		tipiVisiteJSONManager.rimuoviTipo(k);

		return true;
	}
	
	public boolean rimuoviLuogo (String k) {
		JSONObject luogo = jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(k);
		JSONArray tipiLuogo = luogo.getJSONArray(TIPO_VISITA);
		for (Object tipoDaRimuovere : tipiLuogo) {
			JSONArray volontariAssociati = tipiVisiteJSONManager.getVolontariAssociatiTipoJSONArray(k);
			for (Object m : volontariAssociati) {
				usersJSONManager.rimuoviTipoFromVolontario((String)m, (String)tipoDaRimuovere);
			}
			tipiVisiteJSONManager.rimuoviTipo((String)tipoDaRimuovere);
		}
		jsonAmbitoTerritoriale.getJSONObject(LUOGHI).remove(k);
		
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);
		
		return true;
	}
	
	public boolean rimuoviVolontario (String k) {
		JSONArray tipiVolontario = usersJSONManager.getTipiVisitaOfVolontario(k);
		for (Object tipoVolontario : tipiVolontario) { //ciclo sui suoi tipi
			tipiVisiteJSONManager.rimuoviVolontarioDaTipo((String)tipoVolontario, k);
		}
		if (checkIfPlaceExists(tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(k)))
			if (checkIfLuogoHasNoVisitType(tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(k))) rimuoviLuogo(tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(k));
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
		
	    List<VisitaDTO> visiteList = new ArrayList<>();

		for (String k : jsonPianoVisite.keySet()) { //giorno
			if (!k.equals(LAST_CHECK)) {
				JSONObject j = jsonPianoVisite.getJSONObject(k); //prende le visite associate a quel giorno
				for (String m : j.keySet()) { //visite del giorno
					JSONObject visita = j.getJSONObject(m);
	                JSONObject tipoVisita = tipiVisiteJSONManager.getTipoVisitaJSONObject(m);
	                VisitaDTO visitaDTO = new VisitaDTO( 
		                    tipoVisita.getString(TITOLO),
		                    k,
		                    visita.getString(Archivio.LUOGO),
		                    visita.getString(Archivio.STATO_VISITA)
		                );
	                visiteList.add(visitaDTO);
				}
			}
		}
		visiteList.addAll(pianoStoricoJSONManager.getElencoVisiteProposteCompleteConfermateCancellateEffettuate());
		return visiteList;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String username) {
	    List<VisitaDTO> visiteList = new ArrayList<>();
	    if (getTipoUtente(username) != CostantiStruttura.FRUITORE) return null;
	    for (Object codicePrenotazione : usersJSONManager.getElencoPrenotazioniFruitore(username)) {
	    	JSONObject giornoVisita = jsonPianoVisite.getJSONObject(prenotazioniJSONManager.getGiornoPrenotazione((String)codicePrenotazione));
	    	JSONObject visita = giornoVisita.getJSONObject(prenotazioniJSONManager.getTipoVisitaPrenotazione((String)codicePrenotazione));
            String statoVisita = visita.getString(STATO_VISITA);
            if (statoVisita.equals(CONFERMATA) || statoVisita.equals(CANCELLATA) || statoVisita.equals(PROPOSTA)) { //non completa
                visiteList.add(tipiVisiteJSONManager.visitaDTOFruitore(prenotazioniJSONManager.getTipoVisitaPrenotazione((String)codicePrenotazione), 
                		prenotazioniJSONManager.getGiornoPrenotazione((String)codicePrenotazione), 
                		statoVisita));
            }
	    }
	    return visiteList;
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
	    List<VisitaDTO> visiteList = new ArrayList<>();

	    for (String giorno : jsonPianoVisite.keySet()) { // Giorno
	    	if (!giorno.equals(LAST_CHECK)) {
		        JSONObject day = jsonPianoVisite.getJSONObject(giorno); // Visite del giorno
		        for (String tag : day.keySet()) { // Singola visita
		            JSONObject visita = day.getJSONObject(tag);
		            String statoVisita = visita.getString(STATO_VISITA);
		            if (statoVisita.equals(CONFERMATA) || statoVisita.equals(CANCELLATA) || statoVisita.equals(PROPOSTA)) { //non completa
		                visiteList.add(tipiVisiteJSONManager.visitaDTOFruitore(tag, giorno, statoVisita));
		            }
		        }
	    	}
	    }
	    return visiteList;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date) {	
	    List<VisitaDTO> visiteList = new ArrayList<>();
		JSONObject day = jsonPianoVisite.getJSONObject(date); // Visite del giorno
        for (String tag : day.keySet()) { // Singola visita
	            JSONObject visita = day.getJSONObject(tag);
	            String statoVisita = visita.getString(STATO_VISITA);
	            if (statoVisita.equals(CONFERMATA) || statoVisita.equals(CANCELLATA) || statoVisita.equals(PROPOSTA)) { //non completa
	                visiteList.add(tipiVisiteJSONManager.visitaDTOFruitore(tag, date, statoVisita));
	            }
	    }
	    return visiteList;
	}
	
	public String inserisciPrenotazione(String username, PrenotazioneDTO prenotazione) {
		if (getTipoUtente(username) == CostantiStruttura.FRUITORE) {
			if (prenotazione.getNum_da_prenotare() > jsonAmbitoTerritoriale.getInt(MAX_PRENOTAZIONE)) return null;
			if (!jsonPianoVisite.has(prenotazione.getGiorno())) return null;
			JSONObject dayVisits = jsonPianoVisite.getJSONObject(prenotazione.getGiorno());
			if (!dayVisits.has(prenotazione.getTag_visita())) return null;
			JSONObject visita = dayVisits.getJSONObject(prenotazione.getTag_visita());
			int max_fruitore = tipiVisiteJSONManager.getMaxFruitoreVisita(prenotazione.getTag_visita());
			if (!visita.getString(STATO_VISITA).equals(PROPOSTA)) return null;
			if (visita.getInt(NUMERO_ISCRITTI) + prenotazione.getNum_da_prenotare() > max_fruitore) return null;
			String codicePrenotazione = prenotazioniJSONManager.inserisciPrenotazione(prenotazione, username);
			visita.put(NUMERO_ISCRITTI, visita.getInt(NUMERO_ISCRITTI) + prenotazione.getNum_da_prenotare());
			if (visita.getInt(NUMERO_ISCRITTI) == max_fruitore) visita.put(STATO_VISITA, COMPLETA);
			
			visita.getJSONArray(PRENOTAZIONI).put(codicePrenotazione);
			usersJSONManager.inserisciPrenotazioneFruitore(username, codicePrenotazione);
			JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);

			return codicePrenotazione;
		}
		else return null;
	}
	
	public List<VisitaDTO> visiteConfermateVolontario (String username) {
		List<VisitaDTO> visiteList = new ArrayList<>();
	    for (String giorno : jsonPianoVisite.keySet()) { // Giorno
	    	if (!giorno.equals(LAST_CHECK)) {
		        JSONObject day = jsonPianoVisite.getJSONObject(giorno); // Visite del giorno
		        for (String tagVisita : day.keySet()) { // Singola visita
		            JSONObject visita = day.getJSONObject(tagVisita);
		            String statoVisita = visita.getString(STATO_VISITA);
		            if (statoVisita.equals(CONFERMATA)) { 
		            	JSONArray volontari = visita.getJSONArray(VOLONTARI2);
		            	boolean linked = false;
		            	for (int i = 0 ; i < volontari.length() ; i++) {
		            		if (volontari.getString(i).equals(username)) {
		            			linked = true;
		            		}
		            	}
		            	if (linked) {
		            		List<PrenotazioneDTO> prenotazioni = new ArrayList<>();
		            		for (int i = 0 ; i < visita.getJSONArray(PRENOTAZIONI).length() ; i++) {
		            			String codicePrenotazione = visita.getJSONArray(PRENOTAZIONI).getString(i);
		            			prenotazioni.add(new PrenotazioneDTO(codicePrenotazione, 
		            					prenotazioniJSONManager.getGiornoPrenotazione((String)codicePrenotazione), 
		                        		prenotazioniJSONManager.getTipoVisitaPrenotazione((String)codicePrenotazione), 
		                        		prenotazioniJSONManager.getNIscrittiPrenotazione((String)codicePrenotazione)));
		            		}
			                visiteList.add(tipiVisiteJSONManager.visitaDTOVolontario(tagVisita, giorno, statoVisita, prenotazioni));
		            	}
		            	
		            }
		        }
	    	}
	    }
	    return visiteList;
	}
	
	public boolean rimuoviPrenotazione (String username, String codicePrenotazione) {
		if (!prenotazioniJSONManager.containsCodicePrenotazione(codicePrenotazione)) return false;
		if (getTipoUtente(username) == CostantiStruttura.FRUITORE && prenotazioniJSONManager.getLinkedFruitore(codicePrenotazione).equals(username)) {
			
			JSONObject giornoVisite = jsonPianoVisite.getJSONObject(prenotazioniJSONManager.getGiornoPrenotazione(codicePrenotazione));
			JSONObject visita = giornoVisite.getJSONObject(prenotazioniJSONManager.getTipoVisitaPrenotazione(codicePrenotazione));
			JSONArray prenotazioniVisita = visita.getJSONArray(PRENOTAZIONI);		
			for (int i = 0 ; i < prenotazioniVisita.length() ; i ++) {
				if (prenotazioniVisita.getString(i).equals(codicePrenotazione)) {
					prenotazioniVisita.remove(i);
					break;
				}
			}
			visita.put(NUMERO_ISCRITTI, visita.getInt(NUMERO_ISCRITTI) - prenotazioniJSONManager.getNIscrittiPrenotazione(codicePrenotazione));
			
			usersJSONManager.rimuoviPrenotazioneFruitore(username, codicePrenotazione);
			
			prenotazioniJSONManager.aggiornaJsonPrenotazioni();
			JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);
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
			List<String> result = new ArrayList<>();
			try {
				JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
				for (String k : luoghi.toMap().keySet()) {
					JSONObject j = luoghi.getJSONObject(k);
					if (!j.get(TIPO_VISITA).equals("[]")) {
						result.add(j.getString(NAME));
					}
				}
				return result;
			}
			catch (Exception e) {
				System.out.println("Errore nella raccolta dei luoghi visitabili:" + e.getMessage());
				return null;
			}
		}
		else return null;
	}
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String username) {
		
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			HashMap<String, List<String>> result = new HashMap<>();
			try {
				JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(Archivio.LUOGHI);
				for (String nomeLuogo : luoghi.toMap().keySet()) {
					JSONObject infoLuogo = luoghi.getJSONObject(nomeLuogo);
					if (!infoLuogo.get(Archivio.TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
						JSONArray tipiVisite = infoLuogo.getJSONArray(Archivio.TIPO_VISITA);
						List<String> tipiVisiteAssociati = new ArrayList<>();
						for (int i = 0 ; i < tipiVisite.length() ; i++) 
						{
							tipiVisiteAssociati.add(tipiVisiteJSONManager.getTitoloVisita(tipiVisite.get(i).toString()));
						}
						result.put(infoLuogo.getString(NAME), tipiVisiteAssociati);	
					}
				}
				return result;
			}
			catch (Exception e) {
				System.out.println("Errore nella raccolta dei luoghi visitabili:" + e.getMessage());
				return null;
			}
		}
		else return null;
	}
	
	public boolean impostaMaxPrenotazione (int max) {
		try {
			jsonAmbitoTerritoriale.put(MAX_PRENOTAZIONE, max);
			JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
			return true;
		}
		catch (Exception e) {
			System.out.println("Problema nel setting di max prenotazione> " + e.getMessage());
			return false;
		}

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
		for (String day : jsonPianoVisite.keySet()) {
			if (!day.equals(LAST_CHECK) && Time.isThreeDaysOrLessBefore(Time.getActualDate(), day)) {
				JSONObject visiteGiorno = jsonPianoVisite.getJSONObject(day);
				for (String keyVisita : visiteGiorno.keySet()) {
					JSONObject visita = visiteGiorno.getJSONObject(keyVisita);
					if (visita.getInt(NUMERO_ISCRITTI) >=  tipiVisiteJSONManager.getMinFruitoreVisita(keyVisita) && 
							!visita.getString(STATO_VISITA).equals(CANCELLATA)) { //può essere cancellata per altri motivi
						visita.put(STATO_VISITA, CONFERMATA);
					}
					else {
						visita.put(STATO_VISITA, CANCELLATA);
					}
				}
			}
		}
		JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);
	}
	
	public void removeVisiteEffettuateCancellate () {
		if (!isTodayLastCheckPianoVisite()) {
			Set<String> daysToRemove = new HashSet<>();
			for (String day : jsonPianoVisite.keySet()) { //cicla sui giorni
				if (!day.equals(LAST_CHECK) && Time.comesAfter(Time.getActualDate(), day)) { //dovrebbe essere strutturato meglio
					JSONObject visiteGiorno = jsonPianoVisite.getJSONObject(day);
					for (String keyVisita : visiteGiorno.keySet()) {
						JSONObject visita = visiteGiorno.getJSONObject(keyVisita);
						if (visita.getString(STATO_VISITA).equals(CANCELLATA)) {
							
						}
						if (visita.getString(STATO_VISITA).equals(EFFETTUATA)) {
							pianoStoricoJSONManager.inserisciVisitaNelloStorico(visita.getString(LUOGO), keyVisita, day);
						}
					}
					daysToRemove.add(day);
				}
			}
			
			for (String dayToRemove : daysToRemove) jsonPianoVisite.remove(dayToRemove);
			jsonPianoVisite.put(LAST_CHECK, Time.getActualDate());
			JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);
		}
	}
	
	public void setTodayLastCheckPianoVisite () {
		jsonPianoVisite.put(LAST_CHECK, Time.getActualDate());
	}
	
	public boolean isTodayLastCheckPianoVisite () {
		return (jsonPianoVisite.getString(LAST_CHECK).equals(Time.getActualDate()));
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
		for (String usernameVol : disponibilita.keySet()) {
			JSONObject volontarioDisponibilita = disponibilita.getJSONObject(usernameVol);
			for (String data : volontarioDisponibilita.keySet()) {
				if (jsonPianoVisite.has(data)) {
					String tipo = volontarioDisponibilita.getString(data);
					if (jsonPianoVisite.getJSONObject(data).has(tipo)) {
						jsonPianoVisite.getJSONObject(data).getJSONObject(tipo).getJSONArray(VOLONTARI2).put(usernameVol);
					}
					else {
						JSONObject visita = new JSONObject();
						JSONArray volontariAssociati = new JSONArray();
						volontariAssociati.put(usernameVol);
						visita.put(LUOGO, tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(tipo));
						visita.put(STATO_VISITA, PROPOSTA);
						visita.put(VOLONTARI2, volontariAssociati);
						visita.put(NUMERO_ISCRITTI, 0);
						visita.put(PRENOTAZIONI, new JSONArray());
						jsonPianoVisite.getJSONObject(data).put(tipo, visita);
					}
				}
				else {
					jsonPianoVisite.put(data, new JSONObject()); //creo data nel piano
					String tipo = volontarioDisponibilita.getString(data);
					JSONObject visita = new JSONObject();
					JSONArray volontariAssociati = new JSONArray();
					volontariAssociati.put(usernameVol);
					visita.put(LUOGO, tipiVisiteJSONManager.getLuogoAssociatoTipoJSONArray(tipo));
					visita.put(STATO_VISITA, PROPOSTA);
					visita.put(VOLONTARI2, volontariAssociati);
					visita.put(NUMERO_ISCRITTI, 0);
					visita.put(PRENOTAZIONI, new JSONArray());
					jsonPianoVisite.getJSONObject(data).put(tipo, visita);
				}
			}
		}
		
		daPubblicareJSONManager.resetDopoPubblicazione();
		JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);
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
				isUltimaPubblicazioneMeseScorso()) {
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
		
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
		if (luoghi.has(tag)) return false; 
		JSONObject nuovoLuogo = new JSONObject();
		JSONArray tipiVisita = new JSONArray();
	    if (tipiVisitaVal != null) {
	    	for (String tipo : tipiVisitaVal) {
		    	if (!checkIfVisitTypeExists(tipo) && !tipo.equals("")) return false;
		    	else {
		    		if (!tipo.equals("")) tipiVisita.put(tipo); 
		    	}
	    	}
	    } //se null bisogna capire come gestire
	    nuovoLuogo.put(NAME, nome);
		nuovoLuogo.put(COLLOCAZIONE, collocazione);
	    nuovoLuogo.put(TIPO_VISITA, tipiVisita);
	    luoghi.put(tag, nuovoLuogo);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10); 
	    return true;
	}
	
	public boolean tryImpostaCredenzialiNuovoVolontario (String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (checkIfUserExists(username)) return false; //OK
		JSONArray tipiVisite = new JSONArray();
	    if (tipi_visiteVal != null) {
	    	for (String tipo : tipi_visiteVal) {
		    	if (!checkIfVisitTypeExists(tipo) && !tipo.equals("")) return false;
		    	else {
		    		if (!tipo.equals(""))tipiVisite.put(tipo);
		    	}
		    }
	    }
	    if (tipiVisitaNecessario && tipiVisite.length() == 0) return false;
		return impostaCredenzialiNuovoVolontario(username, password, tipiVisite, tipiVisitaNecessario);
	}
	
	public boolean tryAggiungiVisite (String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal) {
		
		JSONArray giorniPrenotabili = new JSONArray();
	    String days = "";
	    for (Integer k : giorniPrenotabiliVal) {
	    	try {
	    		int j = (k);
	    		if (!(j < 1 || j > 7) && !days.contains(GIORNISETTIMANA[j-1])) {
		    		giorniPrenotabili.put(GIORNISETTIMANA[j-1]);
		    		days += GIORNISETTIMANA[j-1] + ",";
		    	}
	    	}
	    	catch (NumberFormatException e) {
	    		return false;
	    	}
	    }
	    if (intersectOtherEventSamePlace (dataInizio, dataFine, oraInizio, durataVisita, days, jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(luogo))) return false;
	    JSONArray volontari = new JSONArray();
	    for (String k : volontariVal) {
    		JSONArray tipi = usersJSONManager.getTipiVisitaOfVolontario(k);
    		if (volontarioAlreadyLinkedForThatDay(dataInizio, dataFine, oraInizio, durataVisita, days, tipi)) return false;
    		volontari.put(k);
	    }
		return aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, giorniPrenotabili, volontari);
	}
	
	public boolean intersectOtherEventSamePlace (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONObject luogo) {
		JSONArray tipiLuogo = luogo.getJSONArray("tipo-visita");
		return tipiVisiteJSONManager.intersectVisitTypeSamePlace(tipiLuogo, dateStart1, dateFinish1, hour1, duration1, days1);
	}
	
	public boolean aggiungiTipoVisite(String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine,  String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, JSONArray giorniPrenotabili, JSONArray volontari) {
		
		usersJSONManager.aggiungiTipoVisiteAVolontari(volontari, tipoVisita);
		tipiVisiteJSONManager.aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, giorniPrenotabili, volontari);

		JSONArray tipiLuogo = jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(luogo).getJSONArray(TIPO_VISITA);
		tipiLuogo.put(tipoVisita);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);
	    return true; 
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
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
		return (luoghi.has(luogo));
	}
	
	public JSONArray getTipiVisitaOfVolontario (String username) {
		return usersJSONManager.getTipiVisitaOfVolontario(username);
	}
	
	public JSONArray getGiorniPrenotabiliJSONArray (JSONObject tipo) {
		return tipiVisiteJSONManager.getGiorniPrenotabiliJSONArray(tipo);
	}
	
	public boolean checkIfLuogoHasNoVisitType (String luogo) {
		
		return jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(luogo).getJSONArray(TIPO_VISITA).length() == 0;
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
