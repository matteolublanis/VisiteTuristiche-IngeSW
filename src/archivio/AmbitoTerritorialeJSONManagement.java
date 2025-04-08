package archivio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import utility.JSONUtility;

public class AmbitoTerritorialeJSONManagement {
	private JSONObject jsonAmbitoTerritoriale = null; 
	private static final String PATH_AMBITO = "json/ambito_territoriale.json";
	private static final String PRIMA_CONFIGURAZIONE = "prima_configurazione";
	private static final String NAME = "nome";
	private static final String LUOGHI = "luoghi";
	private static final String TIPO_VISITA = "tipo-visita";
	private static final String COLLOCAZIONE = "collocazione";
	private static final String MAX_PRENOTAZIONE = "max_prenotazione";
	private static final String DESCRIZIONE = "descrizione";
	
	public AmbitoTerritorialeJSONManagement () {
        if (JSONUtility.readJsonFile(PATH_AMBITO) == null) {
        	creaJsonAmbitoTerritoriale();
        }
        else jsonAmbitoTerritoriale = JSONUtility.readJsonFile(PATH_AMBITO);
	}
	
	private void creaJsonAmbitoTerritoriale () {
		JSONObject ambito = new JSONObject();
		ambito.put(PRIMA_CONFIGURAZIONE, true);
		ambito.put(LUOGHI, new JSONObject());
		ambito.put(NAME, "");
		ambito.put(MAX_PRENOTAZIONE, 1);
		JSONUtility.aggiornaJsonFile(ambito, PATH_AMBITO, 10);
		jsonAmbitoTerritoriale = JSONUtility.readJsonFile(PATH_AMBITO);
	}
	
	public void rimuoviLuogo (String luogo) {
		jsonAmbitoTerritoriale.getJSONObject(LUOGHI).remove(luogo);
		aggiornaJsonAmbito();
	}
	public void aggiungiTipoALuogo (String luogo, String tipoVisita) {
		JSONArray tipiLuogo = getTipiLuogo(luogo);
		tipiLuogo.put(tipoVisita);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);
	}
	
	public int getMaxPrenotazione () {
		return jsonAmbitoTerritoriale.getInt(MAX_PRENOTAZIONE);
	}
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String username, Map<String, String> tipiVisiteTitoli) {
		HashMap<String, List<String>> result = new HashMap<>();
		try {
			for (String nomeLuogo : getLuoghi().toMap().keySet()) {
				JSONObject infoLuogo = getLuogo(nomeLuogo);
				if (!infoLuogo.get(TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
				JSONArray tipiVisite = infoLuogo.getJSONArray(TIPO_VISITA);
				List<String> tipiVisiteAssociati = new ArrayList<>();
				for (int i = 0 ; i < tipiVisite.length() ; i++) 
					{
						tipiVisiteAssociati.add(tipiVisiteTitoli.get(tipiVisite.get(i)));
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
	
	public void rimuoviTipoFromLuogo (String luogo, String tipo) {
		JSONArray tipiLuogo = getTipiLuogo(luogo);
		for (int i = 0 ; i < tipiLuogo.length() ; i++) {
			if (tipiLuogo.getString(i).equals(tipo)) {
				tipiLuogo.remove(i); //rimuove reference
				break;
			}
		}
		aggiornaJsonAmbito();
		if (checkIfLuogoHasNoVisitType(luogo)) rimuoviLuogo(luogo);
	}
	
	public JSONObject getLuoghi () {
		return jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
	}
	
	public JSONArray getTipiLuogo (String luogo) {
		return getLuogo(luogo).getJSONArray(TIPO_VISITA);
	}
	
	public JSONObject getLuogo (String luogo) {
		return jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(luogo);
	}
	
	public List<String> getElencoLuoghiVisitabili (String username) { 
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
	
	public boolean aggiungiLuogo (String tag, String nome, String descrizione, String collocazione, JSONArray tipiVisita) {
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
		if (luoghi.has(tag)) return false; 
		JSONObject nuovoLuogo = new JSONObject();
	    nuovoLuogo.put(NAME, nome);
	    nuovoLuogo.put(DESCRIZIONE, descrizione);
		nuovoLuogo.put(COLLOCAZIONE, collocazione);
	    nuovoLuogo.put(TIPO_VISITA, tipiVisita);
	    luoghi.put(tag, nuovoLuogo);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10); 
	    return true;
	}
	
	public boolean impostaMaxPrenotazione (int max) {
		try {
			jsonAmbitoTerritoriale.put(MAX_PRENOTAZIONE, max);
			aggiornaJsonAmbito();
			return true;
		}
		catch (Exception e) {
			System.out.println("Problema nel setting di max prenotazione> " + e.getMessage());
			return false;
		}

	}
	
	public boolean checkIfPlaceExists(String luogo) {
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
		return (luoghi.has(luogo));
	}
	
	public boolean checkIfLuogoHasNoVisitType (String luogo) {	
		return jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(luogo).getJSONArray(TIPO_VISITA).length() == 0;
	}
	
	public boolean isPrimaConfigurazione () {
		return jsonAmbitoTerritoriale.getBoolean(PRIMA_CONFIGURAZIONE);
	}
	
	public void impostaAmbitoTerritoriale (String nome) {
		jsonAmbitoTerritoriale.put(NAME, nome);
		aggiornaJsonAmbito();
	}
	
	public void setPrimaConfigurazione () {
		jsonAmbitoTerritoriale.put(PRIMA_CONFIGURAZIONE, false);
		aggiornaJsonAmbito();
	}
	
	public void aggiornaJsonAmbito() {
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 5);
	}
}
