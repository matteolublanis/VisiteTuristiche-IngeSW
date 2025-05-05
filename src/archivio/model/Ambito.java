package archivio.model;

import java.util.List;

import archivio.AmbitoManager;
import dto.LuogoDTO;
import dto.TipoVisitaDTO;
import dto.VisitaDTO;

public class Ambito implements AmbitoManager {
	
	@Override
	public List<TipoVisitaDTO> getElencoTipiVisite(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean rimuoviLuogo(String luogo, String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean rimuoviTipo(String tipo, String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkPrimaConfigurazioneArchivio(String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean indicaDatePrecluse(String connectionCode, String date) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkIfPlaceExists(String luogo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean aggiungiLuogo(String connectionCode, LuogoDTO luogo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPrimaConfigurazione() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean aggiungiTipoVisite(TipoVisitaDTO tipoVisita, String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkIfVisitTypeExists(String tipo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void impostaAmbitoTerritoriale(String s, String connectionCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean modificaMaxPrenotazione(String connectionCode, int max) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<LuogoDTO> getElencoLuoghiVisitabili(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<LuogoDTO> getElencoTipiVisiteLuogo(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}


}
