package archivio;

import java.util.Arrays;

import org.json.*;

import utility.CostantiStruttura;
import utility.Credenziali;
import utility.JSONUtility;
import utility.Time;

public class Archivio {
	
	private static final String POSSIBILE_DARE_DISPONIBILITA = "possibile-dare-disponibilita",
			PRIMA_PUBBLICAZIONE = "prima-pubblicazione", VOLONTARI2 = "volontari", MAX_FRUITORE = "max-fruitore", MIN_FRUITORE = "min-fruitore", DA_ACQUISTARE = "da-acquistare",
			DURATA_VISITA = "durata-visita", ORA_INIZIO = "ora-inizio", GIORNI_PRENOTABILI = "giorni-prenotabili", DATA_FINE = "data-fine", DATA_INIZIO = "data-inizio",
			PUNTO_INCONTRO = "punto-incontro", DESCRIPTION = "descrizione", COLLOCAZIONE = "collocazione", DATE_PRECLUSE_MESEIPLUS3 = "datePrecluseI+3",
			MAX_PRENOTAZIONE = "max_prenotazione", STATO_VISITA = "stato", TITOLO = "titolo", LUOGHI = "luoghi", DISPONIBILITA = "disponibilita",
			TIPO_VISITA = "tipo-visita", PASSWORD = "password", TIPO_USER = "tipo", USERNAME = "username", PRIMO_ACCESSO = "primo-accesso", 
			PRIMA_CONFIGURAZIONE = "prima_configurazione", PRIMO_AVVIO = "primo_avvio", NAME = "nome",
			
			SPLIT_REGEX_LISTA = "\\s*,\\s*",
			
			PATH_USERS = "src/archivio/users.json", PATH_VISITE = "src/archivio/piano_visite.json", PATH_VISITE_DAPUBBLICARE = "src/archivio/visite_da_pubblicare.json",
			PATH_TIPI_VISITE = "src/archivio/tipo_visite.json", PATH_AMBITO = "src/archivio/ambito_territoriale.json", 
			PATH_STORICO = "src/archivio/visite_effettuate_storico.json";

