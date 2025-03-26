package archivio;

import org.json.*;

import utility.CostantiStruttura;
import utility.Credenziali;
import utility.JSONUtility;
import utility.Time;

public class Archivio {
	
	private static final String ANNO_ULTIMA_PUBBLICAZIONE = "anno-ultima-pubblicazione";

	private static final String MESE_ULTIMA_PUBBLICAZIONE = "mese-ultima-pubblicazione";

	public static final String POSSIBILE_DARE_DISPONIBILITA = "possibile-dare-disponibilita",
			PRIMA_PUBBLICAZIONE = "prima-pubblicazione", VOLONTARI2 = "volontari", MAX_FRUITORE = "max-fruitore", MIN_FRUITORE = "min-fruitore", DA_ACQUISTARE = "da-acquistare",
			DURATA_VISITA = "durata-visita", ORA_INIZIO = "ora-inizio", GIORNI_PRENOTABILI = "giorni-prenotabili", DATA_FINE = "data-fine", DATA_INIZIO = "data-inizio",
			PUNTO_INCONTRO = "punto-incontro", DESCRIPTION = "descrizione", COLLOCAZIONE = "collocazione", DATE_PRECLUSE_MESEIPLUS3 = "datePrecluseI+3",
			MAX_PRENOTAZIONE = "max_prenotazione", STATO_VISITA = "stato", TITOLO = "titolo", LUOGHI = "luoghi", DISPONIBILITA = "disponibilita",
			TIPO_VISITA = "tipo-visita", PASSWORD = "password", TIPO_USER = "tipo", USERNAME = "username", PRIMO_ACCESSO = "primo-accesso", 
			PRIMA_CONFIGURAZIONE = "prima_configurazione", PRIMO_AVVIO = "primo_avvio", NAME = "nome", ULTIMO_PIANO = "ultimo-piano-pubblicato", LUOGO = "luogo",
			DATE_PRECLUSE = "date-precluse",
			
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
	public static final String[] GIORNISETTIMANA = new String[] {"lun","mar","mer","gio","ven","sab","dom"};
	private static final String[] CREDENZIALI_CONF_INIZIALE = new String[] {"admin", "admin"};
	private static final int RIGHE_USERS = 5;

	public Archivio () {
		System.out.println("Creato archivio.");
	}
	
	public boolean getPossibileDareDisponibilita () {
		return jsonPianoVisiteDaPubblicare.getBoolean(POSSIBILE_DARE_DISPONIBILITA);
	}
	
	public boolean apriRaccoltaDisponibilita() {
		jsonPianoVisiteDaPubblicare.put(POSSIBILE_DARE_DISPONIBILITA, true);
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
		return true;
	}
	
	public int getUltimoMesePubblicazione() {
		return jsonPianoVisiteDaPubblicare.getInt(MESE_ULTIMA_PUBBLICAZIONE);
	}
	
	public int getUltimoAnnoPubblicazione() {
		return jsonPianoVisiteDaPubblicare.getInt(ANNO_ULTIMA_PUBBLICAZIONE);
	}
	
	public boolean canAddOrRemove() {
		if (jsonPianoVisiteDaPubblicare.getBoolean(PRIMA_PUBBLICAZIONE)) return true;
		else return (!jsonPianoVisiteDaPubblicare.getBoolean(POSSIBILE_DARE_DISPONIBILITA));
	}
	
	public boolean chiudiRaccoltaDisponibilita() {
		jsonPianoVisiteDaPubblicare.put(POSSIBILE_DARE_DISPONIBILITA, false);
		jsonPianoVisiteDaPubblicare.put(ULTIMO_PIANO, false);
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
		return true;
	}
	
	public boolean isPrimaConfigurazione () {
		return jsonAmbitoTerritoriale.getBoolean(PRIMA_CONFIGURAZIONE);
	}
	
	public void setPrimaConfigurazione () {
		jsonAmbitoTerritoriale.put(PRIMA_CONFIGURAZIONE, false);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
	}
	
	public boolean checkPrimoAvvio () {
		return (jsonUsers.getBoolean(PRIMO_AVVIO));
	}
	
