package user;

import org.json.simple.JSONObject;
import utility.JSONUtility;

public class GestoreLogin {
	private static JSONObject jsonObject = JSONUtility.readJsonFile("src/memoria.json");
	
	public static boolean passwordCorretta (String user, String password) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		return (utente.get("password").equals(password));
	}
	
	public static boolean usernameEsiste (String username) {
		return !(jsonObject.get(username).equals(null)); 
	}
	
	@SuppressWarnings("unchecked")
	public static void modificaCredenziali (Utente user, String username, String password) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		utente.put("username", username);
		utente.put("password", password);
		
		JSONUtility.aggiornaJsonFile(jsonObject);
	}
	
	@SuppressWarnings("unchecked")
	public static void primoAccessoEseguito (Utente user) {
		JSONObject utente = (JSONObject) jsonObject.get(user);
		utente.put("primo-accesso", true);
		
		JSONUtility.aggiornaJsonFile(jsonObject);
	}
	
}