package archivio;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import dto.PrenotazioneDTO;
import utility.CodicePrenotazioneGenerator;
import utility.JSONUtility;

public class PrenotazioniJSONManagement {
	private static final String PATH_PRENOTAZIONI = "src/archivio/prenotazioni.json";
	private JSONObject jsonPrenotazioni = JSONUtility.readJsonFile(PATH_PRENOTAZIONI);
	private static final String GIORNO = "giorno";
	private static final String UTENTE = "utente";
	private static final String NUMERO_ISCRITTI = "numero-iscritti";
	private static final String TIPO_VISITA = "tipo-visita";


	public JSONObject getPrenotazioneJSONObject (String codicePrenotazione) {
		return jsonPrenotazioni.getJSONObject(codicePrenotazione);
	}
	
	public String getLinkedFruitore (String codicePrenotazione) {
		return getPrenotazioneJSONObject(codicePrenotazione).getString(UTENTE);
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
		prenotazioneJSON.put(UTENTE, username);
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
