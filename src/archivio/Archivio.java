package archivio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.*;


import dto.*;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.JSONUtility;
import utility.Time;

public class Archivio {
	
	private static final String PRENOTAZIONI = "prenotazioni";

	private static final String NUMERO_ISCRITTI = "numero-iscritti";

	public static final String PROPOSTA = "proposta", CANCELLATA = "cancellata", CONFERMATA = "confermata", COMPLETA = "completa", EFFETTUATA = "effettuata";

	private static final String ANNO_ULTIMA_PUBBLICAZIONE = "anno-ultima-pubblicazione";

	private static final String MESE_ULTIMA_PUBBLICAZIONE = "mese-ultima-pubblicazione";
	private static int RELEASE_DAY = 16;

	public static final String POSSIBILE_DARE_DISPONIBILITA = "possibile-dare-disponibilita",
			PRIMA_PUBBLICAZIONE = "prima-pubblicazione", VOLONTARI2 = "volontari", MAX_FRUITORE = "max-fruitore", MIN_FRUITORE = "min-fruitore", DA_ACQUISTARE = "da-acquistare",
			DURATA_VISITA = "durata-visita", ORA_INIZIO = "ora-inizio", GIORNI_PRENOTABILI = "giorni-prenotabili", DATA_FINE = "data-fine", DATA_INIZIO = "data-inizio",
			PUNTO_INCONTRO = "punto-incontro", DESCRIPTION = "descrizione", COLLOCAZIONE = "collocazione", DATE_PRECLUSE_MESEIPLUS3 = "datePrecluseI+3",
			MAX_PRENOTAZIONE = "max_prenotazione", STATO_VISITA = "stato", TITOLO = "titolo", LUOGHI = "luoghi", DISPONIBILITA = "disponibilita",
			TIPO_VISITA = "tipo-visita", PASSWORD = "password", TIPO_USER = "tipo", USERNAME = "username", PRIMO_ACCESSO = "primo-accesso", 
			PRIMA_CONFIGURAZIONE = "prima_configurazione", PRIMO_AVVIO = "primo_avvio", NAME = "nome", ULTIMO_PIANO = "ultimo-piano-pubblicato", LUOGO = "luogo",
			DATE_PRECLUSE = "date-precluse",
			
			
			
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
		else return (jsonPianoVisiteDaPubblicare.getBoolean(ULTIMO_PIANO) &&
				!jsonPianoVisiteDaPubblicare.getBoolean(POSSIBILE_DARE_DISPONIBILITA)); //POSSO MODIFICARE SOLO SE TRA PUBBLICAZIONE E RITORNATA POSS DISP
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
	
