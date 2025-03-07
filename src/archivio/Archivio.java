package archivio;

import user.Credenziali;
import user.Utente;

import java.util.HashMap;

import org.json.*;

import utility.CostantiStruttura;
import utility.JSONUtility;

public class Archivio {
	
	private static final int RIGHE_USERS = 5;
	private static final String PATH_USERS = "src/archivio/users.json";
	private static final String AMBITO = "src/archivio/ambito_territoriale.json";
	private static final String PRIMO_AVVIO = "primo_avvio";
	private JSONObject jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
	private JSONObject jsonAmbitoTerritoriale = JSONUtility.readJsonFile(AMBITO);
	
	public Archivio () {
		System.out.println("Creato archivio.");
	}
	
	public boolean checkPrimoAvvio () {
		boolean primoAvvio = (boolean) jsonUsers.get(PRIMO_AVVIO);
		return (primoAvvio);
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
	}
	//TODO GESTIRE EXCEPTION
	public int getTipoUtente (Credenziali c) {
		JSONObject utente = (JSONObject) jsonUsers.get(c.getUsername());
		return (int) (utente.get("tipo"));
	}
	
	//TODO da gestire eccezioni
	public boolean credenzialiCorrette (Credenziali c) {
		JSONObject utente = (JSONObject) jsonUsers.get(c.getUsername());
		if (utente == (null)) return false;
		return (utente.get("password").equals(c.getPassword()));
	}
	
	public boolean usernameEsiste (String username) {
		return !(jsonUsers.get(username).equals(null)); 
	}
	
	@SuppressWarnings("unchecked") //TODO GESTIRE EXCEPTION
	public void modificaCredenziali (String username, Credenziali c) {
		JSONObject utente = (JSONObject) jsonUsers.get(username);
		utente.put("username", c.getUsername());
		utente.put("password", c.getPassword());
		jsonUsers.remove(username);
		jsonUsers.put(c.getUsername(), utente);

		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
	}
	
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
	
	public String getElencoTipiVisiteLuogo () {
		String result = "";
		HashMap<String, String> visite = JSONUtility.getAllSameValsFromObjects(jsonAmbitoTerritoriale, "tipo_visite");
		for (String k : visite.keySet()) {
			result += "Luogo: " + k + ", tipi visite: " + visite.get(k) + "\n";		
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public void primoAccessoEseguito (Utente user) {
		JSONObject utente = (JSONObject) jsonUsers.get(user.getUsername());
		utente.put("primo_accesso", false); 	
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS); 
	}

}
