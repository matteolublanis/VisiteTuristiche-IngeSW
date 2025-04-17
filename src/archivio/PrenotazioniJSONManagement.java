package archivio;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import dto.PrenotazioneDTO;
import utility.CodicePrenotazioneGenerator;
import utility.JSONUtility;

public class PrenotazioniJSONManagement {
	private static final String PATH_PRENOTAZIONI = "json/prenotazioni.json";
	private JSONObject jsonPrenotazioni = null;
	private static final String GIORNO = "giorno";
	private static final String USERNAME = "username";
	private static final String NUMERO_ISCRITTI = "numero-iscritti";
	private static final String TIPO_VISITA = "tipo-visita";

	public PrenotazioniJSONManagement () {
		if (JSONUtility.readJsonFile(PATH_PRENOTAZIONI) == null) {
			JSONUtility.aggiornaJsonFile(new JSONObject(), PATH_PRENOTAZIONI, 10);
			jsonPrenotazioni = JSONUtility.readJsonFile(PATH_PRENOTAZIONI);
		}
		else jsonPrenotazioni = JSONUtility.readJsonFile(PATH_PRENOTAZIONI);
	}
	
	public void removePrenotazioni (JSONArray prenotazioniDaRimuovere, UsersJSONManagement usersJSONManager) {
		for (Object codice : prenotazioniDaRimuovere) {
			String fruitoreAssociato = jsonPrenotazioni.getJSONObject((String) codice).getString(USERNAME);
			usersJSONManager.rimuoviPrenotazioneFruitore(fruitoreAssociato, (String) codice);
			jsonPrenotazioni.remove((String) codice);
		}
		aggiornaJsonPrenotazioni();
	}
	
	public JSONObject getPrenotazioneJSONObject (String codicePrenotazione) {
		return jsonPrenotazioni.getJSONObject(codicePrenotazione);
	}
	
	public String getLinkedFruitore (String codicePrenotazione) {
		return getPrenotazioneJSONObject(codicePrenotazione).getString(USERNAME);
	}
	
	public int getNIscrittiPrenotazione (String codicePrenotazione) {
		return getPrenotazioneJSONObject(codicePrenotazione).getInt(NUMERO_ISCRITTI);
	}
	
	public String getGiornoPrenotazione (String codicePrenotazione) {
		return getPrenotazioneJSONObject(codicePrenotazione).getString(GIORNO);
	}
	
	public String getTipoVisitaPrenotazione (String codicePrenotazione) {
		return getPrenotazioneJSONObject(codicePrenotazione).getString(TIPO_VISITA);
	}
	
	public Map<String, Integer> prenotazioniNIscritti () {
		Map<String, Integer> result = new HashMap<>();
		for (String tag : jsonPrenotazioni.keySet()) result.put(tag, getNIscrittiPrenotazione(tag));
		return result;
	}
	
	public void rimuoviPrenotazione (String codicePrenotazione) {
		jsonPrenotazioni.remove(codicePrenotazione);
		aggiornaJsonPrenotazioni ();	
	}
	
	public boolean containsCodicePrenotazione(String codicePrenotazione) {
		return jsonPrenotazioni.has(codicePrenotazione);
	}
	
	public String inserisciPrenotazione (PrenotazioneDTO prenotazione, String username) {
		JSONObject prenotazioneJSON = new JSONObject();
		prenotazioneJSON.put(GIORNO, prenotazione.getGiorno());
		prenotazioneJSON.put(USERNAME, username);
		prenotazioneJSON.put(NUMERO_ISCRITTI, prenotazione.getNum_da_prenotare());
		prenotazioneJSON.put(TIPO_VISITA, prenotazione.getTag_visita());
		String codicePrenotazione = null;
		do {
			codicePrenotazione = CodicePrenotazioneGenerator.generateBookingCode();
		} while (jsonPrenotazioni.has(codicePrenotazione));
		jsonPrenotazioni.put(codicePrenotazione, prenotazioneJSON);
		aggiornaJsonPrenotazioni ();
		return codicePrenotazione;
	}
	
	public void aggiornaJsonPrenotazioni () {
		JSONUtility.aggiornaJsonFile(jsonPrenotazioni, PATH_PRENOTAZIONI, 10);
	}
}
