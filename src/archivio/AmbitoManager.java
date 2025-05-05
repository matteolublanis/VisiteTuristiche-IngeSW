package archivio;

import java.util.List;

import dto.LuogoDTO;
import dto.TipoVisitaDTO;
import dto.VisitaDTO;

public interface AmbitoManager {

	public List<TipoVisitaDTO> getElencoTipiVisite (String connectionCode);
	
	public boolean rimuoviLuogo (String luogo, String connectionCode);
		
	public boolean rimuoviTipo (String tipo, String connectionCode);
	
	public boolean checkPrimaConfigurazioneArchivio (String connectionCode);
	
	public boolean indicaDatePrecluse (String connectionCode, String date);

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (String connectionCode);
			
	public boolean checkIfPlaceExists (String luogo);

	public boolean aggiungiLuogo (String connectionCode, LuogoDTO luogo);
		
	public void setPrimaConfigurazione(); 
	
	public boolean aggiungiTipoVisite (TipoVisitaDTO tipoVisita, String connectionCode);
	
	public boolean checkIfVisitTypeExists (String tipo);
	
	public void impostaAmbitoTerritoriale (String s, String connectionCode);
		
	public boolean modificaMaxPrenotazione (String connectionCode, int max);
	
	public List<LuogoDTO> getElencoLuoghiVisitabili (String connectionCode);
	
	public List<LuogoDTO> getElencoTipiVisiteLuogo (String connectionCode);
}