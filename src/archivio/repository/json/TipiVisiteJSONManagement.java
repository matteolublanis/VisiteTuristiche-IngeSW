package archivio.repository.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import dto.DataDisponibilitaDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import dto.VisitaDTO;
import utility.Credenziali;
import utility.JSONUtility;
import utility.Time;

public class TipiVisiteJSONManagement {
	private static final String PATH_TIPI_VISITE = "json/tipo_visite.json";
	private JSONObject jsonTipiVisite = null;
	private static final String VOLONTARI2 = "volontari";
	private static final String LUOGO = "luogo";
	private static final String[] GIORNISETTIMANA = new String[] {"lun","mar","mer","gio","ven","sab","dom"};
	private static final String MAX_FRUITORE = "max-fruitore", 
			MIN_FRUITORE = "min-fruitore";
	private static final String DA_ACQUISTARE = "da-acquistare",
			DURATA_VISITA = "durata-visita", 
			ORA_INIZIO = "ora-inizio", 
			GIORNI_PRENOTABILI = "giorni-prenotabili", 
			DATA_FINE = "data-fine", 
			DATA_INIZIO = "data-inizio",
			PUNTO_INCONTRO = "punto-incontro",
			TITOLO = "titolo",
			DESCRIPTION = "descrizione";
	
	public TipiVisiteJSONManagement () {
		if (JSONUtility.readJsonFile(PATH_TIPI_VISITE) == null) {
			JSONUtility.aggiornaJsonFile(new JSONObject(), PATH_TIPI_VISITE, 10);
			jsonTipiVisite = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
		}
		else jsonTipiVisite = JSONUtility.readJsonFile(PATH_TIPI_VISITE);
	}
	
	public JSONObject getJSONTipiVisite() {
		return jsonTipiVisite;
	}
	
	public JSONObject getTipoVisitaJSONObject (String tipo) {
		return jsonTipiVisite.getJSONObject(tipo);
	}
	
	public JSONArray getVolontariAssociatiTipoJSONArray (String tipo) {
		return getTipoVisitaJSONObject(tipo).getJSONArray(VOLONTARI2);
	}
	
	public String getLuogoAssociatoTipo (String tipo) {
		return getTipoVisitaJSONObject(tipo).getString(LUOGO);
	}
	
	public void aggiornaJsonTipiVisite () {
		JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
	}
	
	public int rimuoviVolontarioDaTipo (String tipo, String volontario) {
		JSONArray volontariTipo = getVolontariAssociatiTipoJSONArray(tipo);
		for (int i = 0 ; i < volontariTipo.length() ; i++) { //cicla sui volontari per rimuovere quello che voglio rimuovere
			if (volontariTipo.get(i).equals(volontario)) {
				volontariTipo.remove(i); 
				break;
			}
		}
		aggiornaJsonTipiVisite();
		return (volontariTipo.length());
	}
	//Questi due metodi VisitaDTO sono rischiosi per lo StatoVisita, non dovrebbe funzionare così
	public VisitaDTO visitaDTOFruitore (String tag, String date, String statoVisita) {
		JSONObject tipoVisita = getTipoVisitaJSONObject(tag);
        VisitaDTO visitaDTO = new VisitaDTO(
        	tag,
        	getTitoloVisita(tag),
            tipoVisita.getString(DESCRIPTION),
            tipoVisita.getString(PUNTO_INCONTRO),
            date,
            tipoVisita.getString(ORA_INIZIO),
            tipoVisita.getBoolean(DA_ACQUISTARE),
            statoVisita
        );
        return visitaDTO;
	}
	
