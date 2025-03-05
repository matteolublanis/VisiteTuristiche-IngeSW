package archivio;

import user.Credenziali;
import user.Utente;
import org.json.*;
import utility.JSONUtility;

public class Archivio {
	
	private static final String PATH_USERS = "src/archivio/users.json";
	private static final String PRIMO_AVVIO = "primo_avvio";
	private JSONObject jsonObject = JSONUtility.readJsonFile(PATH_USERS);
	
	public Archivio () {
		System.out.println("Creato archvio.");
	}
	
	public boolean checkPrimoAvvio () {
		boolean primoAvvio = (boolean) jsonObject.get(PRIMO_AVVIO);
		return (primoAvvio);
	}
	
	public void setPrimoAvvio () {
		jsonObject.put(PRIMO_AVVIO, false);
	}
	//TODO GESTIRE EXCEPTION
	public int getTipoUtente (Credenziali c) {
		JSONObject utente = (JSONObject) jsonObject.get(c.getUsername());
		return (int) (utente.get("tipo"));
	}
	
	//TODO da gestire eccezioni
	public boolean credenzialiCorrette (Credenziali c) {
		JSONObject utente = (JSONObject) jsonObject.get(c.getUsername());
		if (utente == (null)) return false;
		return (utente.get("password").equals(c.getPassword()));
	}
	
	public boolean usernameEsiste (String username) {
		return !(jsonObject.get(username).equals(null)); 
	}
	
	@SuppressWarnings("unchecked") //TODO GESTIRE EXCEPTION
	public void modificaCredenziali (String username, Credenziali c) {
		JSONObject utente = (JSONObject) jsonObject.get(username);
		utente.put("username", c.getUsername());
		utente.put("password", c.getPassword());
		jsonObject.remove(username);
		jsonObject.put(c.getUsername(), utente);

		JSONUtility.aggiornaJsonFile(jsonObject, PATH_USERS);
	}
	
	public boolean checkPrimoAccesso (String username) {
		JSONObject utente = (JSONObject) jsonObject.get(username);
		return ((boolean) utente.get("primo_accesso") == true);
	}
	
	@SuppressWarnings("unchecked")
	public void primoAccessoEseguito (Utente user) {
		JSONObject utente = (JSONObject) jsonObject.get(user.getUsername());
		utente.put("primo_accesso", false); 	
		JSONUtility.aggiornaJsonFile(jsonObject, PATH_USERS); 
	}

}
