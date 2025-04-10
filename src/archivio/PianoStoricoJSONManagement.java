package archivio;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dto.VisitaDTO;
import utility.JSONUtility;

public class PianoStoricoJSONManagement {
	
	private static final String PATH_STORICO = "json/visite_effettuate_storico.json";
	private JSONObject jsonPianoStorico = null;
	
	
	public PianoStoricoJSONManagement () {
		if (JSONUtility.readJsonFile(PATH_STORICO) == null) {
			JSONUtility.aggiornaJsonFile(new JSONObject(), PATH_STORICO, 10);
			jsonPianoStorico = JSONUtility.readJsonFile(PATH_STORICO);
		}
		else {
			jsonPianoStorico = JSONUtility.readJsonFile(PATH_STORICO);
		}
	}
	
	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {	
	    List<VisitaDTO> visiteList = new ArrayList<>();
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
	
	public void inserisciVisitaNelloStorico (String luogo, String tipo, String date) {
		JSONObject visitaStorica = new JSONObject();
		visitaStorica.put(tipo, luogo);
		if (jsonPianoStorico.has(date)) jsonPianoStorico.getJSONObject(date).put(tipo, luogo);
		else jsonPianoStorico.put(date, visitaStorica);
		aggiornaJSONStorico();
	}
	
	public void aggiornaJSONStorico () {
		JSONUtility.aggiornaJsonFile(jsonPianoStorico, PATH_STORICO, 10);

	}
	
}
