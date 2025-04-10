package archivio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import dto.PrenotazioneDTO;
import dto.VisitaDTO;
import utility.JSONUtility;
import utility.Time;

public class PianoVisiteJSONManagement {
	
	private static final String PROPOSTA = "proposta", CANCELLATA = "cancellata", CONFERMATA = "confermata", COMPLETA = "completa", EFFETTUATA = "effettuata";
	private static final String PATH_VISITE = "json/piano_visite.json";
	private JSONObject jsonPianoVisite = null; 
	private static final String LAST_CHECK = "last-check";
	private static final String STATO_VISITA = "stato";
	private static final String LUOGO = "luogo";
	private static final String PRENOTAZIONI = "prenotazioni";
	private static final String NUMERO_ISCRITTI = "numero-iscritti";
	private static final String 
			VOLONTARI2 = "volontari";
	
	public PianoVisiteJSONManagement () {
		if (JSONUtility.readJsonFile(PATH_VISITE) == null) {
			JSONObject pianoVisite = new JSONObject();
			pianoVisite.put(LAST_CHECK, Time.getActualDate());
			JSONUtility.aggiornaJsonFile(pianoVisite, PATH_VISITE, 10);
			jsonPianoVisite = JSONUtility.readJsonFile(PATH_VISITE); 
		}
		else jsonPianoVisite = JSONUtility.readJsonFile(PATH_VISITE); 
	}
	
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
	
	public boolean pubblicaPiano(JSONObject disponibilita, Map<String, String> tipiVisiteLuoghi) {
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
						visita.put(LUOGO, tipiVisiteLuoghi.get(tipo));
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
					visita.put(LUOGO, tipiVisiteLuoghi.get(tipo));
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
						if (visita.getString(STATO_VISITA).equals(CONFERMATA)) { 
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

	public boolean prenotazioneInseribile(String username, PrenotazioneDTO prenotazione, int maxPrenotazione, int max_fruitore) {
			if (prenotazione.getNum_da_prenotare() > maxPrenotazione) return false;
			if (!jsonPianoVisite.has(prenotazione.getGiorno())) return false;
			JSONObject dayVisits = jsonPianoVisite.getJSONObject(prenotazione.getGiorno());
			if (!dayVisits.has(prenotazione.getTag_visita())) return false;
			JSONObject visita = dayVisits.getJSONObject(prenotazione.getTag_visita());
			if (!visita.getString(STATO_VISITA).equals(PROPOSTA)) return false;
			if (visita.getInt(NUMERO_ISCRITTI) + prenotazione.getNum_da_prenotare() > max_fruitore) return false;
			return true;
	}
	
	public void inserisciPrenotazione (String username, PrenotazioneDTO prenotazione, int max_fruitore, String codicePrenotazione) {
		JSONObject visita = jsonPianoVisite.getJSONObject(prenotazione.getGiorno()).getJSONObject(prenotazione.getTag_visita());
		visita.put(NUMERO_ISCRITTI, visita.getInt(NUMERO_ISCRITTI) + prenotazione.getNum_da_prenotare());
		if (visita.getInt(NUMERO_ISCRITTI) == max_fruitore) visita.put(STATO_VISITA, COMPLETA);
		visita.getJSONArray(PRENOTAZIONI).put(codicePrenotazione);
		JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);	
	}
	
	public List<VisitaDTO> visiteConfermateVolontario (String username, Map<String, Integer> prenotazioniJSONManager,
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
		            					giorno, 
		            					tagVisita, 
		                        		prenotazioniJSONManager.get((String)codicePrenotazione)));
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
		if (visita.getString(STATO_VISITA) == COMPLETA) visita.put(STATO_VISITA, PROPOSTA);
		JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);

	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date, TipiVisiteJSONManagement tipiVisiteJSONManager) {	
	    if (!jsonPianoVisite.has(date)) return null;
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
	
	public void setVisiteCancellateConfermate (Map<String, Integer> tipiVisiteMinFruitori) {
		for (String day : jsonPianoVisite.keySet()) {
			if (!day.equals(LAST_CHECK) && Time.isThreeDaysOrLessBefore(Time.getActualDate(), day)) {
				JSONObject visiteGiorno = jsonPianoVisite.getJSONObject(day);
				for (String keyVisita : visiteGiorno.keySet()) {
					JSONObject visita = visiteGiorno.getJSONObject(keyVisita);
					if (visita.getInt(NUMERO_ISCRITTI) >=  tipiVisiteMinFruitori.get(keyVisita) && 
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
	
	public VisitaDTO getVisitaProposteConfermateCancellatePrenotateDalFruitore (String giornoPrenotazione, String tipoVisita, TipiVisiteJSONManagement tipiVisiteJSONManager) {
		JSONObject giornoVisita = jsonPianoVisite.getJSONObject(giornoPrenotazione);
	   	JSONObject visita = giornoVisita.getJSONObject(tipoVisita);
        String statoVisita = visita.getString(STATO_VISITA);

        if (statoVisita.equals(CONFERMATA) || statoVisita.equals(COMPLETA) || statoVisita.equals(CANCELLATA) || statoVisita.equals(PROPOSTA)) { //non completa
            return (tipiVisiteJSONManager.visitaDTOFruitore(tipoVisita, giornoPrenotazione, statoVisita));
        }
        else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (Map<String, String> tipiVisiteTitoli) {
	    List<VisitaDTO> visiteList = new ArrayList<>();

		for (String k : jsonPianoVisite.keySet()) { //giorno
			if (!k.equals(LAST_CHECK)) {
				JSONObject j = jsonPianoVisite.getJSONObject(k); //prende le visite associate a quel giorno
				for (String m : j.keySet()) { //visite del giorno
					JSONObject visita = j.getJSONObject(m);
	                VisitaDTO visitaDTO = new VisitaDTO( 
	                		tipiVisiteTitoli.get(m),
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
