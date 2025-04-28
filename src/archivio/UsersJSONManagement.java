package archivio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import dto.UserDTO;
import dto.VolontarioDTO;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.JSONUtility;

public class UsersJSONManagement {
	private static final String PATH_USERS = "json/users.json";
	private JSONObject jsonUsers = null;
	private static final String PRIMO_AVVIO = "primo_avvio";
	private static final String PASSWORD = "password", TIPO_USER = "tipo", USERNAME = "username", PRIMO_ACCESSO = "primo-accesso";
	private static final String TIPO_VISITA = "tipo-visita";
	private static final String PRENOTAZIONI = "prenotazioni";
	
	public UsersJSONManagement () {
		if (JSONUtility.readJsonFile(PATH_USERS) == null) {
			JSONObject users = new JSONObject();
			users.put(PRIMO_AVVIO, true);
			JSONUtility.aggiornaJsonFile(users, PATH_USERS, 10);
			jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
			impostaCredenzialiNuovoConfiguratore("admin", "admin");
		}
		else jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
	}
	
	public boolean checkPrimoAvvio () {
		return (jsonUsers.getBoolean(PRIMO_AVVIO));
	}
	
	public void loadJSONUsers () {
		jsonUsers = JSONUtility.readJsonFile(PATH_USERS);
	}
	
	public int getTipoUtente (String username) {
		JSONObject utente = null;
		try {
			utente = jsonUsers.getJSONObject(username);
		}
		catch (Exception e) {
			System.err.println("Utente non trovato: getTipoUtente");
			return -1;
		}
		return (int) (utente.get(TIPO_USER));
	}
	
	public Set<UserDTO> getListaUser (int tipo_user) {
			Set<UserDTO> result = new HashSet<>();
			for (String s : JSONUtility.allObjectsSameIntValue(jsonUsers, tipo_user, TIPO_USER)) {
				JSONObject user = jsonUsers.getJSONObject(s);
				switch (tipo_user) {
				case CostantiStruttura.CONFIGURATORE:
					result.add(new VolontarioDTO(user.getString(USERNAME)));
					break;
				case CostantiStruttura.VOLONTARIO:
					List<String> tipiAssociati = new ArrayList<>();
					for (Object m : user.getJSONArray(TIPO_VISITA)) {
						tipiAssociati.add((String)m);
					}
					result.add(new VolontarioDTO(user.getString(USERNAME), tipiAssociati)); //TODO Simple Factory
					break;
				case CostantiStruttura.FRUITORE:
					result.add(new VolontarioDTO(user.getString(USERNAME)));
				default:
					return null;
				}

			}
			return result;
	}
	
