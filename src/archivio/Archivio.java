package archivio;

import org.json.*;

import utility.CostantiStruttura;
import utility.Credenziali;
import utility.JSONUtility;
import utility.Time;

public class Archivio {
	
	private static final String PATH_USERS = "src/archivio/users.json";
	private static final String PATH_VISITE = "src/archivio/piano_visite.json";
	private static final String PATH_VISITE_DAPUBBLICARE = "src/archivio/piano_visite.json";
	private static final String PATH_TIPI_VISITE = "src/archivio/tipo_visite.json";
	private static final String PATH_AMBITO = "src/archivio/ambito_territoriale.json";
	private static final String PATH_STORICO = "src/archivio/visite_effettuate_storico.json";
	private JSONObject jsonTipiVisite = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
	private JSONObject jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
	private JSONObject jsonAmbitoTerritoriale = JSONUtility.readJsonFile(PATH_AMBITO);
	private JSONObject jsonPianoVisite = JSONUtility.readJsonFile(PATH_VISITE); 
	private JSONObject jsonPianoVisiteDaPubblicare = JSONUtility.readJsonFile(PATH_VISITE_DAPUBBLICARE);
	private JSONObject jsonPianoStorico = JSONUtility.readJsonFile(PATH_STORICO);
	private static final String TIPO_VISITA = "tipo-visita";
	private static final String[] GIORNISETTIMANA = new String[] {"lun","mar","mer","gio","ven","sab","dom"};
	private static final String PASSWORD = "password";
	private static final String TIPO_USER = "tipo";
	private static final String USERNAME = "username";
	private static final String PRIMO_ACCESSO = "primo-accesso";
	private static final String CREDENZIALI_CONF_INIZIALE = "PRIMO AVVIO, CREDENZIALI CONFIGURATORE\nUsername: admin Password: admin";
	private static final String PRIMA_CONFIGURAZIONE = "prima_configurazione";
	private static final int RIGHE_USERS = 5;
	private static final String PRIMO_AVVIO = "primo_avvio";

	public Archivio () {
		System.out.println("Creato archivio.");
	}
	
	public boolean isPrimaConfigurazione () {
		return jsonAmbitoTerritoriale.getBoolean(PRIMA_CONFIGURAZIONE);
	}
	
	public void setPrimaConfigurazione () {
		jsonAmbitoTerritoriale.put(PRIMA_CONFIGURAZIONE, false);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
	}
	
	public boolean checkPrimoAvvio () {
		boolean primoAvvio = (boolean) jsonUsers.get(PRIMO_AVVIO);
		return (primoAvvio);
	}
	
