package archivio;

import user.Utente;

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
	private static final String PATH_USERS = "src/archivio/users.json";
	private static final String AMBITO = "src/archivio/ambito_territoriale.json";
	private static final String PRIMO_AVVIO = "primo_avvio";
	private JSONObject jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
	private JSONObject jsonAmbitoTerritoriale = JSONUtility.readJsonFile(AMBITO);
	
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
	
	//Può causare eccezione
	public boolean checkPrimoAvvio () {
		boolean primoAvvio = (boolean) jsonUsers.get(PRIMO_AVVIO);
		return (primoAvvio);
	}
	
	public void impostaAmbitoTerritoriale (String nome) {
		jsonAmbitoTerritoriale.put("nome", nome);
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
	
	public void impostaMaxPrenotazione (int max) {
		jsonAmbitoTerritoriale.put("max_prenotazione", max);
		JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, AMBITO, RIGHE_USERS);

	}
	
	//TODO gestire eccezione
	public void setPrimoAvvio () {
		jsonUsers.put(PRIMO_AVVIO, false);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
	}
	//TODO gestire eccezione
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
	
	//TODO gestire eccezioni
	public boolean checkCredenzialiCorrette (Credenziali c) {
		JSONObject utente = null;
		try {
			utente = (JSONObject) jsonUsers.get(c.getUsername());
		}
		catch (Exception e) {
			//TODO nulla
		}
		
		if (utente == (null)) return false;
		return (utente.get("password").equals(c.getPassword()));
	}
	
	//TODO gestire eccezione
	public boolean usernameEsiste (String username) {
		return !(jsonUsers.get(username).equals(null)); 
	}
	
	//TODO gestire eccezione
	public boolean modificaCredenziali (String username, Credenziali c) {
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
	
	//TODO gestire eccezione
	public boolean checkPrimoAccesso (String username) {
		JSONObject utente = (JSONObject) jsonUsers.get(username);
		return ((boolean) utente.get("primo_accesso") == true);
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
	
	public void modificaValoreAmbitoTerritoriale (String chiave, String s) {
		
	}
	
	public String getElencoTipiVisiteLuogo () {
		String result = "";
		HashMap<String, String> visite = JSONUtility.getAllSameValsFromObjects(jsonAmbitoTerritoriale, "tipo_visite");
		for (String k : visite.keySet()) {
			result += "Luogo: " + k + ", tipi visite: " + visite.get(k) + "\n";		
		}
		return result;
	}
	
	//TODO gestire eccezione
	public void primoAccessoEseguito (String user) {
		JSONObject utente = (JSONObject) jsonUsers.get(user);
		utente.put("primo_accesso", false); 	
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS); 
	}

	public String getCredenzialiConfIniziale() {
		return CREDENZIALI_CONF_INIZIALE;
	}

}