	private JSONObject jsonTipiVisite = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
	private JSONObject jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
	private JSONObject jsonAmbitoTerritoriale = JSONUtility.readJsonFile(PATH_AMBITO);
	private JSONObject jsonPianoVisite = JSONUtility.readJsonFile(PATH_VISITE); 
	private JSONObject jsonPianoVisiteDaPubblicare = JSONUtility.readJsonFile(PATH_VISITE_DAPUBBLICARE);
	private JSONObject jsonPianoStorico = JSONUtility.readJsonFile(PATH_STORICO);
	private static final String[] GIORNISETTIMANA = new String[] {"lun","mar","mer","gio","ven","sab","dom"},
			CREDENZIALI_CONF_INIZIALE = new String[] {"admin", "admin"};
	private static final int RIGHE_USERS = 5;
	private static int RELEASE_DAY = 16;

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
			jsonAmbitoTerritoriale.put(NAME, nome);
			JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
			setPrimaConfigurazione();
		}
	}
	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, String tipi_visiteVal, boolean tipiVisitaNecessari) {
		if (checkValueExistance(username, PATH_USERS)) return false; 
		else {
			JSONObject volontario = new JSONObject();
			JSONArray tipiVisite = new JSONArray();
		    String[] s = tipi_visiteVal.split(SPLIT_REGEX_LISTA);
		    for (String k : s) {
		    	if (!checkValueExistance(k, PATH_TIPI_VISITE) && !k.equals("")) {
		    		jsonTipiVisite = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
		    		return false;
		    	}
		    	else {
		    		if (!k.equals("")) {
		    			tipiVisite.put(k);
			    		jsonTipiVisite.getJSONObject(k).getJSONArray(VOLONTARI2).put(username);
		    		}
		    	}
		    }
		    if (tipiVisitaNecessari && tipiVisite.length() == 0) return false;
		    volontario.put(USERNAME, username);
			volontario.put(PRIMO_ACCESSO, true);
			volontario.put(TIPO_USER, CostantiStruttura.VOLONTARIO);
			volontario.put(PASSWORD, password);
		    volontario.put(TIPO_VISITA, tipiVisite);
			jsonUsers.put(username, volontario);
			JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
			JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, RIGHE_USERS);
			return true;
		}
	}
	
	public String getElencoVisiteProposteCompleteConfermateCancellateEffettuate () { //TODO non ritornare una stringa ma un oggetto che dia tutte le info da stampare
		String result = "";
		//ciclo sul pianoVisite attuale
		for (String k : jsonPianoVisite.keySet()) { //giorno
			JSONObject j = jsonPianoVisite.getJSONObject(k); 
			for (String m : j.keySet()) { //visite del giorno
				JSONObject visita = j.getJSONObject(m);
				JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
				JSONObject luogo = luoghi.getJSONObject(m);
				String titoloLuogo = luogo.getString(NAME);
				JSONObject tipoVisita = jsonTipiVisite.getJSONObject(visita.getString(TIPO_VISITA));
				String titoloVisita = tipoVisita.getString(TITOLO);
				String stato = visita.getString(STATO_VISITA);
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
			jsonAmbitoTerritoriale.put(MAX_PRENOTAZIONE, max);
			JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
			return true;
		}
		catch (Exception e) {
			System.out.println("Problema nel setting di max prenotazione> " + e.getMessage());
			return false;
		}

	}
	
	public String getElencoUser (int tipo) { //TODO non ritornare una stringa ma un oggetto che dia tutte le info da stampare
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
			JSONArray datePrecluse = jsonPianoVisiteDaPubblicare.getJSONArray(DATE_PRECLUSE_MESEIPLUS3);

			if (!Time.isThisDateInMonthiplus3(date)) {
				return false;
			}
			System.out.println(2);
	        for (int i = 0; i < datePrecluse.length(); i++) {
	            if (datePrecluse.get(i).equals(date)) { //eccezione?
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
	
	public String getElencoTipiVisite () { //TODO non ritornare una stringa ma un oggetto che dia tutte le info da stampare
		String result = "[";
		for (String k : jsonTipiVisite.keySet()) {
			result += k + ",";
		}
		result += "]";
		return result;
	}

	public String getElencoLuoghiVisitabili () { //TODO non ritornare una stringa ma un oggetto che dia tutte le info da stampare
		String result = "";
		try {
			JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
			for (String k : luoghi.toMap().keySet()) {
				JSONObject j = luoghi.getJSONObject(k);
				if (!j.get(TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
					result += "Luogo: " + j.get(NAME) + "\n";
				}
			}
			return result;
		}
		catch (Exception e) {
			return (e.getMessage());
		}
	}

	public String getElencoTipiVisiteLuogo () { //TODO non ritornare una stringa ma un oggetto che dia tutte le info da stampare
		String result = "";
		try {
			JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
			for (String nomeLuogo : luoghi.toMap().keySet()) {
				JSONObject infoLuogo = luoghi.getJSONObject(nomeLuogo);
				if (!infoLuogo.get(TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
					JSONArray tipiVisite = infoLuogo.getJSONArray(TIPO_VISITA);
					result += "Luogo: " + infoLuogo.get(NAME) + ", tipi visite associati: ";
					for (int i = 0 ; i < tipiVisite.length() ; i++) 
					{
						result += (jsonTipiVisite.getJSONObject(tipiVisite.get(i).toString())).get(TITOLO) + ",";
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
		String[] s = tipiVisitaVal.split(SPLIT_REGEX_LISTA);
		JSONArray tipiVisita = new JSONArray();
	    for (String k : s) {
	    	if (!checkValueExistance(k, PATH_TIPI_VISITE) && !k.equals("")) return false;
	    	else {
	    		if (!k.equals("")) tipiVisita.put(k);
	    	}
	    }
	    nuovoLuogo.put(NAME, nome);
		nuovoLuogo.put(COLLOCAZIONE, collocazione);
	    nuovoLuogo.put(TIPO_VISITA, tipiVisita);
	    JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
	    luoghi.put(tag, nuovoLuogo);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10); 
	    return true;
	}
	
	public boolean aggiungiTipoVisite(String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, String giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, String volontariVal) {
		
		JSONObject nuovoTipoVisita = new JSONObject();
		JSONArray giorniPrenotabili = new JSONArray();
	    String[] s = giorniPrenotabiliVal.split(SPLIT_REGEX_LISTA);
	    String days = "";
	    for (String k : s) {
	    	try {
	    		int j = Integer.parseInt(k);
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
	    String[] m = volontariVal.split(SPLIT_REGEX_LISTA);
	    JSONArray volontari = new JSONArray();
	    for (String k : m) {
    		JSONObject volontario = jsonUsers.getJSONObject(k);
    		JSONArray tipi = volontario.getJSONArray(TIPO_VISITA);
    		if (volontarioAlreadyLinkedForThatDay(dataInizio, dataFine, oraInizio, durataVisita, days, tipi)) return false;
    		tipi.put(tipoVisita);
    		volontari.put(k);
	    }
	    nuovoTipoVisita.put(TITOLO, titolo);
	    nuovoTipoVisita.put(DESCRIPTION, descrizione);
	    nuovoTipoVisita.put(PUNTO_INCONTRO, puntoIncontro);
	    nuovoTipoVisita.put(DATA_INIZIO, dataInizio);
	    nuovoTipoVisita.put(DATA_FINE, dataFine);
	    nuovoTipoVisita.put(GIORNI_PRENOTABILI, giorniPrenotabili);
	    nuovoTipoVisita.put(ORA_INIZIO, oraInizio); 
	    nuovoTipoVisita.put(DURATA_VISITA, durataVisita); 
	    nuovoTipoVisita.put(DA_ACQUISTARE, daAcquistare);
	    nuovoTipoVisita.put(MIN_FRUITORE, minFruitore);
	    nuovoTipoVisita.put(MAX_FRUITORE, maxFruitore);
	    nuovoTipoVisita.put(VOLONTARI2, volontari);
		JSONArray tipiLuogo = jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(luogo).getJSONArray(TIPO_VISITA);
		tipiLuogo.put(tipoVisita);
	    jsonTipiVisite.put(tipoVisita, nuovoTipoVisita);
	    JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);
	    JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
	    return true; 
	}
	
	public boolean getPossibilitaDareDisponibilita() {
		return jsonPianoVisiteDaPubblicare.getBoolean(POSSIBILE_DARE_DISPONIBILITA);
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		if (isPrimaPubblicazione()) setPrimaPubblicazione();
		jsonPianoVisiteDaPubblicare.put(POSSIBILE_DARE_DISPONIBILITA, b);
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
	}
	
	public String getElencoTipiVisiteVolontario(String v) { 
 	    return jsonUsers.getJSONObject(v).getJSONArray(TIPO_VISITA).toString();
 	}
	
	public void setPrimaPubblicazione() {
		jsonPianoVisiteDaPubblicare.put(PRIMA_PUBBLICAZIONE, false);
	}
	
	public boolean isReleaseOrLaterDay() {
		return (RELEASE_DAY <= Time.getActualDayOfTheMonth());
	}
	
	public boolean isPrimaPubblicazione() {
		return jsonPianoVisiteDaPubblicare.getBoolean(PRIMA_PUBBLICAZIONE);
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
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
		if (luoghi.has(luogo)) return true;
		else return false;
	}
	
	public boolean volontarioAlreadyLinkedForThatDay (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONArray tipiVisitaVolontario) {
		for (Object k : tipiVisitaVolontario) { 
			JSONObject tipo = jsonTipiVisite.getJSONObject((String)k); //prende ogni tipo dal json dei tipi
			if (Time.comesBefore(dateStart1, tipo.getString(DATA_FINE)) && !Time.comesBefore(dateFinish1, tipo.getString(DATA_INIZIO))) { //controlla se periodi intersecano
				JSONArray days2 = tipo.getJSONArray(GIORNI_PRENOTABILI); //prende giorni prenotabili del tipo
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
			if (Time.comesBefore(dateStart1, tipo.getString(DATA_FINE)) && !Time.comesBefore(dateFinish1, tipo.getString(DATA_INIZIO))) {
				JSONArray days2 = tipo.getJSONArray(GIORNI_PRENOTABILI); //giorni del tipo già esistente
				for (Object d : days2) {
					if (days1.contains((String)d)) { //vuol dire che un giorno qualsiasi può intersecare
						String startHourType = tipo.getString(ORA_INIZIO);
						int[] fValue = Time.calculateEndTimeWithStartAndDuration(Integer.parseInt(startHourType.split(":")[0]), Integer.parseInt(startHourType.split(":")[1]), tipo.getInt(DURATA_VISITA));
						String finishHourType = String.format("%02d:%02d", fValue[0], fValue[1]);
						if (Time.isTimeBetween(hour1, startHourType, finishHourType)) return true;
					}
				}
			}
		}
		return false;
	}
	
	public String getDatePerDisponibilita(String username) {
		if (getTipoUtente(username) == CostantiStruttura.VOLONTARIO) {
			String result = "";
			JSONObject volontario = jsonUsers.getJSONObject(username); //prendi user
			JSONArray tipiVisite = volontario.getJSONArray(TIPO_VISITA); //prendi tipi
			if (tipiVisite.length() == 0); //TODO se non ha tipi bisogna chiudere l'App e rimuovere il volontario associato, o gestire la mancanza
			for (Object s : tipiVisite) { 
				JSONObject tipo = jsonTipiVisite.getJSONObject((String)s);
				try {
					String[] periodoDaDareDisponibilita = Time.getAvailabilityWindow(tipo.getString(DATA_INIZIO), tipo.getString(DATA_FINE), Time.getDesideredMonthAndYear(RELEASE_DAY, Time.getActualDate()));
					JSONArray giorni = tipo.getJSONArray(GIORNI_PRENOTABILI);
					result += "Giorni tipo " + s + ": ";
					for (Object g : giorni) {
						result += Time.getAllDatesSameDayOfTheWeek(periodoDaDareDisponibilita[0], periodoDaDareDisponibilita[1], Arrays.asList(GIORNISETTIMANA).indexOf((String) g) + 1); //calcola giorni disponibili
					}
					result += "\n";
				}
				catch (Exception e) {
					result += "Il tipo " + s + " non ha date disponibili, contattare un configuratore\n";
				}
			}
			
			return result;
		}
		else return "";
	}
	
	public boolean inserisciDisponibilita(String data, String username) {
		if (!getDatePerDisponibilita(username).contains(data)) return false;
		else {
			JSONObject disponibilita = jsonPianoVisiteDaPubblicare.getJSONObject(DISPONIBILITA);
			if (!disponibilita.has(username)) {
				JSONArray volontario = new JSONArray();
				volontario.put(data);
				disponibilita.put(username, volontario);
				JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
				return true;
			}
			else {
				JSONArray volontario = disponibilita.getJSONArray(username);
				if (JSONUtility.containsValue(volontario, data)) return true;
				else {
					volontario.put(data);
					JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
					return true;
				}
			}
		}
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

	public Credenziali getCredenzialiConfIniziale() { 
		return new Credenziali(CREDENZIALI_CONF_INIZIALE[0], CREDENZIALI_CONF_INIZIALE[1]);
	}

}
