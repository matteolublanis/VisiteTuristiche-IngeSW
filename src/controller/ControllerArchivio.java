package controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import archivio.Archivio;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.JSONUtility;
import utility.Time;

public class ControllerArchivio {
	
	private static final String SPLIT_REGEX_LISTA = "\\s*,\\s*";
	private Archivio d;
	private static int RELEASE_DAY = 16;

	public ControllerArchivio (Archivio d) {
		this.d = d; 
	}

	public boolean checkPrimoAvvio () {
		return d.checkPrimoAvvio();
	}
	
	public int getTipoUtente (String username) {
		return d.getTipoUtente(username);
	}
	
	public boolean pubblicaPiano() {
		return d.pubblicaPiano();
	}
	
	public boolean chiudiRaccoltaDisponibilita (String username) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE && !d.isPrimaPubblicazione() && Time.getActualDayOfTheMonth() >= RELEASE_DAY) {
			if (d.getPossibileDareDisponibilita()) return d.chiudiRaccoltaDisponibilita(); 
			else return false;
		}
		else {
			return false;
		}
	}
	
	public boolean apriRaccoltaDisponibilita(String username) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE && !d.isPrimaPubblicazione() && Time.getActualDayOfTheMonth() >= RELEASE_DAY) {
			if (!d.isUltimoPianoPubblicato() || d.getPossibileDareDisponibilita()) return false;
			else return d.apriRaccoltaDisponibilita();
		}
		else {
			return false;
		}
	}
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String volontario, String tipoVisita) {
		if (!d.checkIfUserExists(volontario) || !d.checkIfVisitTypeExists(tipoVisita)) {
			JSONObject v = d.getJSONUsers().getJSONObject(volontario);
			JSONArray tipi = v.getJSONArray(Archivio.TIPO_VISITA);
			JSONObject tipo = d.getJSONTipiVisite().getJSONObject(tipoVisita);
			if (volontarioAlreadyLinkedForThatDay(tipo.getString(Archivio.DATA_INIZIO), tipo.getString(Archivio.DATA_FINE), tipo.getString(Archivio.ORA_INIZIO), Integer.parseInt(tipo.getString(Archivio.DURATA_VISITA)), tipo.getJSONArray(Archivio.GIORNI_PRENOTABILI).toString(), tipi)) {
				return false;
			}
			else {
				return d.associaVolontarioEsistenteATipoVisitaEsistente(volontario, tipoVisita, tipi, tipo);
			}
		}
		else return false;
	}
	
	public boolean volontarioAlreadyLinkedForThatDay (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONArray tipiVisitaVolontario) {
		for (Object k : tipiVisitaVolontario) { 
			JSONObject tipo = d.getJSONTipiVisite().getJSONObject((String)k); //prende ogni tipo dal json dei tipi
			if (Time.comesBefore(dateStart1, tipo.getString(Archivio.DATA_FINE)) && !Time.comesBefore(dateFinish1, tipo.getString(Archivio.DATA_INIZIO))) { //controlla se periodi intersecano
				JSONArray days2 = tipo.getJSONArray(Archivio.GIORNI_PRENOTABILI); //prende giorni prenotabili del tipo
				for (Object d : days2) { 
					if (days1.contains((String)d)) return true; //se i giorni intersecano allora volontario linkato per quei giorni
				}
			}
		}
		return false;
	}
	
	public boolean getPossibilitaDareDisponibilita() { 
		return d.getPossibilitaDareDisponibilita();
	}
	
	public boolean inserisciDisponibilita(String data, String username) {
		HashMap<String, List<String>> m = getDatePerDisponibilita(username);
		for (String k : m.keySet()) {
			if (m.get(k).contains(data)) return d.inserisciDisponibilita(data, username, k);
		}
		return false;
	}
	
	public HashMap<String, List<String>> getDatePerDisponibilita(String username) {	
		if (getTipoUtente(username) == CostantiStruttura.VOLONTARIO) {
			HashMap<String, List<String>> result = new HashMap<> () ;
			JSONArray tipiVisite = d.getTipiVisitaOfVolontario(username);
			for (Object s : tipiVisite) { 
				JSONObject tipo = d.getTipoVisitaJSONObject((String)s);
				try {
					String[] periodoDaDareDisponibilita = Time.getAvailabilityWindow(tipo.getString(Archivio.DATA_INIZIO), tipo.getString(Archivio.DATA_FINE), Time.getDesideredMonthAndYear(RELEASE_DAY, Time.getActualDate()));
					JSONArray giorni = tipo.getJSONArray(Archivio.GIORNI_PRENOTABILI);
					
					String days = "";
					for (Object g : giorni) {
						days += Time.getAllDatesSameDayOfTheWeek(periodoDaDareDisponibilita[0], periodoDaDareDisponibilita[1], Arrays.asList(Archivio.GIORNISETTIMANA).indexOf((String) g) + 1); //calcola giorni disponibili
					}
					result.put((String)s, Arrays.asList(days.split(" ")));
				}	
				catch (Exception e) {
					//do smth
				}
			}
			return result;
		}
		else return null;
	}
	
	public String getElencoTipiVisite () {
 		return d.getElencoTipiVisite();
 	}
 
 	public String getElencoTipiVisiteVolontario (String username) {
 		return d.getElencoTipiVisiteVolontario(username);
 	}
	
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, String tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (checkIfUserExists(username)) return false;
		JSONArray tipiVisite = new JSONArray();
	    String[] s = tipi_visiteVal.split(SPLIT_REGEX_LISTA);
	    for (String k : s) {
	    	if (!checkIfVisitTypeExists(k) && !k.equals("")) return false;
	    	else {
	    		if (!k.equals(""))tipiVisite.put(k);
	    	}
	    }
	    if (tipiVisitaNecessario && tipiVisite.length() == 0) return false;
		return d.impostaCredenzialiNuovoVolontario(username, password, tipiVisite, tipiVisitaNecessario);
	}
	
	public boolean rimuoviLuogo (String luogo, String username) {
		if (d.checkIfPlaceExists(luogo) && canAddOrRemove(username)) return d.rimuoviLuogo(luogo);
		else return false;
	}
	
	public boolean rimuoviVolontario (String volontario, String username) {
		if (d.checkIfUserExists(volontario) && d.getTipoUtente(volontario) == CostantiStruttura.VOLONTARIO && canAddOrRemove(username)) return d.rimuoviVolontario(volontario);
		else return false;
	}
	
	public boolean rimuoviTipo (String tipo, String username) {
		if (d.checkIfVisitTypeExists(tipo) && canAddOrRemove(username)) return d.rimuoviTipo(tipo);
		else return false;
	}
	
	public boolean canAddOrRemove(String username) {
		if (checkIfUserExists(username) && getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return d.canAddOrRemove();
		}
		else return false;
	}
	
	public boolean checkPrimaConfigurazioneArchivio (String username) {
		if (d.checkIfUserExists(username) && d.getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			return d.isPrimaConfigurazione();
		}
		else return false;
	}
	
	public Credenziali getCredenzialiIniziali () {
		if (checkPrimoAvvio()) {
			d.setPrimoAvvio();
			return (d.getCredenzialiConfIniziale()); 
		}
		else return null; 
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		d.setPossibilitaDareDisponibilitaVolontari(b);
	}
	
	public boolean isReleaseOrLaterDay() {
		return (RELEASE_DAY <= Time.getActualDayOfTheMonth());
	}
	
	public boolean isPrimaPubblicazione () {
		return d.isPrimaPubblicazione();
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		return d.checkCredenzialiCorrette(c);
	}
	
	public boolean checkPrimoAccesso (String username) {
		if (!checkIfUserExists(username)) return false;
		else return (d.checkPrimoAccesso(username)); 
	}
	
	public boolean indicaDatePrecluse (String date) {
		if (Time.isValidDate(date) && Time.isThisDateInMonthiplus3(date)) return d.indicaDatePrecluse(date);
		else return false;
	}
	
	public String getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		String result = "";
		JSONObject jsonPianoVisite = d.getJSONPianoVisite();
		JSONObject jsonAmbitoTerritoriale = d.getJSONAmbitoTerritoriale();
		JSONObject jsonTipiVisite = d.getJSONTipiVisite();
		JSONObject jsonPianoStorico = d.getJSONPianoStorico();
		//ciclo sul pianoVisite attuale
		for (String k : jsonPianoVisite.keySet()) { //giorno
			JSONObject j = jsonPianoVisite.getJSONObject(k); 
			for (String m : j.keySet()) { //visite del giorno
				JSONObject visita = j.getJSONObject(m);
				JSONObject luogo = jsonAmbitoTerritoriale.getJSONObject(Archivio.LUOGHI).getJSONObject(m);
				result += "Giorno: " + k + ", Luogo: " + luogo.getString(Archivio.NAME) + ", Visita: " + jsonTipiVisite.getJSONObject(visita.getString(Archivio.TIPO_VISITA)).getString(Archivio.TITOLO) + ", Stato: " + visita.getString(Archivio.STATO_VISITA) + "\n";
			}
		}
		for (String k : jsonPianoStorico.keySet()) {
			JSONObject j = jsonPianoStorico.getJSONObject(k); 
			for (String m : j.keySet()) { //TODO rivedere
				String s = j.getString(m);
				result += "Giorno: " + k + ", Tag Luogo: " + m + ", Tipo Visita: " + s + ", Stato: effettuata\n";
			}
		}
		return result;
	}
	
	public boolean cambiaCredenziali (String username, Credenziali c, ControllerUtente gu) {
		if (!checkIfUserExists(username)) return false;
		if (checkIfUserExists(c.getUsername())) return false;
		if (d.modificaCredenziali(username, c)) {
			d.primoAccessoEseguito(c.getUsername());
			gu.setUsername(c.getUsername());
			return true;
		}	
		return false;
	}
	
	public boolean checkIfPlaceExists (String luogo) {
		return d.checkIfPlaceExists(luogo);
	}
	
	public boolean aggiungiLuogo (String tag, String nome, String collocazione, String tipiVisitaVal) {
		return d.aggiungiLuogo(tag, nome, collocazione, tipiVisitaVal);
	}
	
	public void setPrimaConfigurazione() {
		d.setPrimaConfigurazione();
	}
	
	public boolean aggiungiTipoVisite (String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, String giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, String volontariVal) {
		JSONArray giorniPrenotabili = new JSONArray();
	    String days = "";
	    for (String k : giorniPrenotabiliVal.split(SPLIT_REGEX_LISTA)) {
	    	try {
	    		int j = Integer.parseInt(k);
	    		if (!(j < 1 || j > 7) && !days.contains(Archivio.GIORNISETTIMANA[j-1])) {
		    		giorniPrenotabili.put(Archivio.GIORNISETTIMANA[j-1]);
		    		days += Archivio.GIORNISETTIMANA[j-1] + ",";
		    	}
	    	}
	    	catch (NumberFormatException e) {
	    		return false;
	    	}
	    }
	    if (intersectOtherEventSamePlace (dataInizio, dataFine, oraInizio, durataVisita, days, d.getJSONAmbitoTerritoriale().getJSONObject(Archivio.LUOGHI).getJSONObject(luogo))) return false;
	    String[] m = volontariVal.split(SPLIT_REGEX_LISTA);
	    JSONArray volontari = new JSONArray();
	    for (String k : m) {
    		JSONObject volontario = d.getJSONUsers().getJSONObject(k);
    		JSONArray tipi = volontario.getJSONArray(Archivio.TIPO_VISITA);
    		if (volontarioAlreadyLinkedForThatDay(dataInizio, dataFine, oraInizio, durataVisita, days, tipi)) return false;
    		tipi.put(tipoVisita); 
    		volontari.put(k);
	    }
		return d.aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, giorniPrenotabili, volontari);
	}
	
	public boolean intersectOtherEventSamePlace (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONObject luogo) {
		JSONArray tipiLuogo = luogo.getJSONArray("tipo-visita");
		for (Object k : tipiLuogo) { //tipiVisita del luogo
			JSONObject tipo = d.getJSONTipiVisite().getJSONObject((String)k); 
			if (Time.comesBefore(dateStart1, tipo.getString(Archivio.DATA_FINE)) && !Time.comesBefore(dateFinish1, tipo.getString(Archivio.DATA_INIZIO))) {
				JSONArray days2 = tipo.getJSONArray(Archivio.GIORNI_PRENOTABILI); //giorni del tipo già esistente
				for (Object d : days2) {
					if (days1.contains((String)d)) return true; 
				}
			}
		}
		return false;
	}
	
	public boolean checkIfUserExists (String volontario) {
		return d.checkIfUserExists(volontario);
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		return d.checkIfVisitTypeExists(tipo);
	}
	
	public void impostaAmbitoTerritoriale (String s, String username) {
		if (d.isPrimaConfigurazione() && getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) d.impostaAmbitoTerritoriale(s);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String usernameChiEsegue, String username, String password) {
		if (getTipoUtente(usernameChiEsegue) == CostantiStruttura.CONFIGURATORE) return d.impostaCredenzialiNuovoConfiguratore(username, password);
		else return false;
	}
	
	public boolean modificaMaxPrenotazione (String username, int max) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) return d.impostaMaxPrenotazione(max);
		else return false;
	}
	
	public String getListaUser(String username, int tipo_user) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			String result = "";
			JSONObject jsonUsers = d.getJSONUsers();
			for (String s : JSONUtility.allObjectsSameIntValue(jsonUsers, tipo_user, Archivio.TIPO_USER)) {
				JSONObject user = (JSONObject) jsonUsers.get(s);
				switch (tipo_user) {
				case CostantiStruttura.CONFIGURATORE:
					result += "Username: " + user.get(Archivio.USERNAME) + "\n";
					break;
				case CostantiStruttura.VOLONTARIO:
					result += "Username: " + user.get(Archivio.USERNAME) + "\n" +
								"Tipi visite: " + user.get(Archivio.TIPO_VISITA) + "\n";
					break;
				case CostantiStruttura.FRUITORE:
					result += "Username: " + user.get(Archivio.USERNAME) + "\n";
				}
			}
			return result;
		}
		else return "";
	}
	
	public String getElencoLuoghiVisitabili (String username) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			String result = "";
			JSONObject jsonAmbitoTerritoriale = d.getJSONAmbitoTerritoriale();
			try {
				JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(Archivio.LUOGHI);
				for (String k : luoghi.toMap().keySet()) {
					JSONObject j = luoghi.getJSONObject(k);
					if (!j.get(Archivio.TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
						result += "Luogo: " + j.get(Archivio.NAME) + "\n";
					}
				}
				return result;
			}
			catch (Exception e) {
				return (e.getMessage());
			}
		}
		else return "";
	}
	
	public String getElencoTipiVisiteLuogo (String username) {
		if (getTipoUtente(username) == CostantiStruttura.CONFIGURATORE) {
			String result = "";
			JSONObject jsonAmbitoTerritoriale = d.getJSONAmbitoTerritoriale();
			JSONObject jsonTipiVisite = d.getJSONTipiVisite();
			try {
				JSONObject luoghi = jsonAmbitoTerritoriale.getJSONObject(Archivio.LUOGHI);
				for (String nomeLuogo : luoghi.toMap().keySet()) {
					JSONObject infoLuogo = luoghi.getJSONObject(nomeLuogo);
					if (!infoLuogo.get(Archivio.TIPO_VISITA).equals("[]")) { //[] indica array vuoto tipivisite, quindi no visite
						JSONArray tipiVisite = infoLuogo.getJSONArray(Archivio.TIPO_VISITA);
						result += "Luogo: " + infoLuogo.get(Archivio.NAME) + ", tipi visite associati: ";
						for (int i = 0 ; i < tipiVisite.length() ; i++) 
						{
							result += (jsonTipiVisite.getJSONObject(tipiVisite.get(i).toString())).get(Archivio.TITOLO) + ",";
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
		else return "";
	}
	
}
