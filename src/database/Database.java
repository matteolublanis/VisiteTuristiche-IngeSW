package database;

import user.Utente;
import org.json.simple.JSONObject;
import utility.JSONUtility;

public class Database {
	
	private JSONObject jsonObject = JSONUtility.readJsonFile("src/memoria.json");
	
	public Database () {
		System.out.println("Creato database server.");

	}
	
	public boolean passwordCorretta (String user, String password) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		return (utente.get("password").equals(password));
	}
	
	public boolean usernameEsiste (String username) {
		return !(jsonObject.get(username).equals(null)); 
	}
	
	@SuppressWarnings("unchecked")
	public void modificaCredenziali (Utente user, String username, String password) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		utente.put("username", username);
		utente.put("password", password);
		
		JSONUtility.aggiornaJsonFile(jsonObject);
	}
	
	@SuppressWarnings("unchecked")
	public void primoAccessoEseguito (Utente user) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		utente.put("primo-accesso", true);
		
		JSONUtility.aggiornaJsonFile(jsonObject);
	}
	
}