	public void impostaAmbitoTerritoriale (String nome) {
		jsonAmbitoTerritoriale.put(NAME, nome);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, RIGHE_USERS);
	}
	
	public boolean checkIfUserExists(String user) {
		JSONObject json = JSONUtility.readJsonFile(PATH_USERS);
		try {
			return (json.has(user)); 
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean rimuoviTipo (String k) {
		JSONObject tipo = jsonTipiVisite.getJSONObject(k);
		JSONArray volontariTipo = tipo.getJSONArray(VOLONTARI2);
		for (Object volontarioTipo : volontariTipo) {
			JSONObject volontario = jsonUsers.getJSONObject((String) volontarioTipo);
			JSONArray tipiAssociatiVolontario = volontario.getJSONArray(TIPO_VISITA);
			for (int i = 0 ; i < tipiAssociatiVolontario.length() ; i++) {
				if (tipiAssociatiVolontario.get(i).equals(k)) {
					tipiAssociatiVolontario.remove(i);
					break;
				}
			}
			if (tipiAssociatiVolontario.length() == 0) rimuoviVolontario((String) volontarioTipo);
		}
		JSONObject luogo = jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(tipo.getString(LUOGO));
		JSONArray tipiLuogo = luogo.getJSONArray(TIPO_VISITA);
		for (int i = 0 ; i < tipiLuogo.length() ; i++) {
			if (tipiLuogo.getString(i).equals(k)) {
				tipiLuogo.remove(i); //rimuove reference
				break;
			}
		}
		if (checkIfPlaceExists(tipo.getString(LUOGO)))
			if (checkIfLuogoHasNoVisitType(tipo.getString(LUOGO))) rimuoviLuogo(tipo.getString(LUOGO));
		
		jsonTipiVisite.remove(k);
		JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);

		return true;
	}
	
	public boolean rimuoviVolontario (String k) {
		JSONObject volontario = jsonUsers.getJSONObject(k); //prendo volontario
		JSONArray tipiVolontario = volontario.getJSONArray(TIPO_VISITA);
		for (Object tipoVolontario : tipiVolontario) { //ciclo sui suoi tipi
			JSONObject tipo = jsonTipiVisite.getJSONObject((String)tipoVolontario);
			JSONArray volontariTipo = tipo.getJSONArray(VOLONTARI2);
			for (int i = 0 ; i < volontariTipo.length() ; i++) { //cicla sui volontari per rimuovere quello che voglio rimuovere
				if (volontariTipo.get(i).equals(k)) {
					volontariTipo.remove(i); 
					break;
				}
			}
			if (checkIfVisitTypeHasNoVolunteer((String) tipoVolontario)) { //se dopo aver rimosso il volontario il tipoVisita non ha più volontari lo elimino
				rimuoviTipo((String)tipoVolontario); //rimuovo il tipo dai tipi, se rimuovendolo vado ad intaccare Luogo lo rimuove rimuovi tipo
			}
		}
		/*
		JSONObject disponibilita = jsonPianoVisiteDaPubblicare.getJSONObject(DISPONIBILITA);
		disponibilita.remove(k);
		*/
		jsonUsers.remove(k);
		JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);
		//JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
		return true;
	}
	
	public boolean rimuoviLuogo (String k) {
		JSONObject luogo = jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(k);
		JSONArray tipiLuogo = luogo.getJSONArray(TIPO_VISITA);
		for (Object s : tipiLuogo) {
			JSONObject tipo = jsonTipiVisite.getJSONObject((String)s);
			JSONArray volontariAssociati = tipo.getJSONArray(VOLONTARI2);
			for (Object m : volontariAssociati) {
				JSONObject volontario = jsonUsers.getJSONObject((String)m);
				JSONArray tipiVolontario = volontario.getJSONArray(TIPO_VISITA);
				for (int i = 0 ; i < tipiVolontario.length() ; i++) {
					if (tipiVolontario.getString(i).equals((String)s)) {
						tipiVolontario.remove(i);
						break;
					}
				}
				if (tipiVolontario.length() == 0) {
					rimuoviLuogo((String)m); //TODO vedere se può usare rimuoviVolontario: jsonUsers.remove((String)m)
				}
			}
			jsonTipiVisite.remove((String)s);
		}
		jsonAmbitoTerritoriale.getJSONObject(LUOGHI).remove(k);
		
		JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);
		
		return true;
	}
	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, JSONArray tipi_visite, boolean tipiVisitaNecessari) {
		    for (Object k : tipi_visite) {
	    		JSONObject tipo = jsonTipiVisite.getJSONObject((String)k);
	    		JSONArray vol = tipo.getJSONArray(VOLONTARI2);
	    		vol.put(username);
		    }
			JSONObject volontario = putValueInUserObject(username, true, CostantiStruttura.VOLONTARIO, password);
		    volontario.put(TIPO_VISITA, tipi_visite);
			jsonUsers.put(username, volontario);
			JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
			JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
			return true;
	}
	
	public JSONObject getJSONPianoVisite () {
		return jsonPianoVisite;
	}
	
	public JSONObject getJSONUsers () {
		return jsonUsers;
	}
	
	public JSONObject getJSONAmbitoTerritoriale () {
		return jsonAmbitoTerritoriale;
	}
	
	public JSONObject getJSONTipiVisite () {
		return jsonTipiVisite;
	}
	public JSONObject getJSONPianoStorico () {
		return jsonPianoStorico;
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String volontario, String tipoVisita, JSONArray tipi, JSONObject tipo) {
		tipi.put(tipoVisita); //del volontario
		tipo.getJSONArray(VOLONTARI2).put(volontario);
		JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
		return true;
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password) {
		JSONObject configuratore = putValueInUserObject(username, true, CostantiStruttura.CONFIGURATORE, password);
		jsonUsers.put(username, configuratore);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
		return true;
	}
	
	public JSONObject putValueInUserObject (String username, boolean firstAccess, int tipo, String pw) {
		JSONObject user = new JSONObject();
		user.put(USERNAME, username);
		user.put(PRIMO_ACCESSO, true);
		user.put(TIPO_USER, tipo);
		user.put(PASSWORD, pw);
		return user;
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
	
	public boolean modificaCredenziali (String username, Credenziali c) {
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
			System.err.println("Problema in modificaCredenziali: " + e.getMessage()); //da comunicare ad App???
			return false;
		}
		
	}
	
	public boolean isUltimoPianoPubblicato () {
		return jsonPianoVisiteDaPubblicare.getBoolean(ULTIMO_PIANO);
	}
	
	public boolean indicaDatePrecluse (String date) {
		JSONArray datePrecluse = jsonPianoVisiteDaPubblicare.getJSONArray(DATE_PRECLUSE_MESEIPLUS3);
		for (int i = 0; i < datePrecluse.length(); i++) {
			if (datePrecluse.get(i).equals(date)) { 
				return false;
			}
		}
		datePrecluse.put(date);
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10); 
		return true;
	}
	
	public boolean checkPrimoAccesso (String username) {
		JSONObject utente = (JSONObject) jsonUsers.get(username);
		return utente.getBoolean(PRIMO_ACCESSO);
	}
	
	//what a glorious set of stairs we make
	public boolean pubblicaPiano() {
		JSONObject disponibilita = jsonPianoVisiteDaPubblicare.getJSONObject(DISPONIBILITA);
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
						visita.put(LUOGO, jsonTipiVisite.getJSONObject(tipo).getString(LUOGO));
						visita.put(STATO_VISITA, "proposta");
						visita.put(VOLONTARI2, volontariAssociati);
						jsonPianoVisite.getJSONObject(data).put(tipo, visita);
					}
				}
				else {
					jsonPianoVisite.put(data, new JSONObject()); //creo data nel piano
					String tipo = volontarioDisponibilita.getString(data);
					JSONObject visita = new JSONObject();
					JSONArray volontariAssociati = new JSONArray();
					volontariAssociati.put(usernameVol);
					visita.put(LUOGO, jsonTipiVisite.getJSONObject(tipo).getString(LUOGO));
					visita.put(STATO_VISITA, "proposta");
					visita.put(VOLONTARI2, volontariAssociati);
					jsonPianoVisite.getJSONObject(data).put(tipo, visita);
				}
			}
		}
		
		jsonPianoVisiteDaPubblicare.put(DATE_PRECLUSE, jsonPianoVisiteDaPubblicare.getJSONArray(DATE_PRECLUSE_MESEIPLUS3));
		jsonPianoVisiteDaPubblicare.put(DATE_PRECLUSE_MESEIPLUS3, new JSONArray());
		jsonPianoVisiteDaPubblicare.put(DISPONIBILITA, new JSONObject());
		jsonPianoVisiteDaPubblicare.put(ULTIMO_PIANO, true);
		jsonPianoVisiteDaPubblicare.put(MESE_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.MONTH));
		jsonPianoVisiteDaPubblicare.put(ANNO_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.YEAR));
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
		JSONUtility.aggiornaJsonFile(jsonPianoVisite, PATH_VISITE, 10);
		return true;
	}
	
	public String getElencoTipiVisite () { //TODO non ritornare una stringa ma un oggetto che dia tutte le info da stampare
		return jsonTipiVisite.keySet().toString();
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
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
		if (luoghi.has(tag)) return false; //TODO da inserire in controller?
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
	    luoghi.put(tag, nuovoLuogo);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10); 
	    return true;
	}
	
	public boolean aggiungiTipoVisite(String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine,  String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, JSONArray giorniPrenotabili, JSONArray volontari) {
	    JSONObject nuovoTipoVisita = setNewVisitType(luogo, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabili, 
	    		oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, volontari);
		JSONArray tipiLuogo = jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(luogo).getJSONArray(TIPO_VISITA);
		tipiLuogo.put(tipoVisita);
	    jsonTipiVisite.put(tipoVisita, nuovoTipoVisita);
	    JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10);
	    JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
	    return true; 
	}
	
	public JSONObject setNewVisitType (String luogo,String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, JSONArray giorniPrenotabili, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, JSONArray volontari) {
		JSONObject newVisitType = new JSONObject();
		newVisitType.put(LUOGO, luogo);
		newVisitType.put(TITOLO, titolo);
		newVisitType.put(DESCRIPTION, descrizione);
		newVisitType.put(PUNTO_INCONTRO, puntoIncontro);
		newVisitType.put(DATA_INIZIO, dataInizio);
		newVisitType.put(DATA_FINE, dataFine);
		newVisitType.put(GIORNI_PRENOTABILI, giorniPrenotabili);
		newVisitType.put(ORA_INIZIO, oraInizio); 
		newVisitType.put(DURATA_VISITA, durataVisita); 
		newVisitType.put(DA_ACQUISTARE, daAcquistare);
		newVisitType.put(MIN_FRUITORE, minFruitore);
		newVisitType.put(MAX_FRUITORE, maxFruitore);
		newVisitType.put(VOLONTARI2, volontari);
		return newVisitType;
	}
	
	public boolean getPossibilitaDareDisponibilita() {
		return jsonPianoVisiteDaPubblicare.getBoolean(POSSIBILE_DARE_DISPONIBILITA);
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		jsonPianoVisiteDaPubblicare.put(POSSIBILE_DARE_DISPONIBILITA, b);
		jsonPianoVisiteDaPubblicare.put(ULTIMO_PIANO, b);
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
	}
	
	public String getElencoTipiVisiteVolontario(String v) { 
 	    return jsonUsers.getJSONObject(v).getJSONArray(TIPO_VISITA).toString();
 	}
	
	public boolean setPrimaPubblicazione() {
		jsonPianoVisiteDaPubblicare.put(PRIMA_PUBBLICAZIONE, false);
		jsonPianoVisiteDaPubblicare.put(ULTIMO_PIANO, true);
		jsonPianoVisiteDaPubblicare.put(MESE_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.MONTH));
		jsonPianoVisiteDaPubblicare.put(ANNO_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.YEAR));

		apriRaccoltaDisponibilita();
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
		return true;
	}
	
	public boolean isPrimaPubblicazione() {
		return jsonPianoVisiteDaPubblicare.getBoolean(PRIMA_PUBBLICAZIONE);
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		JSONObject t = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
		try {
			return (t.has(tipo)); 
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean checkIfPlaceExists(String luogo) {
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
		return (luoghi.has(luogo));
	}
	
	public JSONArray getTipiVisitaOfVolontario (String username) {
		return jsonUsers.getJSONObject(username).getJSONArray(TIPO_VISITA);
	}
	
	public JSONObject getTipoVisitaJSONObject (String tipo) {
		return jsonTipiVisite.getJSONObject(tipo);
	}
	
	public JSONArray getGiorniPrenotabiliJSONArray (JSONObject tipo) {
		return tipo.getJSONArray(GIORNI_PRENOTABILI);
	}
	
	public boolean checkIfVisitTypeHasNoVolunteer (String tipo) {
		return jsonTipiVisite.getJSONObject(tipo).getJSONArray(VOLONTARI2).length() == 0;
	}
	
	public boolean checkIfLuogoHasNoVisitType (String luogo) {
		return jsonAmbitoTerritoriale.getJSONObject(LUOGHI).getJSONObject(luogo).getJSONArray(TIPO_VISITA).length() == 0;
	}
	
	public boolean checkIfVolontarioHasNoVisitType (String username) {
		return jsonUsers.getJSONObject(username).getJSONArray(TIPO_VISITA).length() == 0;
	}
	
	public boolean inserisciDisponibilita(String data, String username, String tagVisita) {
		JSONObject disponibilita = jsonPianoVisiteDaPubblicare.getJSONObject(DISPONIBILITA);
		//TODO assicurarsi che non prenda datePrecluse
		if (!disponibilita.has(username)) {
			JSONObject d =  new JSONObject();
			disponibilita.put(username, d);
			JSONObject volontario = disponibilita.getJSONObject(username);
			volontario.put(data, tagVisita);
			JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
			return true;
		}
		else {
			JSONObject volontario = disponibilita.getJSONObject(username);
			if (volontario.has(data)) return true;
			else {
				volontario.put(data, tagVisita);
				JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
				return true;
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