	public void impostaAmbitoTerritoriale (String nome) {
		if (isPrimaConfigurazione()) {
			jsonAmbitoTerritoriale.put("nome", nome);
			JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
			setPrimaConfigurazione();
		}
	}
	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, String tipi_visiteVal) {
		if (checkValueExistance(username, PATH_USERS)) return false; 
		else {
			JSONObject volontario = new JSONObject();
			JSONArray tipiVisite = new JSONArray();
		    String[] s = tipi_visiteVal.split("\\s*,\\s*");
		    for (String k : s) {
		    	if (!checkValueExistance(k, PATH_TIPI_VISITE) && !k.equals("")) return false;
		    	else {
		    		if (!k.equals(""))tipiVisite.put(k);
		    	}
		    }
		    volontario.put(USERNAME, username);
			volontario.put(PRIMO_ACCESSO, true);
			volontario.put(TIPO_USER, CostantiStruttura.VOLONTARIO);
			volontario.put(PASSWORD, password);
		    volontario.put(TIPO_VISITA, tipiVisite);
			jsonUsers.put(username, volontario);
			JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
			return true;
		}
	}
	
	public String getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		String result = "";
		//ciclo sul pianoVisite attuale
		for (String k : jsonPianoVisite.keySet()) { //giorno
			JSONObject j = jsonPianoVisite.getJSONObject(k); 
			for (String m : j.keySet()) { //visite del giorno
				JSONObject visita = j.getJSONObject(m);
				JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject("luoghi");
				JSONObject luogo = luoghi.getJSONObject(m);
				String titoloLuogo = luogo.getString("nome");
				JSONObject tipoVisita = jsonTipiVisite.getJSONObject(visita.getString("tipo-visita"));
				String titoloVisita = tipoVisita.getString("titolo");
				String stato = visita.getString("stato");
				result += "Giorno: " + k + ", Luogo: " + titoloLuogo + ", Visita: " + titoloVisita + ", Stato: " + stato + "\n";
			}
		}
		for (String k : jsonPianoStorico.keySet()) { //giorno
			JSONObject j = jsonPianoStorico.getJSONObject(k); 
			for (String m : j.keySet()) { //visite del giorno
				String s = j.getString(m);
				result += "Giorno: " + k + ", Tag Luogo: " + m + ", Tipo Visita: " + s + ", Stato: effettuata\n";
			}
		}
		return result;
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password) {
		if (checkValueExistance(username, PATH_USERS)) return false; 
		else {
			JSONObject configuratore = new JSONObject();
			configuratore.put(USERNAME, username);
			configuratore.put(PRIMO_ACCESSO, true);
			configuratore.put(TIPO_USER, CostantiStruttura.CONFIGURATORE);
			configuratore.put(PASSWORD, password);
			jsonUsers.put(username, configuratore);
			JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
			return true;
		}
	}
	
	public boolean impostaMaxPrenotazione (int max) {
		try {
			jsonAmbitoTerritoriale.put("max_prenotazione", max);
			JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
			return true;
		}
		catch (Exception e) {
			System.out.println("Problema nel setting di max prenotazione> " + e.getMessage());
			return false;
		}

	}
	
	public String getElencoUser (int tipo) {
		String result = "";
		for (String s : JSONUtility.allObjectsSameIntValue(jsonUsers, tipo, TIPO_USER)) {
			JSONObject user = (JSONObject) jsonUsers.get(s);
			switch (tipo) {
			case CostantiStruttura.CONFIGURATORE:
				result += "Username: " + user.get(USERNAME) + "\n";
				break;
			case CostantiStruttura.VOLONTARIO:
				result += "Username: " + user.get(USERNAME) + "\n" +
							"Tipi visite: " + user.get(TIPO_VISITA) + "\n";
				break;
			case CostantiStruttura.FRUITORE:
				result += "Username: " + user.get(USERNAME) + "\n";
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
		return (int) (utente.get(TIPO_USER));
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		if (!checkValueExistance(c.getUsername(), PATH_USERS)) return false;
		else { 
			JSONObject utente = (JSONObject) jsonUsers.get(c.getUsername());
			if (utente == (null)) return false;
			return (utente.get(PASSWORD).equals(c.getPassword()));
		}
	
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String username) {
		if (!checkValueExistance(username, PATH_USERS)) return false;
		else {
			if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE &&
					isPrimaConfigurazione()) {
				return true;
			}
			else return false;
		}
	}
	
	public boolean modificaCredenziali (String username, Credenziali c) {
		if (!checkValueExistance(username, PATH_USERS)) return false;
		if (checkValueExistance(c.getUsername(), PATH_USERS)) return false; 
		else {
			JSONObject utente = null;
			try {
				utente = (JSONObject) jsonUsers.get(username);
				utente.put(USERNAME, c.getUsername());
				utente.put(PASSWORD, c.getPassword());
				jsonUsers.remove(username);
				jsonUsers.put(c.getUsername(), utente);
				JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
				return true;
			}
			catch (Exception e) {
				System.out.println("Problema in modificaCredenziali: " + e.getMessage()); //da comunicare ad App???
				return false;
			}
		}
		
	}
	
	public boolean indicaDatePrecluse (String date) {
		if (Time.isValidDate(date)) {
			JSONArray datePrecluse = (JSONArray) jsonPianoVisiteDaPubblicare.get("datePrecluseI+3");
			if (!Time.isThisDateInMonthiplus3(date)) return false; 
	        for (int i = 0; i < datePrecluse.length(); i++) {
	            if (datePrecluse.get(i).equals(date)) {
	                return false;
	            }
	        }
	        datePrecluse.put(date);
	        JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10); 
	        return true;
		}
		else return false;
	}
	
	public boolean checkPrimoAccesso (String username) {
		if (checkValueExistance(username, PATH_USERS)) {
			JSONObject utente = (JSONObject) jsonUsers.get(username);
			return ((boolean) utente.get(PRIMO_ACCESSO) == true);
		}
		else return false;
	}
	//TODO implementare
	public void pubblicaPianoVisite() {
		if (Time.todayIsDay(16)) {
			
		}
	}
	
	public String getElencoTipiVisite () {
		String result = "[";
		for (String k : jsonTipiVisite.keySet()) {
			result += k + ",";
		}
		result += "]";
		return result;
	}

	public String getElencoLuoghiVisitabili () {
		String result = "";
		try {
			JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject("luoghi");
			for (String k : luoghi.toMap().keySet()) {
				JSONObject j = luoghi.getJSONObject(k);
				if (!j.get(TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
					result += "Luogo: " + j.get("nome") + "\n";
				}
			}
			return result;
		}
		catch (Exception e) {
			return (e.getMessage());
		}
	}

	public String getElencoTipiVisiteLuogo () {
		String result = "";
		try {
			JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject("luoghi");
			for (String nomeLuogo : luoghi.toMap().keySet()) {
				JSONObject infoLuogo = luoghi.getJSONObject(nomeLuogo);
				if (!infoLuogo.get(TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
					JSONArray tipiVisite = infoLuogo.getJSONArray(TIPO_VISITA);
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
			return (e.getMessage());
		}
	}
	
	public boolean primoAccessoEseguito (String user) {
		try {
			JSONObject utente = (JSONObject) jsonUsers.get(user);
			utente.put(PRIMO_ACCESSO, false); 	
			JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS); 
			return true;
		}
		catch (Exception e) {
			return false;
		}
		
	}
	
	public boolean aggiungiLuogo (String tag, String nome, String collocazione, String tipiVisitaVal) {
		if (checkValueExistance(tag, PATH_AMBITO)) return false;
		JSONObject nuovoLuogo = new JSONObject();
		String[] s = tipiVisitaVal.split("\\s*,\\s*");
		JSONArray tipiVisita = new JSONArray();
	    for (String k : s) {
	    	if (!checkValueExistance(k, PATH_TIPI_VISITE) && !k.equals("")) return false;
	    	else {
	    		if (!k.equals("")) tipiVisita.put(k);
	    	}
	    }
	    nuovoLuogo.put("nome", nome);
		nuovoLuogo.put("collocazione", collocazione);
	    nuovoLuogo.put(TIPO_VISITA, tipiVisita);
	    JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject("luoghi");
	    luoghi.put(tag, nuovoLuogo);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10); 
	    return true;
	}
	
	public boolean aggiungiTipoVisite(String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, String giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, String volontariVal) {
		
		JSONObject nuovoTipoVisita = new JSONObject();
	    JSONArray giorniPrenotabili = new JSONArray();
	    String[] s = giorniPrenotabiliVal.split("\\s*,\\s*");
	    String days = "";
	    for (String k : s) {
	    	try {
	    		int j = Integer.parseInt(k);
	    		if (!(j < 1 || j > 7)) {
		    		giorniPrenotabili.put(GIORNISETTIMANA[j-1]);
		    		days += GIORNISETTIMANA[j-1] + ",";
		    	}
	    	}
	    	catch (NumberFormatException e) {
	    		return false;
	    	}
	    }
	    if (days.length() == 0) return false;
	    String[] m = volontariVal.split("\\s*,\\s*");
	    JSONArray volontari = new JSONArray();
	    for (String k : m) {
	    	JSONObject volontario = jsonUsers.getJSONObject(k);
	    	JSONArray tipi = volontario.getJSONArray(TIPO_VISITA);
	   		if (volontarioAlreadyLinkedForThatDay(dataInizio, dataFine, oraInizio, durataVisita, days, tipi)) return false;
	   		tipi.put(tipoVisita);
	   		volontari.put(k);
	    }
	    System.out.println(volontariVal);
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
	    nuovoTipoVisita.put("volontari", volontari);
		JSONArray tipiLuogo = jsonAmbitoTerritoriale.getJSONObject("luoghi").getJSONObject(luogo).getJSONArray(TIPO_VISITA);
		tipiLuogo.put(tipoVisita);
	    jsonTipiVisite.put(tipoVisita, nuovoTipoVisita);
	    JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);
	    JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
	    return true; 
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		JSONObject t = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
		try {
			return !(t.get(tipo).equals(null)); 
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean checkIfVolontarioExists (String volontario) {
		JSONObject user = JSONUtility.readJsonFile(PATH_USERS);
		try {
			return !(user.get(volontario).equals(null)); 
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean checkIfPlaceExists(String luogo) {
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject("luoghi");
		if (luoghi.has(luogo)) return true;
		else return false;
	}
	
	public boolean volontarioAlreadyLinkedForThatDay (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONArray tipiVisitaVolontario) {
		for (Object k : tipiVisitaVolontario) { 
			JSONObject tipo = jsonTipiVisite.getJSONObject((String)k); //prende ogni tipo dal json dei tipi
			if (Time.comesBefore(dateStart1, tipo.getString("data-fine")) && !Time.comesBefore(dateFinish1, tipo.getString("data-inizio"))) { //controlla se periodi intersecano
				JSONArray days2 = tipo.getJSONArray("giorni-prenotabili"); //prende giorni prenotabili del tipo
				for (Object d : days2) { 
					if (days1.contains((String)d)) return true; //se i giorni intersecano allora volontario linkato per quei giorni
				}
			}
		}
		return false;
	}
	
	public boolean intersectOtherEventSamePlace (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONObject luogo) {
		JSONArray tipiLuogo = luogo.getJSONArray("tipo-visita");
		for (Object k : tipiLuogo) { //tipiVisita del luogo
			JSONObject tipo = jsonTipiVisite.getJSONObject((String)k); 
			if (Time.comesBefore(dateStart1, tipo.getString("data-fine")) && !Time.comesBefore(dateFinish1, tipo.getString("data-inizio"))) {
				JSONArray days2 = tipo.getJSONArray("giorni-prenotabili"); //giorni del tipo già esistente
				for (Object d : days2) {
					if (days1.contains((String)d)) return true; 
				}
			}
		}
		return false;
	}
	
	public boolean checkValueExistance (String key, String path) {
		JSONObject json = JSONUtility.readJsonFile(path);
		try {
			return !(json.get(key).equals(null)); 
		}
		catch (Exception e) {
			return false;
		}
	}

	public String getCredenzialiConfIniziale() {
		return CREDENZIALI_CONF_INIZIALE; //TODO da prendere dall'archivo
	}

}