	public Set<UserDTO> getListaUser (String username, int tipo_user) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			Set<UserDTO> result = new HashSet<>();
			for (String s : JSONUtility.allObjectsSameIntValue(jsonUsers, tipo_user, Archivio.TIPO_USER)) {
				JSONObject user = (JSONObject) jsonUsers.get(s);
				switch (tipo_user) {
				case CostantiStruttura.CONFIGURATORE:
					result.add(new UserDTO(user.getString(USERNAME)));
					break;
				case CostantiStruttura.VOLONTARIO:
					List<String> tipiAssociati = new ArrayList<>();
					for (Object m : user.getJSONArray(Archivio.TIPO_VISITA)) tipiAssociati.add((String)m);
					result.add(new UserDTO(user.getString(USERNAME), tipiAssociati));
					break;
				case CostantiStruttura.FRUITORE:
					result.add(new UserDTO(user.getString(USERNAME)));
				}
			}
			return result;
		}
		else return null;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		
	    List<VisitaDTO> visiteList = new ArrayList<>();

		for (String k : jsonPianoVisite.keySet()) { //giorno
			JSONObject j = jsonPianoVisite.getJSONObject(k); //prende le visite associate a quel giorno
			for (String m : j.keySet()) { //visite del giorno
				JSONObject visita = j.getJSONObject(m);
                JSONObject tipoVisita = jsonTipiVisite.getJSONObject(m);
                VisitaDTO visitaDTO = new VisitaDTO( 
	                    tipoVisita.getString(TITOLO),
	                    k,
	                    visita.getString(Archivio.LUOGO),
	                    visita.getString(Archivio.STATO_VISITA)
	                );
                visiteList.add(visitaDTO);
			}
		}
		for (String giornoVisite : jsonPianoStorico.keySet()) {
			JSONObject visiteStoricheGiornoX = jsonPianoStorico.getJSONObject(giornoVisite); 
			for (String visitaStorica : visiteStoricheGiornoX.keySet()) { 
				VisitaDTO visitaDTO = new VisitaDTO(
						visitaStorica,
						giornoVisite,
						visiteStoricheGiornoX.getString(visitaStorica), //TODO per salvare anche cancellate, renderlo un JSONObject, o rivedere con DBMS
						"effettuata");
                visiteList.add(visitaDTO);
			}
		}
		return visiteList;
	}
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore () {
		
	    List<VisitaDTO> visiteList = new ArrayList<>();

	    for (String k : jsonPianoVisite.keySet()) { // Giorno
	        JSONObject j = jsonPianoVisite.getJSONObject(k); // Visite del giorno
	        for (String m : j.keySet()) { // Singola visita
	            JSONObject visita = j.getJSONObject(m);
	            String statoVisita = visita.getString(STATO_VISITA);
	            if (statoVisita.equals(CONFERMATA) || statoVisita.equals(CANCELLATA) || statoVisita.equals(PROPOSTA)) {
	                JSONObject tipoVisita = jsonTipiVisite.getJSONObject(m);
	                VisitaDTO visitaDTO = new VisitaDTO(
	                    tipoVisita.getString(TITOLO),
	                    tipoVisita.getString(DESCRIPTION),
	                    tipoVisita.getString(PUNTO_INCONTRO),
	                    k,
	                    tipoVisita.getString(ORA_INIZIO),
	                    tipoVisita.getBoolean(DA_ACQUISTARE),
	                    statoVisita
	                );
	                visiteList.add(visitaDTO);
	            }
	        }
	    }
	    return visiteList;
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
					rimuoviVolontario((String)m);  
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
	
	public List<String> getDatePrecluse () {
		
		JSONArray datePrecluse = jsonPianoVisiteDaPubblicare.getJSONArray(DATE_PRECLUSE);
		List<String> result = new ArrayList<String>();
		for (Object m : datePrecluse) {
			result.add((String) m);
		}
		return result;
	}
	
	public boolean checkIfCanLinkVolontario (String volontario, String tipoVisita) {
		
		JSONObject v = jsonUsers.getJSONObject(volontario);
		JSONArray tipi = v.getJSONArray(TIPO_VISITA);
		JSONObject tipo = jsonTipiVisite.getJSONObject(tipoVisita);
		if (volontarioAlreadyLinkedForThatDay(tipo.getString(DATA_INIZIO), tipo.getString(DATA_FINE), tipo.getString(ORA_INIZIO), tipo.getInt(DURATA_VISITA), tipo.getJSONArray(GIORNI_PRENOTABILI).toString(), tipi)) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean volontarioAlreadyLinkedForThatDay (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONArray tipiVisitaVolontario) {
		
		System.out.println(1);
		for (Object k : tipiVisitaVolontario) {  //OK
			System.out.println(1);
			JSONObject tipo = jsonTipiVisite.getJSONObject((String)k); //prende ogni tipo dal json dei tipi
			if (Time.comesBefore(dateStart1, tipo.getString(DATA_FINE)) && !Time.comesBefore(dateFinish1, tipo.getString(DATA_INIZIO))) { //controlla se periodi intersecano
				System.out.println(2);
				JSONArray days2 = tipo.getJSONArray(GIORNI_PRENOTABILI); //prende giorni prenotabili del tipo
				for (Object d : days2) { 
					System.out.println(3);
					if (days1.contains((String)d)) return true; //se i giorni intersecano allora volontario linkato per quei giorni
				}
			}
		}
		return false;
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String volontario, String tipoVisita) {
		
		JSONObject volontarioJson = jsonUsers.getJSONObject(volontario);
		JSONArray tipi = volontarioJson.getJSONArray(TIPO_VISITA);
		tipi.put(tipoVisita); //del volontario
		JSONObject tipo = jsonTipiVisite.getJSONObject(tipoVisita);
		tipo.getJSONArray(VOLONTARI2).put(volontario);
		JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, 10);
		return true;
	}
	
	public boolean impostaCredenzialiNuovoFruitore (String username, String password) {
		JSONObject fruitore = putValueInUserObject(username, false, CostantiStruttura.FRUITORE, password);
		jsonUsers.put(username, fruitore);
		//TODO il fruitore tecnicamente ha più dati di un configuratore: codice prenotazione, visite a cui partecipa (non necessariamente da salvare col fruitore, può essere legato al codice)
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
		return true;
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password) {
		JSONObject configuratore = putValueInUserObject(username, true, CostantiStruttura.CONFIGURATORE, password);
		jsonUsers.put(username, configuratore);
		JSONUtility.aggiornaJsonFile(jsonUsers, PATH_USERS, RIGHE_USERS);
		return true;
	}
	
	public List<String> getElencoLuoghiVisitabili (String username) { 
		
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			List<String> result = new ArrayList<>();
			try {
				JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
				for (String k : luoghi.toMap().keySet()) {
					JSONObject j = luoghi.getJSONObject(k);
					if (!j.get(TIPO_VISITA).equals("[]")) {
						result.add(j.getString(NAME));
					}
				}
				return result;
			}
			catch (Exception e) {
				System.out.println("Errore nella raccolta dei luoghi visitabili:" + e.getMessage());
				return null;
			}
		}
		else return null;
	}
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String username) {
		
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			HashMap<String, List<String>> result = new HashMap<>();
			try {
				JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(Archivio.LUOGHI);
				for (String nomeLuogo : luoghi.toMap().keySet()) {
					JSONObject infoLuogo = luoghi.getJSONObject(nomeLuogo);
					if (!infoLuogo.get(Archivio.TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
						JSONArray tipiVisite = infoLuogo.getJSONArray(Archivio.TIPO_VISITA);
						List<String> tipiVisiteAssociati = new ArrayList<>();
						for (int i = 0 ; i < tipiVisite.length() ; i++) 
						{
							tipiVisiteAssociati.add(jsonTipiVisite.getJSONObject(tipiVisite.get(i).toString()).getString(TITOLO));
						}
						result.put(infoLuogo.getString(NAME), tipiVisiteAssociati);	
					}
				}
				return result;
			}
			catch (Exception e) {
				System.out.println("Errore nella raccolta dei luoghi visitabili:" + e.getMessage());
				return null;
			}
		}
		else return null;
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
			//TODO modificare anche file visiteDaPubblicare? tecnicamente le modifica solo all'inizio
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
						visita.put(STATO_VISITA, PROPOSTA);
						visita.put(VOLONTARI2, volontariAssociati);
						visita.put(NUMERO_ISCRITTI, 0);
						visita.put(PRENOTAZIONI, new JSONArray());
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
					visita.put(STATO_VISITA, PROPOSTA);
					visita.put(VOLONTARI2, volontariAssociati);
					visita.put(NUMERO_ISCRITTI, 0);
					visita.put(PRENOTAZIONI, new JSONArray());
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
	
	public Set<String> getElencoTipiVisite () { //TODO non ritornare una stringa ma un oggetto che dia tutte le info da stampare
		
		return jsonTipiVisite.keySet();
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
	
	public boolean isReleaseOrLaterDay() {
		return (RELEASE_DAY <= Time.getActualDateValue(Time.DAY));
	}
	
	public boolean tryApriRaccoltaDisponibilita(String username) {  //OK
		
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE && !isPrimaPubblicazione() && Time.getActualDateValue(Time.DAY) >= RELEASE_DAY&&
				((getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) && getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR)))) { //aggiornato quando pubblicato
			if (!isUltimoPianoPubblicato() || getPossibileDareDisponibilita()) return false; //SE ULTIMO PIANO NON PUBBLICATO O GIA' APERTA RITORNA FALSO
			else return apriRaccoltaDisponibilita();
		}
		else {
			return false;
		}
	}
	
	public boolean tryPubblicaPiano(String username) { //OK
		
		if(isPrimaPubblicazione()) return setPrimaPubblicazione(); 
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE && Time.getActualDateValue(Time.DAY) >= RELEASE_DAY && //SE ULTIMO PIANO PUBBLICATO MESE SCORSO PUBBLICA
				isUltimaPubblicazioneMeseScorso()) {
			return pubblicaPiano();
		}
		else return false;
	}
	
	public boolean tryChiudiRaccoltaDisponibilita (String username) { //OK
		
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE && 
				!isPrimaPubblicazione() && Time.getActualDateValue(Time.DAY) >= RELEASE_DAY &&
						isUltimaPubblicazioneMeseScorso()) {
			if (getPossibileDareDisponibilita()) return chiudiRaccoltaDisponibilita(); 
			else return false;
		}
		else {
			return false;
		}
	}
	
	private boolean isUltimaPubblicazioneMeseScorso () {
		
		return ((getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) - 1 && getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR)) ||
				(getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) - 1 + 12 && getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR) - 1));
	}
	
	public HashMap<String, List<String>> getDatePerDisponibilita(String username) {	 //OK
		
		if (getTipoUtente(username) == CostantiStruttura.VOLONTARIO) {
			HashMap<String, List<String>> result = new HashMap<> () ;
			JSONArray tipiVisite = getTipiVisitaOfVolontario(username);
			for (Object s : tipiVisite) { 
				JSONObject tipo = getTipoVisitaJSONObject((String)s);
				try {
					String[] periodoDaDareDisponibilita = Time.getAvailabilityWindow(tipo.getString(Archivio.DATA_INIZIO), tipo.getString(Archivio.DATA_FINE), Time.getDesideredMonthAndYear(RELEASE_DAY, Time.getActualDate()));
					JSONArray giorni = tipo.getJSONArray(Archivio.GIORNI_PRENOTABILI);
					
					List<String> days = new ArrayList<>();
					for (Object g : giorni) {
						List<String> k = Time.getAllDatesSameDayOfTheWeek(periodoDaDareDisponibilita[0], periodoDaDareDisponibilita[1], Arrays.asList(Archivio.GIORNISETTIMANA).indexOf((String) g) + 1);
						for (String date : k) {
							days.add(date);
						}
					}
					result.put((String)s, days);
				}	
				catch (Exception e) {
					//do smth
				}
			}
			return result;
		}
		else return null;
	}
	
	public boolean aggiungiLuogo (String tag, String nome, String collocazione, Set<String> tipiVisitaVal) {
		
		JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(LUOGHI);
		if (luoghi.has(tag)) return false; 
		JSONObject nuovoLuogo = new JSONObject();
		JSONArray tipiVisita = new JSONArray();
	    if (tipiVisitaVal != null) {
	    	for (String tipo : tipiVisitaVal) {
		    	if (!checkValueExistance(tipo, PATH_TIPI_VISITE) && !tipo.equals("")) return false;
		    	else {
		    		if (!tipo.equals("")) tipiVisita.put(tipo); 
		    	}
	    	}
	    } //se null bisogna capire come gestire
	    nuovoLuogo.put(NAME, nome);
		nuovoLuogo.put(COLLOCAZIONE, collocazione);
	    nuovoLuogo.put(TIPO_VISITA, tipiVisita);
	    luoghi.put(tag, nuovoLuogo);
	    JSONUtility.aggiornaJsonFile(jsonAmbitoTerritoriale, PATH_AMBITO, 10); 
	    return true;
	}
	
	public boolean tryImpostaCredenzialiNuovoVolontario (String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (checkIfUserExists(username)) return false; //OK
		JSONArray tipiVisite = new JSONArray();
	    if (tipi_visiteVal != null) {
	    	for (String tipo : tipi_visiteVal) {
		    	if (!checkIfVisitTypeExists(tipo) && !tipo.equals("")) return false;
		    	else {
		    		if (!tipo.equals(""))tipiVisite.put(tipo);
		    	}
		    }
	    }
	    if (tipiVisitaNecessario && tipiVisite.length() == 0) return false;
		return impostaCredenzialiNuovoVolontario(username, password, tipiVisite, tipiVisitaNecessario);
	}
	
	public boolean tryAggiungiVisite (String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal) {
		
		JSONArray giorniPrenotabili = new JSONArray();
	    String days = "";
	    for (Integer k : giorniPrenotabiliVal) {
	    	try {
	    		int j = (k);
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
	    JSONArray volontari = new JSONArray();
	    for (String k : volontariVal) {
    		JSONObject volontario = jsonUsers.getJSONObject(k);
    		JSONArray tipi = volontario.getJSONArray(TIPO_VISITA);
    		if (volontarioAlreadyLinkedForThatDay(dataInizio, dataFine, oraInizio, durataVisita, days, tipi)) return false;
    		volontari.put(k);
	    }
		return aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, giorniPrenotabili, volontari);
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
	
	public boolean aggiungiTipoVisite(String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine,  String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, JSONArray giorniPrenotabili, JSONArray volontari) {
		
		for (Object volontario : volontari) jsonUsers.getJSONObject((String)volontario).getJSONArray(TIPO_VISITA).put(tipoVisita);
		
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
	
	public List<String> getElencoTipiVisiteVolontario(String volontario) {
		
		List<String> result = new ArrayList<>();
		for (Object tipo : jsonUsers.getJSONObject(volontario).getJSONArray(TIPO_VISITA)) {
			result.add((String)tipo);
		}
 	    return result;
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
	
	public boolean inserisciDisponibilita(String data, String username, String tagVisita) { //ok
		
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

	public boolean checkValueExistance (String key, String path) { //ok
		JSONObject json = JSONUtility.readJsonFile(path);
		try {
			return !(json.get(key).equals(null)); 
		}
		catch (Exception e) {
			return false;
		}
	}

	public Credenziali getCredenzialiConfIniziale() { //ok
		return new Credenziali(CREDENZIALI_CONF_INIZIALE[0], CREDENZIALI_CONF_INIZIALE[1]);
	}

}
