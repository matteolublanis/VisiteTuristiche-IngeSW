package archivio;

import java.util.HashMap;
import org.json.*;
import controller.Credenziali;
import utility.CostantiStruttura;
import utility.JSONUtility;

public class Archivio {
	
	private static final String CREDENZIALI_CONF_INIZIALE = "PRIMO AVVIO, CREDENZIALI CONFIGURATORE\n"
			+ "Username: admin Password: admin";
	private static final String PRIMA_CONFIGURAZIONE = "prima_configurazione";
	private static final int RIGHE_USERS = 5;
	private static final String PATH_DATE_PRECLUSE = "src/archivio/date_precluse.json";
	private static final String PATH_USERS = "src/archivio/users.json";
	private static final String PATH_VISITE = "src/archivio/piano_visite.json";
	private static final String PATH_TIPI_VISITE = "src/archivio/tipo_visite.json";
	private static final String AMBITO = "src/archivio/ambito_territoriale.json";
	private static final String PRIMO_AVVIO = "primo_avvio";
	private JSONObject jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
	private JSONObject jsonAmbitoTerritoriale = JSONUtility.readJsonFile(AMBITO);
	private JSONObject pianoVisite = JSONUtility.readJsonFile(PATH_VISITE);

	public Archivio () {
		System.out.println("Creato archivio.");
	}
	
	public boolean isPrimaConfigurazione () {
		return jsonAmbitoTerritoriale.getBoolean(PRIMA_CONFIGURAZIONE);
	}
	
