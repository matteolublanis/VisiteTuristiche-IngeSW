package archivio.model;

import java.util.List;

import archivio.UserInfoManager;
import dto.DataDisponibilitaDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import dto.UserDTO;
import dto.VisitaDTO;
import utility.Credenziali;

public class Utente implements UserInfoManager {

	@Override
	public int getTipoUtente(String username) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTipoLinkato(String connectionCode) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean associaVolontariATipoVisitaEsistente(String connectionCode, List<Credenziali> volontari,
			String tipoVisita) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String connectionCode, String volontario,
			String tipoVisita) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean inserisciDisponibilita(String connectionCode, String data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<DataDisponibilitaDTO> getDatePerDisponibilita(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TipoVisitaDTO> getElencoTipiVisiteVolontario(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String inserisciPrenotazione(String connectionCode, PrenotazioneDTO prenotazione) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean rimuoviVolontario(String volontario, String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<UserDTO> getListaUser(String connectionCode, int tipo_user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean rimuoviPrenotazione(String connectionCode, String codicePrenotazione) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<VisitaDTO> visiteConfermateVolontario(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(String connectionCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canAddOrRemove(String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(String date) {
		// TODO Auto-generated method stub
		return null;
	}

}