	public VisitaDTO visitaDTOVolontario (String tag, String k, String statoVisita, List<PrenotazioneDTO> prenotazioni) {
		JSONObject tipoVisita = getTipoVisitaJSONObject(tag);			                
        VisitaDTO visitaDTO = new VisitaDTO(
        	tag,
            tipoVisita.getString(TITOLO),
            tipoVisita.getString(DESCRIPTION),
            tipoVisita.getString(PUNTO_INCONTRO),
            k,
            tipoVisita.getString(ORA_INIZIO),
            tipoVisita.getBoolean(DA_ACQUISTARE),
            statoVisita,
            prenotazioni
        );
        return visitaDTO;
	}
	
	public boolean otherVisitTypesIntersect (String tipoVisita, JSONArray tipi) {
		JSONObject tipo = jsonTipiVisite.getJSONObject(tipoVisita);
		if (visitTypeIntersectsOtherVisitTypes(tipo.getString(DATA_INIZIO), tipo.getString(DATA_FINE), tipo.getString(ORA_INIZIO), tipo.getInt(DURATA_VISITA), tipo.getJSONArray(GIORNI_PRENOTABILI).toString(), tipi)) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean visitTypeIntersectsOtherVisitTypes (String dateStart1, String dateFinish1, String hour1, int duration1, String days1, JSONArray tipiVisitaVolontario) {
		for (Object k : tipiVisitaVolontario) {  //OK
			JSONObject tipo = jsonTipiVisite.getJSONObject((String)k); //prende ogni tipo dal json dei tipi
			if (Time.comesBefore(dateStart1, tipo.getString(DATA_FINE)) && !Time.comesBefore(dateFinish1, tipo.getString(DATA_INIZIO))) { //controlla se periodi intersecano
				JSONArray days2 = tipo.getJSONArray(GIORNI_PRENOTABILI); //prende giorni prenotabili del tipo
				for (Object d : days2) { 
					if (days1.contains((String)d)) return true; //se i giorni intersecano allora volontario linkato per quei giorni
				}
			}
		}
		return false;
	}
	
	public void aggiungiVolontarioATipo (String tipoVisita, String volontario) {
		for (Object o : getVolontariAssociatiTipoJSONArray(tipoVisita)) 
			if (((String)o).equals(volontario)) return;
		getVolontariAssociatiTipoJSONArray(tipoVisita).put(volontario);
		JSONUtility.aggiornaJsonFile(jsonTipiVisite, PATH_TIPI_VISITE, 10);
	}
	
	public void inserisciNuovoVolontarioAssociatoAVisite (JSONArray tipi_visite, String username) {
	    for (Object k : tipi_visite) {
    		JSONArray vol = getVolontariAssociatiTipoJSONArray((String)k);
    		vol.put(username);
	    }
	    aggiornaJsonTipiVisite();
	}
	
	public Map<String, Integer> getTipiVisitaMinFruitori () {
		Map<String, Integer> result = new HashMap<>();
		for (String tag : jsonTipiVisite.keySet()) result.put(tag, getMinFruitoreVisita(tag));
		return result;
	}
	
	public Map<String, String> getTipiVisitaLuoghi () {
		Map<String, String> result = new HashMap<>();
		for (String tag : jsonTipiVisite.keySet()) result.put(tag, getLuogoAssociatoTipo(tag));
		return result;
	}
	
	public Map<String, String> getTipiVisitaTitoli () {
		Map<String, String> result = new HashMap<>();
		for (String tag : jsonTipiVisite.keySet()) result.put(tag, getTitoloVisita(tag));
		return result;
	}
	
	public String getTitoloVisita (String tipo) {
		return jsonTipiVisite.getJSONObject(tipo).getString(TITOLO);
	}
	
	public int getMaxFruitoreVisita (String tipo) {
		return jsonTipiVisite.getJSONObject(tipo).getInt(MAX_FRUITORE);
	}
	
	public int getMinFruitoreVisita (String tipo) {
		return jsonTipiVisite.getJSONObject(tipo).getInt(MIN_FRUITORE);
	}
	
	public boolean checkIfVisitTypeHasNoVolunteer (String tipo) {
		return jsonTipiVisite.getJSONObject(tipo).getJSONArray(VOLONTARI2).length() == 0;
	}
	
	public void rimuoviTipo (String tipo) {
		jsonTipiVisite.remove(tipo);
		aggiornaJsonTipiVisite();
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
	
	public List<TipoVisitaDTO> getElencoTipiVisite () {
		List<TipoVisitaDTO> result = new ArrayList<>();
		for (String key : jsonTipiVisite.keySet()) {
			result.add(new TipoVisitaDTO(key, jsonTipiVisite.getJSONObject(key).getString(TITOLO)));
		}
		return result;
	}
	
	public List<DataDisponibilitaDTO> getDatePerDisponibilitaFromTipiVisite(String username, JSONArray tipiVisite) {	 //OK
		List<DataDisponibilitaDTO> result = new ArrayList<>();
			for (Object s : tipiVisite) { 
				JSONObject tipo = getTipoVisitaJSONObject((String)s);
				try {
					String[] periodoDaDareDisponibilita = Time.getAvailabilityWindow(tipo.getString(DATA_INIZIO), tipo.getString(DATA_FINE), Time.getDesideredMonthAndYear(ArchivioJSON.RELEASE_DAY, Time.getActualDate()));
					JSONArray giorni = tipo.getJSONArray(GIORNI_PRENOTABILI);
					
					List<String> days = new ArrayList<>();
					for (Object g : giorni) {
						List<String> k = Time.getAllDatesSameDayOfTheWeek(periodoDaDareDisponibilita[0], periodoDaDareDisponibilita[1], Arrays.asList(GIORNISETTIMANA).indexOf((String) g) + 1);
						for (String date : k) {
							days.add(date);
						}
					}
					result.add(new DataDisponibilitaDTO((String)s, days));
				}	
				catch (Exception e) {
					return null;
				}
			}
			return result;
	}
	
	public JSONArray returnGiorniPrenotabili (List<Integer> giorniPrenotabiliVal) {
		JSONArray giorniPrenotabili = new JSONArray();
	    for (Integer k : giorniPrenotabiliVal) {
	    	try {
	    		int j = (k);
	    		if (!(j < 1 || j > 7) && !giorniPrenotabili.toString().contains(GIORNISETTIMANA[j-1])) {
		    		giorniPrenotabili.put(GIORNISETTIMANA[j-1]);
		    	}
	    	}
	    	catch (NumberFormatException e) {
	    		return null;
	    	}
	    }
	    return giorniPrenotabili;
	}
	
	public boolean intersectVisitTypeSamePlace (JSONArray tipiLuogo, String dateStart1, String dateFinish1, String hour1, int duration1, String days1) {
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
	
	public void aggiungiTipoVisite(TipoVisitaDTO tipoVisita) {
		JSONArray giorniPrenotabili = returnGiorniPrenotabili(tipoVisita.getGiorniPrenotabiliVal());
		JSONArray volontari = new JSONArray();
	    for (Credenziali k : tipoVisita.getVolontariVal()) { volontari.put(k.getUsername()); }
	    JSONObject nuovoTipoVisita = setNewVisitType(tipoVisita.getLuogo(), tipoVisita.getTitolo(), tipoVisita.getDescrizione(), 
	    		tipoVisita.getPuntoIncontro(), tipoVisita.getDataInizio(), tipoVisita.getDataFine(), giorniPrenotabili, 
	    		tipoVisita.getOraInizio(), tipoVisita.getDurataVisita()
	    		, tipoVisita.isDaAcquistare(), tipoVisita.getMinFruitore(), tipoVisita.getMaxFruitore(), volontari);
	    jsonTipiVisite.put(tipoVisita.getTag(), nuovoTipoVisita);
	    aggiornaJsonTipiVisite();
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
	
	public JSONArray getGiorniPrenotabiliJSONArray (JSONObject tipo) {
		return tipo.getJSONArray(GIORNI_PRENOTABILI);
	}
	
}
