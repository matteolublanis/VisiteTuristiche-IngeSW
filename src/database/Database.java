package database;

import user.Credenziali;
import user.Utente;
import org.json.*;
import utility.JSONUtility;

public class Database {
	
	private static final String PATH_USERS = "src/database/users.json";
	private static final String PRIMO_AVVIO = "primo_avvio";
	private JSONObject jsonObject = JSONUtility.readJsonFile(PATH_USERS);
	
	protected Database () {
		System.out.println("Creato database.");
	}
	
	protected boolean checkPrimoAvvio () {
		boolean primoAvvio = (boolean) jsonObject.get(PRIMO_AVVIO);
		return (primoAvvio);
	}
	
	@SuppressWarnings("unchecked")
	protected void setPrimoAvvio () {
		jsonObject.put(PRIMO_AVVIO, false);
	}
	
	protected int getTipoUtente (Credenziali c) {
		JSONObject utente = (JSONObject) jsonObject.get(c.getUsername());
		return (int) (utente.get("tipo"));
	}
	
	
	protected boolean credenzialiCorrette (Credenziali c) {
		JSONObject utente = (JSONObject) jsonObject.get(c.getUsername());
		if (utente == (null)) return false;
		return (utente.get("password").equals(c.getPassword()));
	}
	
	protected boolean usernameEsiste (String username) {
		return !(jsonObject.get(username).equals(null)); 
	}
	
	@SuppressWarnings("unchecked")
	protected void modificaCredenziali (Utente user, Credenziali c) {
		JSONObject utente = (JSONObject) jsonObject.get(user.getUsername());
		utente.put("username", c.getUsername());
		utente.put("password", c.getPassword());
		jsonObject.remove(user.getUsername());
		jsonObject.put(c.getUsername(), utente);

		JSONUtility.aggiornaJsonFile(jsonObject, PATH_USERS);
	}
	
	public boolean checkPrimoAccesso (Utente user) {
		JSONObject utente = (JSONObject) jsonObject.get(user.getUsername());
		return ((boolean) utente.get("primo_accesso") == true);
	}
	
	@SuppressWarnings("unchecked")
	protected void primoAccessoEseguito (Utente user) {
		JSONObject utente = (JSONObject) jsonObject.get(user.getUsername());
		utente.put("primo_accesso", false); 	
		JSONUtility.aggiornaJsonFile(jsonObject, PATH_USERS); 
	}

}
