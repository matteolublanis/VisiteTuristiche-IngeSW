package archivio.repository.json;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import dto.DataDisponibilitaDTO;
import utility.JSONUtility;
import utility.Time;

public class DaPubblicareJSONManagement {
	
	private static final String POSSIBILE_DARE_DISPONIBILITA = "possibile-dare-disponibilita";
	private JSONObject jsonPianoVisiteDaPubblicare = null;
	private static final String PATH_VISITE_DAPUBBLICARE = "json/visite_da_pubblicare.json";
	private static final String ANNO_ULTIMA_PUBBLICAZIONE = "anno-ultima-pubblicazione";
	private static final String MESE_ULTIMA_PUBBLICAZIONE = "mese-ultima-pubblicazione";
	private static final String PRIMA_PUBBLICAZIONE = "prima-pubblicazione";
	private static final String ULTIMO_PIANO = "ultimo-piano-pubblicato";
	private static final String DATE_PRECLUSE = "date-precluse";
	private static final String DATE_PRECLUSE_MESEIPLUS3 = "datePrecluseI+3";
	private static final String DISPONIBILITA = "disponibilita";
	
	public DaPubblicareJSONManagement () {
		if (JSONUtility.readJsonFile(PATH_VISITE_DAPUBBLICARE) == null) {
			creaDaPubblicareJSON();
		}
		else jsonPianoVisiteDaPubblicare = JSONUtility.readJsonFile(PATH_VISITE_DAPUBBLICARE);
	}
	
	private void creaDaPubblicareJSON () {
		JSONObject daPubblicare = new JSONObject();
		daPubblicare.put(ULTIMO_PIANO, false);
		daPubblicare.put(PRIMA_PUBBLICAZIONE, true);
		daPubblicare.put(DISPONIBILITA, new JSONObject());
		daPubblicare.put(DATE_PRECLUSE, new JSONArray());
		daPubblicare.put(DATE_PRECLUSE_MESEIPLUS3, new JSONArray());
		daPubblicare.put(MESE_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.MONTH)); //TODO da controllare
		daPubblicare.put(ANNO_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.YEAR));
		daPubblicare.put(POSSIBILE_DARE_DISPONIBILITA, false);
		JSONUtility.aggiornaJsonFile(daPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
		jsonPianoVisiteDaPubblicare = JSONUtility.readJsonFile(PATH_VISITE_DAPUBBLICARE);
		
	}
	public JSONObject getDisponibilita () {
		return jsonPianoVisiteDaPubblicare.getJSONObject(DISPONIBILITA);
	}
	
	public boolean setPrimaPubblicazione() {
		
		jsonPianoVisiteDaPubblicare.put(PRIMA_PUBBLICAZIONE, false);
		jsonPianoVisiteDaPubblicare.put(ULTIMO_PIANO, true);
		jsonPianoVisiteDaPubblicare.put(MESE_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.MONTH));
		jsonPianoVisiteDaPubblicare.put(ANNO_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.YEAR));

		apriRaccoltaDisponibilita();
		aggiornaJsonDaPubblicare();
		return true;
	}
	
	public boolean isPrimaPubblicazione() {
		return jsonPianoVisiteDaPubblicare.getBoolean(PRIMA_PUBBLICAZIONE);
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		
		jsonPianoVisiteDaPubblicare.put(POSSIBILE_DARE_DISPONIBILITA, b);
		jsonPianoVisiteDaPubblicare.put(ULTIMO_PIANO, b);
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
	}
	
	public boolean apriRaccoltaDisponibilita() {
		jsonPianoVisiteDaPubblicare.put(POSSIBILE_DARE_DISPONIBILITA, true);
		aggiornaJsonDaPubblicare();
		return true;
	}
	
	public int getUltimoMesePubblicazione() {
		return jsonPianoVisiteDaPubblicare.getInt(MESE_ULTIMA_PUBBLICAZIONE);
	}
	
	public List<String> getDatePrecluse () {
		loadJsonDaPubblicare();
		JSONArray datePrecluse = jsonPianoVisiteDaPubblicare.getJSONArray(DATE_PRECLUSE);
		List<String> result = new ArrayList<String>();
		for (Object m : datePrecluse) {
			result.add((String) m);
		}
		return result;
	}
	
	public boolean indicaDatePrecluse (String date) {
		JSONArray datePrecluse = jsonPianoVisiteDaPubblicare.getJSONArray(DATE_PRECLUSE_MESEIPLUS3);
		for (int i = 0; i < datePrecluse.length(); i++) {
			if (datePrecluse.get(i).equals(date)) { 
				return false;
			}
		}
		datePrecluse.put(date);
		aggiornaJsonDaPubblicare();
		return true;
	}
	
	
	public boolean inserisciDisponibilita(String data, String username, List<DataDisponibilitaDTO> m) { //ok\
		List<String> datePrecluse = getDatePrecluse();
		if (datePrecluse.contains(data)) return false; //per precondizione già sistemato
		for (DataDisponibilitaDTO dataDisp : m) {
			if ((dataDisp.getGiorni().contains(data))) {
				JSONObject disponibilita = jsonPianoVisiteDaPubblicare.getJSONObject(DISPONIBILITA);
				if (!disponibilita.has(username)) {
					JSONObject d =  new JSONObject();
					disponibilita.put(username, d);
					JSONObject volontario = disponibilita.getJSONObject(username);
					volontario.put(data, dataDisp.getTag());
					JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
					return true;
				}
				else {
					JSONObject volontario = disponibilita.getJSONObject(username);
					if (volontario.has(data)) return true;
					else {
						volontario.put(data, dataDisp.getTag());
						JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean isUltimoPianoPubblicato () {
		return jsonPianoVisiteDaPubblicare.getBoolean(ULTIMO_PIANO);
	}
	
	public void resetDopoPubblicazione () {
		jsonPianoVisiteDaPubblicare.put(DATE_PRECLUSE, jsonPianoVisiteDaPubblicare.getJSONArray(DATE_PRECLUSE_MESEIPLUS3));
		jsonPianoVisiteDaPubblicare.put(DATE_PRECLUSE_MESEIPLUS3, new JSONArray());
		jsonPianoVisiteDaPubblicare.put(DISPONIBILITA, new JSONObject());
		jsonPianoVisiteDaPubblicare.put(ULTIMO_PIANO, true);
		jsonPianoVisiteDaPubblicare.put(MESE_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.MONTH));
		jsonPianoVisiteDaPubblicare.put(ANNO_ULTIMA_PUBBLICAZIONE, Time.getActualDateValue(Time.YEAR));
		aggiornaJsonDaPubblicare();
	}
	
	public void loadJsonDaPubblicare () {
		jsonPianoVisiteDaPubblicare = JSONUtility.readJsonFile(PATH_VISITE_DAPUBBLICARE);
	}
	
	public void aggiornaJsonDaPubblicare () {
		JSONUtility.aggiornaJsonFile(jsonPianoVisiteDaPubblicare, PATH_VISITE_DAPUBBLICARE, 10);
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
		aggiornaJsonDaPubblicare();
		return true;
	}
	
	public boolean getPossibileDareDisponibilita () {
		return jsonPianoVisiteDaPubblicare.getBoolean(POSSIBILE_DARE_DISPONIBILITA);
	}
}
