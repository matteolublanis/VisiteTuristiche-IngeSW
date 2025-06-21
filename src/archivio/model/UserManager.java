package archivio.model;

import java.util.List;

import archivio.CredenzialiManager;
import archivio.UserInfoManager;
import archivio.repository.AmbitoRepository;
import archivio.repository.UserRepository;
import archivio.repository.VisitsRepository;
import dto.DataDisponibilitaDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import dto.UserDTO;
import dto.VisitaDTO;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.Time;

public class UserManager implements UserInfoManager {
	
	private CredenzialiManager accessoManager;
	private UserRepository userRep;
	private AmbitoRepository ambitoRep;
	private VisitsRepository visitRep;
	
	public UserManager(CredenzialiManager accessoManager, UserRepository userRep, AmbitoRepository ambitoRep,
			VisitsRepository visitRep) {
		super();
		this.accessoManager = accessoManager;
		this.userRep = userRep;
		this.ambitoRep = ambitoRep;
		this.visitRep = visitRep;
	}
	
	private String getUsernameClient(String connectionCode) {
		return accessoManager.getLinkedUsername(connectionCode);
	}
	
	@Override
	public int getTipoUtente (String username) { 
		return userRep.getTipoUtente(username);
	}

	@Override
	public boolean checkIfUsernameExists (String username) {
		return userRep.checkIfUserExists(username);
	}

	@Override
	public boolean associaVolontariATipoVisitaEsistente(String connectionCode, List<Credenziali> volontari,
			String tipoVisita) {
		if (canAddOrRemove(connectionCode)) 
			return userRep.associaVolontariATipoVisita(volontari, tipoVisita);
		else return false;
	}	

	@Override
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String connectionCode, String volontario, String tipoVisita) { //OK
		if ( getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE
				&& checkIfUsernameExists(volontario) && ambitoRep.checkIfVisitTypeExists(tipoVisita)) {
			if (userRep.checkIfCanLinkVolontario(volontario, tipoVisita)) {
				return userRep.associaVolontarioEsistenteATipoVisitaEsistente(volontario, tipoVisita);
			}
			else {
				return false;
			}
		}
		else return false;
	}

	@Override
    public boolean inserisciDisponibilita(String connectionCode, String data) { //OK
   	 if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.VOLONTARIO) 
   		 return userRep.inserisciDisponibilita(data, getUsernameClient(connectionCode));
   	 else return false;
	}

	@Override
	public List<DataDisponibilitaDTO> getDatePerDisponibilita(String connectionCode) {	 //OK
		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.VOLONTARIO) 
			return userRep.getDatePerDisponibilita(getUsernameClient(connectionCode));
		else return null;
	}

	@Override
 	public List<TipoVisitaDTO> getElencoTipiVisiteVolontario (String connectionCode) {
 		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.VOLONTARIO) 
 			return userRep.getElencoTipiVisiteVolontario(getUsernameClient(connectionCode));
 		else return null;
 	}

	@Override
	public String inserisciPrenotazione (String connectionCode, PrenotazioneDTO prenotazione) {
		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.FRUITORE) 
			return userRep.inserisciPrenotazione(getUsernameClient(connectionCode), prenotazione);
		else return null;
	}

	@Override
	public boolean rimuoviVolontario (String volontario, String connectionCode) {
		if (userRep.getTipoUtente(volontario) == CostantiStruttura.VOLONTARIO 
				&& canAddOrRemove(connectionCode)) 
			return userRep.rimuoviVolontario(volontario);
		else return false;
	}

	@Override
	public List<UserDTO> getListaUser(String connectionCode, int tipo_user) {
		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE)
			return userRep.getListaUser(tipo_user);
		else return null;
	}

	@Override
	public boolean rimuoviPrenotazione(String connectionCode, String codicePrenotazione) {
		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.FRUITORE) {
			PrenotazioneDTO prenotazione = userRep.getPrenotazione(codicePrenotazione);
			if (prenotazione != null) {
				if (Time.isThreeDaysOrLessBefore(Time.getActualDate(), 
						prenotazione.getGiorno())) return false;
				else if (userRep.linkedUserToPrenotazione(codicePrenotazione)
						.equals(getUsernameClient(connectionCode))) {
					return userRep.rimuoviPrenotazione(getUsernameClient(connectionCode),
							codicePrenotazione);
				}
				else return false;
			}
			else return false;
		}
		else return false;
	}

	@Override
	public List<VisitaDTO> visiteConfermateVolontario (String connectionCode) {
		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.VOLONTARIO) 
			return userRep.visiteConfermateVolontario(getUsernameClient(connectionCode));
		else return null;
	}

	@Override
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String connectionCode) {
		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.FRUITORE)
			return userRep.getElencoPrenotazioniFruitore(getUsernameClient(connectionCode));
		else return null;
	}

	@Override
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore() { 
		return userRep.getElencoVisiteProposteConfermateCancellateFruitore();
	}

	@Override
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String connectionCode) {
		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.FRUITORE)
			return userRep.getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore(getUsernameClient(connectionCode));
		else return null;
	}

	@Override
	public boolean canAddOrRemove(String connectionCode) {
		if (getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
			return canAddOrRemove();
		}
		else return false;
	}

	@Override
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date) {
		return userRep.getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato(date);
	}
	
	private boolean canAddOrRemove() {
		if (visitRep.isPrimaPubblicazione()) return true;
		else return (visitRep.isUltimoPianoPubblicato() && !visitRep.getPossibileDareDisponibilita()); 
	}

}
