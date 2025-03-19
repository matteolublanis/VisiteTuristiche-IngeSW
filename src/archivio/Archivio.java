package archivio;

import org.json.*;
import controller.Credenziali;
import utility.CostantiStruttura;
import utility.JSONUtility;
import utility.Time;

public class Archivio {
	
	private static final String CREDENZIALI_CONF_INIZIALE = "PRIMO AVVIO, CREDENZIALI CONFIGURATORE\n"
			+ "Username: admin Password: admin";
	private static final String PRIMA_CONFIGURAZIONE = "prima_configurazione";
	private static final int RIGHE_USERS = 5;
	private static final String PATH_DATE_PRECLUSE = "src/archivio/date_precluse.json";
	private static final String PATH_USERS = "src/archivio/users.json";
	private static final String PATH_VISITE = "src/archivio/piano_visite.json";
	private static final String PATH_VISITE_DAPUBBLICARE = "src/archivio/piano_visite.json";
	private static final String PATH_TIPI_VISITE = "src/archivio/tipo_visite.json";
	private static final String AMBITO = "src/archivio/ambito_territoriale.json";
	private static final String PRIMO_AVVIO = "primo_avvio";
	private JSONObject jsonTipiVisite = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
	private JSONObject jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
	private JSONObject jsonAmbitoTerritoriale = JSONUtility.readJsonFile(AMBITO);
	private JSONObject pianoVisite = JSONUtility.readJsonFile(PATH_VISITE);
	private JSONObject pianoVisiteDaPubblicare = JSONUtility.readJsonFile(PATH_VISITE_DAPUBBLICARE);
	JSONObject jsonPreclusione = JSONUtility.readJsonFile(PATH_DATE_PRECLUSE);

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
	
	public boolean impostaMaxPrenotazione (int max) {
		try {
			jsonAmbitoTerritoriale.put("max_prenotazione", max);
			JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, AMBITO, RIGHE_USERS);
			return true;
		}
		catch (Exception e) {
			System.out.println("Problema nel setting di max prenotazione> " + e.getMessage());
			return false;
		}

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
	
	public boolean indicaDatePrecluse (String date) {
		if (Time.isValidDate(date)) {
			JSONArray datePrecluse = (JSONArray) jsonPreclusione.get("datePrecluseI+3");
	        for (int i = 0; i < datePrecluse.length(); i++) {
	            if (datePrecluse.get(i).equals(date)) {
	                return false;
	            }
	        }
	        datePrecluse.put(date);
	        JSONUtility.aggiornaJsonFile(jsonPreclusione, PATH_DATE_PRECLUSE, 31); 
	        System.out.println("Data preclusa salvata con successo: " + date);
	        return true;
		}
		else return false;
	}
	
	public boolean checkPrimoAccesso (String username) {
		if (!usernameEsiste(username)) {
			JSONObject utente = (JSONObject) jsonUsers.get(username);
			return ((boolean) utente.get("primo_accesso") == true);
		}
		else return false;
	}
	
	public void aggiungiVisita(String luogo, String tipo, String data) {
		JSONObject giorno = (JSONObject) pianoVisiteDaPubblicare.get(data); //TODO gestire eccezione, può non esserci
		JSONObject nuovaVisita = new JSONObject();
		nuovaVisita.put("luogo", luogo);
		nuovaVisita.put("tipo-visita", tipo);
		nuovaVisita.put("data", data);
		giorno.put(tipo ,nuovaVisita); //TODO Rivedere

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
	
	public String getElencoTipiVisite () {
		return null; //TODO implementare
	}
	//TODO stampa da far gestire alla GUI
	public String getElencoLuoghiVisitabili () {
		String result = "";
		try {
			JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject("luoghi");
			for (String k : luoghi.toMap().keySet()) {
				JSONObject j = luoghi.getJSONObject(k);
				if (!j.get("tipo-visita").equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
					result += "Luogo: " + j.get("nome") + "\n";
				}
			}
			return result;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return result;
		}
	}
	//TODO stampa da far gestire alla GUI
	public String getElencoTipiVisiteLuogo () {
		String result = "";
		try {
			JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject("luoghi");
			for (String nomeLuogo : luoghi.toMap().keySet()) {
				JSONObject infoLuogo = luoghi.getJSONObject(nomeLuogo);
				if (!infoLuogo.get("tipo-visita").equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
					JSONArray tipiVisite = infoLuogo.getJSONArray("tipo-visita");
					result += "Luogo: " + infoLuogo.get("nome") + ", tipi visite associati: ";
					for (int i = 0 ; i < tipiVisite.length() ; i++) 
					{
						result += (jsonTipiVisite.getJSONObject(tipiVisite.get(i).toString())).get("titolo") + ",";
					}
					result += "\n";	
				}
			}
			return result;
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return result;
		}
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
	
	public boolean aggiungiTipoVisite(String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, String giorniPrenotabili, String oraInizio,
			String durataVisita, String daAcquistare, String minFruitore, String maxFruitore) {
		
	    JSONObject nuovoTipoVisita = new JSONObject();
	    nuovoTipoVisita.put("titolo", titolo);
	    nuovoTipoVisita.put("descrizione", descrizione);
	    nuovoTipoVisita.put("punto-incontro", puntoIncontro);
	    nuovoTipoVisita.put("data-inizio", dataInizio);
	    nuovoTipoVisita.put("data-fine", dataFine);
	    nuovoTipoVisita.put("giorni-prenotabili", giorniPrenotabili);
	    nuovoTipoVisita.put("ora-inizio", oraInizio);
	    nuovoTipoVisita.put("durata-visita", durataVisita);
	    nuovoTipoVisita.put("da-acquistare", daAcquistare);
	    nuovoTipoVisita.put("min-fruitore", minFruitore);
	    nuovoTipoVisita.put("max-fruitore", maxFruitore);
	    	    
	    pianoVisite.put(tipoVisita, nuovoTipoVisita);

	    JSONUtility.aggiornaJsonFile(pianoVisite, PATH_TIPI_VISITE, 4);
	    return true; //TODO rivedere
	}

	public String getCredenzialiConfIniziale() {
		return CREDENZIALI_CONF_INIZIALE;
	}

}
