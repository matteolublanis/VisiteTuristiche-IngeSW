package archivio;

import java.util.List;

import dto.DataDisponibilitaDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import dto.UserDTO;
import dto.VisitaDTO;
import utility.Credenziali;

public interface UserInfoManager {
	
	public int getTipoUtente (String username);
		
	public boolean checkIfUsernameExists (String username);
	
	public boolean associaVolontariATipoVisitaEsistente(String connectionCode, List<Credenziali> volontari, String tipoVisita);
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String connectionCode, String volontario, String tipoVisita);

	public boolean inserisciDisponibilita(String connectionCode, String data);
	
	public List<DataDisponibilitaDTO> getDatePerDisponibilita(String connectionCode);
	
	public List<TipoVisitaDTO> getElencoTipiVisiteVolontario (String connectionCode);
	
	public String inserisciPrenotazione (String connectionCode, PrenotazioneDTO prenotazione);
		
	public boolean rimuoviVolontario (String volontario, String connectionCode);
	
	public List<UserDTO> getListaUser(String connectionCode, int tipo_user);
	
	public boolean rimuoviPrenotazione(String connectionCode, String codicePrenotazione);
	
	public List<VisitaDTO> visiteConfermateVolontario (String connectionCode);
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String connectionCode);
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore();
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String connectionCode);
	
	public boolean canAddOrRemove(String connectionCode); 
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date);


}
