package archivio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import controller.ControllerUtente;
import dto.PrenotazioneDTO;
import dto.UserDTO;
import dto.VisitaDTO;
import utility.Credenziali;

public interface ArchivioFacade {
	public boolean addControllerUtente(ControllerUtente gu, String username);

	public boolean checkPrimoAvvio ();
	
	public int getTipoUtente (String username);
	
	public boolean pubblicaPiano(ControllerUtente gu);
	
	public boolean chiudiRaccoltaDisponibilita (ControllerUtente gu);
	
	public boolean apriRaccoltaDisponibilita(ControllerUtente gu);
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(ControllerUtente gu, String volontario, String tipoVisita);
	
	public boolean getPossibilitaDareDisponibilita();
	
    public boolean inserisciDisponibilita(ControllerUtente gu, String data, String username);
	
	public Map<String, List<String>> getDatePerDisponibilita(ControllerUtente gu);

	public Set<String> getElencoTipiVisite (ControllerUtente gu);

 	public List<String> getElencoTipiVisiteVolontario (ControllerUtente gu);
	
	public boolean impostaCredenzialiNuovoVolontario (ControllerUtente gu, String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario);
	
	public boolean rimuoviLuogo (String luogo, ControllerUtente gu);
	
	public boolean rimuoviVolontario (String volontario, ControllerUtente gu);
	
	public boolean rimuoviTipo (String tipo, ControllerUtente gu);
	
	public boolean canAddOrRemove(String username);
	
	public boolean checkPrimaConfigurazioneArchivio (ControllerUtente gu);
	
	public Credenziali getCredenzialiIniziali ();
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b);
	
	public boolean isReleaseOrLaterDay(ControllerUtente gu);
	
	public boolean isPrimaPubblicazione ();
	public boolean checkCredenzialiCorrette (Credenziali c);
	
	public boolean checkPrimoAccesso (ControllerUtente gu);
	
	public boolean indicaDatePrecluse (ControllerUtente gu, String date);
	
	public boolean rimuoviPrenotazione(ControllerUtente gu, String codicePrenotazione);
	
	public List<VisitaDTO> visiteConfermateVolontario (ControllerUtente gu);
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (ControllerUtente gu);
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore();
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (ControllerUtente gu);

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (ControllerUtente gu);
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date);
	
	public String inserisciPrenotazione (ControllerUtente gu, PrenotazioneDTO prenotazione);
	
	public boolean cambiaCredenziali (ControllerUtente gu, Credenziali c);
	
	public boolean checkIfPlaceExists (String luogo);
	//Da refattorizzare
	public boolean aggiungiLuogo (ControllerUtente gu, String tag, String nome, String descrizione, String collocazione, Set<String> tipiVisitaVal);

	public void setPrimaConfigurazione();
	
	//TODO assolutamente da refattorizzare
	public boolean aggiungiTipoVisite (ControllerUtente gu, String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal);
	
	public boolean checkIfUserExists (String username);
	
	public boolean createNewFruitore(Credenziali c);
	
	public boolean checkIfVisitTypeExists (String tipo);
	
	public void impostaAmbitoTerritoriale (String s, ControllerUtente gu);
	
	public boolean impostaCredenzialiNuovoConfiguratore(ControllerUtente gu, String username, String password);
	
	public boolean modificaMaxPrenotazione (ControllerUtente gu, int max);

	public Set<UserDTO> getListaUser(ControllerUtente gu, int tipo_user);
	
	public List<String> getElencoLuoghiVisitabili (ControllerUtente gu);
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (ControllerUtente gu);
}