	public void aggiornaJsonUsers () {
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 5);
	}
	
	public void setPrimoAvvio () {
		jsonUsers.put(PRIMO_AVVIO, false);
		aggiornaJsonUsers ();
	}
	
	public boolean impostaCredenzialiNuovoFruitore (String username, String password) {
		JSONArray prenotazioni = new JSONArray();
		JSONObject fruitore = putValueInUserObject(username, false, CostantiStruttura.FRUITORE, password);
		fruitore.put(PRENOTAZIONI, prenotazioni);
		jsonUsers.put(username, fruitore);
		aggiornaJsonUsers ();
		return true;
	}
	//Precondizione: username esiste
	public boolean checkCredenzialiCorrette (Credenziali c) {	
		JSONObject utente = (JSONObject) jsonUsers.get(c.getUsername());
		if (utente == (null)) return false;
		return (utente.get(PASSWORD).equals(c.getPassword()));
	
	}
	
	public JSONArray getTipiVisitaOfVolontario (String username) {
		return jsonUsers.getJSONObject(username).getJSONArray(TIPO_VISITA);
	}
	
	public JSONArray getElencoPrenotazioniFruitore (String username) {
		return jsonUsers.getJSONObject(username).getJSONArray(PRENOTAZIONI);
	}
	
	public void inserisciPrenotazioneFruitore (String username, String codicePrenotazione) {
		jsonUsers.getJSONObject(username).getJSONArray(PRENOTAZIONI).put(codicePrenotazione);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
	}
	
	public void rimuoviPrenotazioneFruitore (String username, String codicePrenotazione) {
		JSONArray prenotazioniUser = getElencoPrenotazioniFruitore(username);
		for (int i = 0 ; i < prenotazioniUser.length() ; i++) {
			if (prenotazioniUser.getString(i).equals(codicePrenotazione)) {
				prenotazioniUser.remove(i);
				break;
			}
		}
		aggiornaJsonUsers();
	}

	public void rimuoviTipoFromVolontario (String volontarioTipo, String tipoDaRimuovere) {
		JSONObject volontario = jsonUsers.getJSONObject(volontarioTipo);
		JSONArray tipiAssociatiVolontario = volontario.getJSONArray(TIPO_VISITA);
		for (int i = 0 ; i < tipiAssociatiVolontario.length() ; i++) {
			if (tipiAssociatiVolontario.get(i).equals(tipoDaRimuovere)) {
				tipiAssociatiVolontario.remove(i);
				break;
			}
		}
		aggiornaJsonUsers();
		if (tipiAssociatiVolontario.length() == 0) rimuoviVolontario(volontarioTipo);
	}
	
	public void rimuoviVolontario (String volontarioTipo) {
		jsonUsers.remove(volontarioTipo);
		aggiornaJsonUsers();
	}
	
	public boolean checkIfVolontarioHasNoVisitType (String username) { 
		return jsonUsers.getJSONObject(username).getJSONArray(TIPO_VISITA).length() == 0;
	}
	
	public List<String> getElencoTipiVisiteVolontario(String volontario) {
		
		List<String> result = new ArrayList<>();
		for (Object tipo : jsonUsers.getJSONObject(volontario).getJSONArray(TIPO_VISITA)) {
			result.add((String)tipo);
		}
 	    return result;
 	}
	
	public boolean checkPrimoAccesso (String username) {
		JSONObject utente = (JSONObject) jsonUsers.get(username);
		return utente.getBoolean(PRIMO_ACCESSO);
	}
	
	public boolean primoAccessoEseguito (String user) {
		try {
			JSONObject utente = (JSONObject) jsonUsers.get(user);
			utente.put(PRIMO_ACCESSO, false); 	
			aggiornaJsonUsers ();
			return true;
		}
		catch (Exception e) {
			return false;
		}
		
	}
	
	public boolean modificaCredenziali (String username, Credenziali c) {
		JSONObject utente = null;
		try {
			utente = (JSONObject) jsonUsers.get(username);
			utente.put(USERNAME, c.getUsername());
			utente.put(PASSWORD, c.getPassword());
			//TODO modificare anche file visiteDaPubblicare? tecnicamente le modifica solo all'inizio
			jsonUsers.remove(username);
			jsonUsers.put(c.getUsername(), utente);
			aggiornaJsonUsers ();
			return true;
		}
		catch (Exception e) {
			System.err.println("Problema in modificaCredenziali: " + e.getMessage()); //da comunicare ad App???
			return false;
		}
		
	}
	
	public void aggiungiTipoVisiteAVolontari (JSONArray volontari, String tipoVisita) {
		for (Object volontario : volontari) getTipiVisitaOfVolontario((String)volontario).put(tipoVisita);
	    JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
	}
	
	public void associaVolontarioEsistenteATipoVisitaEsistente (String username, String tipoVisita) {
		JSONArray tipi = getTipiVisitaOfVolontario(username);
		tipi.put(tipoVisita); //del volontario
		aggiornaJsonUsers();
	}
	
	public void impostaCredenzialiNuovoVolontario (String username, String password, JSONArray tipi_visite) {
		JSONObject volontario = putValueInUserObject(username, true, CostantiStruttura.VOLONTARIO, password);
	    volontario.put(TIPO_VISITA, tipi_visite);
		jsonUsers.put(username, volontario);
		aggiornaJsonUsers();
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password) {
		JSONObject configuratore = putValueInUserObject(username, true, CostantiStruttura.CONFIGURATORE, password);
		jsonUsers.put(username, configuratore);
		aggiornaJsonUsers ();
		return true;
	}
	public JSONObject putValueInUserObject (String username, boolean firstAccess, int tipo, String pw) {
		JSONObject user = new JSONObject();
		user.put(USERNAME, username);
		user.put(PRIMO_ACCESSO, firstAccess);
		user.put(TIPO_USER, tipo);
		user.put(PASSWORD, pw);
		return user;
	}
	
	public boolean checkIfUserExists(String user) {
		loadJSONUsers();
		try {
			return (jsonUsers.has(user)); 
		}
		catch (Exception e) {
			return false;
		}
	}

}
