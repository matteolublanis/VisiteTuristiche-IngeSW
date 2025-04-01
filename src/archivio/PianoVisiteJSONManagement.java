package archivio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import dto.PrenotazioneDTO;
import dto.VisitaDTO;
import utility.JSONUtility;
import utility.Time;

public class PianoVisiteJSONManagement {
	
	private static final String PROPOSTA = "proposta", CANCELLATA = "cancellata", CONFERMATA = "confermata", COMPLETA = "completa", EFFETTUATA = "effettuata";
	private static final String PATH_VISITE = "src/archivio/piano_visite.json";
	private JSONObject jsonPianoVisite = JSONUtility.readJsonFile(PATH_VISITE); 
	private static final String LAST_CHECK = "last-check";
	private static final String STATO_VISITA = "stato";
	private static final String LUOGO = "luogo";
	private static final String PRENOTAZIONI = "prenotazioni";
	private static final String NUMERO_ISCRITTI = "numero-iscritti";
	private static final String 
			VOLONTARI2 = "volontari";
	
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore (TipiVisiteJSONManagement tipiVisiteJSONManager) {	
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
	
	//what a glorious set of stairs we make
	public boolean pubblicaPiano(JSONObject disponibilita, TipiVisiteJSONManagement tipiVisiteJSONManager) {
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
						visita.put(LUOGO, tipiVisiteJSONManager.getLuogoAssociatoTipo(tipo));
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
					visita.put(LUOGO, tipiVisiteJSONManager.getLuogoAssociatoTipo(tipo));
					visita.put(STATO_VISITA, PROPOSTA);
					visita.put(VOLONTARI2, volontariAssociati);
					visita.put(NUMERO_ISCRITTI, 0);
					visita.put(PRENOTAZIONI, new JSONArray());
					jsonPianoVisite.getJSONObject(data).put(tipo, visita);
				}
			}
		}
		JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);
		return true;
	}
	
	public boolean isTodayLastCheckPianoVisite () {
		return (jsonPianoVisite.getString(LAST_CHECK).equals(Time.getActualDate()));
	}
	
	public void removeVisiteEffettuateCancellate (PianoStoricoJSONManagement pianoStoricoJSONManager) {
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
	
	//Questo metodo è estremamente dipendente da altre classi, è da riscrivere e riposizionare
	/*
	 * Fa i controlli, inserisce prenotazione in prenotazioni e in user, poi lo mette nella visita
	 * Il codice viene generato in Prenotazioni
	 */
	public String inserisciPrenotazione(String username, PrenotazioneDTO prenotazione,
			AmbitoTerritorialeJSONManagement ambitoJSONManager, TipiVisiteJSONManagement tipiVisiteJSONManager,
			UsersJSONManagement usersJSONManager, PrenotazioniJSONManagement prenotazioniJSONManager) {
			if (prenotazione.getNum_da_prenotare() > ambitoJSONManager.getMaxPrenotazione()) return null;
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
	
	public List<VisitaDTO> visiteConfermateVolontario (String username, PrenotazioniJSONManagement prenotazioniJSONManager,
			TipiVisiteJSONManagement tipiVisiteJSONManager) {
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
	
	public void rimuoviPrenotazione (String username, String codicePrenotazione, String giorno, String tagTipo, int nIscritti) {
		JSONObject giornoVisite = jsonPianoVisite.getJSONObject(giorno);
		JSONObject visita = giornoVisite.getJSONObject(tagTipo);
		JSONArray prenotazioniVisita = visita.getJSONArray(PRENOTAZIONI);		
		for (int i = 0 ; i < prenotazioniVisita.length() ; i ++) {
			if (prenotazioniVisita.getString(i).equals(codicePrenotazione)) {
				prenotazioniVisita.remove(i);
				break;
			}
		}
		visita.put(NUMERO_ISCRITTI, visita.getInt(NUMERO_ISCRITTI) - nIscritti);
		JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);

	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date, TipiVisiteJSONManagement tipiVisiteJSONManager) {	
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
	
	public void setTodayLastCheckPianoVisite () {
		jsonPianoVisite.put(LAST_CHECK, Time.getActualDate());
	}
	
	public void setVisiteCancellateConfermate (TipiVisiteJSONManagement tipiVisiteJSONManager) {
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
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (JSONArray codiciPrenotazione, TipiVisiteJSONManagement tipiVisiteJSONManager, PrenotazioniJSONManagement prenotazioniJSONManager) {
	    List<VisitaDTO> visiteList = new ArrayList<>();
	    for (Object codicePrenotazione : codiciPrenotazione) {
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
	
	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (TipiVisiteJSONManagement tipiVisiteJSONManager) {
	    List<VisitaDTO> visiteList = new ArrayList<>();

		for (String k : jsonPianoVisite.keySet()) { //giorno
			if (!k.equals(LAST_CHECK)) {
				JSONObject j = jsonPianoVisite.getJSONObject(k); //prende le visite associate a quel giorno
				for (String m : j.keySet()) { //visite del giorno
					JSONObject visita = j.getJSONObject(m);
	                VisitaDTO visitaDTO = new VisitaDTO( 
		                    tipiVisiteJSONManager.getTitoloVisita(m),
		                    k, 
		                    visita.getString(LUOGO),
		                    visita.getString(STATO_VISITA)
		                );
	                visiteList.add(visitaDTO);
				}
			}
		}
		return visiteList;
	}
}