	public void setPrimaConfigurazione () {
		jsonAmbitoTerritoriale.put(PRIMA_CONFIGURAZIONE, false);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, AMBITO, RIGHE_USERS);
	}
	
	public boolean checkPrimoAvvio () {
		boolean primoAvvio = (boolean) jsonUsers.get(PRIMO_AVVIO);
		return (primoAvvio);
	}
	
	public void impostaAmbitoTerritoriale (String nome) {
		if (isPrimaConfigurazione()) {
			jsonAmbitoTerritoriale.put("nome", nome);
			JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, AMBITO, RIGHE_USERS);
			setPrimaConfigurazione();
		}

	}
	
	public void impostaMaxPrenotazione (int max) {
		jsonAmbitoTerritoriale.put("max_prenotazione", max);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, AMBITO, RIGHE_USERS);

	}
	
	public String getElencoUser (int tipo) {
		String result = "";
		for (String s : JSONUtility.allObjectsSameIntValue(jsonUsers, tipo, "tipo")) {
			JSONObject user = (JSONObject) jsonUsers.get(s);
			switch (tipo) {
			case CostantiStruttura.CONFIGURATORE:
				result += "Username: " + user.get("username") + "\n";
				break;
			case CostantiStruttura.VOLONTARIO:
				result += "Username: " + user.get("username") + "\n" +
							"Tipi visite: " + user.get("tipi_visite") + "\n";
				break;
			case CostantiStruttura.FRUITORE:
				result += "Username: " + user.get("username") + "\n";
			}
		}
		return result;
	}
	
	public void setPrimoAvvio () {
		jsonUsers.put(PRIMO_AVVIO, false);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
	}

	public int getTipoUtente (String username) {
		JSONObject utente = null;
		try {
			utente = (JSONObject) jsonUsers.get(username);
		}
		catch (Exception e) {
			System.out.println("Utente non trovato: getTipoUtente");
			return -1;
		}
		return (int) (utente.get("tipo"));
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		if (!usernameEsiste(c.getUsername())) return false;
		else { 
			JSONObject utente = (JSONObject) jsonUsers.get(c.getUsername());
			if (utente == (null)) return false;
			return (utente.get("password").equals(c.getPassword()));
		}
	
	}
	
	public boolean usernameEsiste (String username) {
		try {
			return !(jsonUsers.get(username).equals(null)); 
		}
		catch (Exception e) {
			return false;
		}
		
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String username) {
		if (!usernameEsiste(username)) return false;
		else {
			if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE &&
					isPrimaConfigurazione()) {
				return true;
			}
			else return false;
		}
	}
	
	public boolean modificaCredenziali (String username, Credenziali c) {
		if (!usernameEsiste(username)) return false;
		if (usernameEsiste(c.getUsername())) return false; //check if new user exists already
		else {
			JSONObject utente = null;
			try {
				utente = (JSONObject) jsonUsers.get(username);
				utente.put("username", c.getUsername());
				utente.put("password", c.getPassword());
				jsonUsers.remove(username);
				jsonUsers.put(c.getUsername(), utente);
				JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
				return true;
			}
			catch (Exception e) {
				System.out.println("Utente inesistente.");
				return false;
			}
		}
		
	}
	
	public boolean checkPrimoAccesso (String username) {
		if (!usernameEsiste(username)) {
			JSONObject utente = (JSONObject) jsonUsers.get(username);
			return ((boolean) utente.get("primo_accesso") == true);
		}
		else return false;
	}
	
	public String getElencoLuoghiVisitabili () {
		String result = "";
		HashMap<String, String> visite = JSONUtility.getAllSameValsFromObjects(jsonAmbitoTerritoriale, "tipo_visite");
		for (String k : visite.keySet()) {
			if (!visite.get(k).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
				result += "Luogo: " + k + "\n";
			}
		}
		return result;
	}
	
	
	public void aggiungiVisita(String luogo, String tipo, String data) {
		
		JSONArray visite = (JSONArray) pianoVisite.get("visite");
		JSONObject nuovaVisita = new JSONObject();
		nuovaVisita.put("luogo", (String) luogo);
		nuovaVisita.put("tipo", (String) tipo);
		nuovaVisita.put("data", (String) data);
		visite.put(nuovaVisita);
		JSONUtility.aggiornaJsonFile(pianoVisite, PATH_VISITE, 4); //TODO check file json
		//System.out.println("Visita aggiunta con successo.");
	}
	
	public void indicaDatePrecluse(Object data) {
		JSONObject jsonPreclusione = JSONUtility.readJsonFile(PATH_DATE_PRECLUSE);
		JSONArray datePrecluse = (JSONArray) jsonPreclusione.get("datePrecluse");
		boolean contains = false;
        for (int i = 0; i < datePrecluse.length(); i++) {
            if (datePrecluse.get(i).equals(data)) {
                contains = true;
                break;
            }
        }
        if (contains) {
        	System.out.println("La data " + data + " è già preclusa.");
        } else {
        	datePrecluse.put(data);
            JSONUtility.aggiornaJsonFile(jsonPreclusione, PATH_DATE_PRECLUSE, 4); //TODO check file in cui salvare
            System.out.println("Data preclusa salvata con successo: " + data);
        }
	}
	
	public void pubblicaPianoVisite() {
		/*
		JSONObject jsonPiano = JSONUtility.readJsonFile(PATH_VISITE);
		JSONArray visiteArray = jsonPiano.getJSONArray("visite");
		for (int i = 0; i < visiteArray.length(); i++) {
            JSONObject visita = visiteArray.getJSONObject(i);
            String tipo = visita.optString("tipo", "Tipo non specificato");
            String data = visita.optString("data", "Data non specificata");
            String luogo = visita.optString("luogo", "Luogo non specificato");

            System.out.println("Visita " + (i + 1) + ": ");
            System.out.println("Tipo: " + tipo);
            System.out.println("Data: " + data);
            System.out.println("Luogo: " + luogo);
            System.out.println();
        }
        */
	}
	
	public String getElencoTipiVisiteLuogo () {
		String result = "";
		HashMap<String, String> visite = JSONUtility.getAllSameValsFromObjects(jsonAmbitoTerritoriale, "tipo_visite");
		for (String k : visite.keySet()) {
			result += "Luogo: " + k + ", tipi visite: " + visite.get(k) + "\n";		
		}
		return result;
	}
	
	public boolean primoAccessoEseguito (String user) {
		try {
			JSONObject utente = (JSONObject) jsonUsers.get(user);
			utente.put("primo_accesso", false); 	
			JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS); 
			return true;
		}
		catch (Exception e) {
			return false;
		}
		
	}
	
	public void aggiungiTipoVisite(Object tipoVisita, Object titolo, Object descrizione, Object puntoIncontro, Object dataInizio, Object dataFine, Object giorniPrenotabili, Object oraInizio, Object durataVisita, Object daAcquistare, Object minFruitore, Object maxFruitore) {
		
	    JSONObject pianoVisite = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
	    JSONObject nuovoTipoVisita = new JSONObject();
	    nuovoTipoVisita.put("titolo", (String) titolo);
	    nuovoTipoVisita.put("descrizione", (String) descrizione);
	    nuovoTipoVisita.put("punto-incontro", (String) puntoIncontro);
	    nuovoTipoVisita.put("data-inizio", (String) dataInizio);
	    nuovoTipoVisita.put("data-fine", (String) dataFine);
	    nuovoTipoVisita.put("giorni-prenotabili", (String) giorniPrenotabili);
	    nuovoTipoVisita.put("ora-inizio", (String) oraInizio);
	    nuovoTipoVisita.put("durata-visita", (String) durataVisita);
	    nuovoTipoVisita.put("da-acquistare", (Boolean) daAcquistare);
	    nuovoTipoVisita.put("min-fruitore", (Integer) minFruitore);
	    nuovoTipoVisita.put("max-fruitore", (Integer) maxFruitore);
	    	    
	    pianoVisite.put((String) tipoVisita, nuovoTipoVisita);

	    JSONUtility.aggiornaJsonFile(pianoVisite, PATH_TIPI_VISITE, 4);
	    System.out.println("Tipo di visita aggiunto con successo.");
	}

	public String getCredenzialiConfIniziale() {
		return CREDENZIALI_CONF_INIZIALE;
	}

}
