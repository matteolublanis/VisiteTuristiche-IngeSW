package archivio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dto.PrenotazioneDTO;
import dto.UserDTO;
import dto.VisitaDTO;
import utility.Credenziali;

public interface ArchivioFacade {
	public String makeConnection(Credenziali c);
	
	public boolean checkPrimoAvvio ();
	
	public int getTipoUtente (String username);
	
	public boolean pubblicaPiano(String connectionCode);
	
	public boolean chiudiRaccoltaDisponibilita (String connectionCode);
	
	public boolean apriRaccoltaDisponibilita(String connectionCode);
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String connectionCode, String volontario, String tipoVisita);
	
	public boolean getPossibilitaDareDisponibilita();
	
    public boolean inserisciDisponibilita(String connectionCode, String data);
	
	public Map<String, List<String>> getDatePerDisponibilita(String connectionCode);

	public Set<String> getElencoTipiVisite (String connectionCode);

 	public List<String> getElencoTipiVisiteVolontario (String connectionCode);
	
	public boolean impostaCredenzialiNuovoVolontario (String connectionCode, String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario);
	
	public boolean rimuoviLuogo (String luogo, String connectionCode);
	
	public boolean rimuoviVolontario (String volontario, String connectionCode);
	
	public boolean rimuoviTipo (String tipo, String connectionCode);
	
	public boolean canAddOrRemove(String connectionCode);
	
	public boolean checkPrimaConfigurazioneArchivio (String connectionCode);
	
	public Credenziali getCredenzialiIniziali ();
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b);
	
	public boolean isReleaseOrLaterDay(String connectionCode);
	
	public boolean isPrimaPubblicazione ();
	public boolean checkCredenzialiCorrette (Credenziali c);
	
	public boolean checkPrimoAccesso (String connectionCode);
	
	public boolean indicaDatePrecluse (String connectionCode, String date);
	
	public boolean rimuoviPrenotazione(String connectionCode, String codicePrenotazione);
	
	public List<VisitaDTO> visiteConfermateVolontario (String connectionCode);
	
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String connectionCode);
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore();
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String connectionCode);

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (String connectionCode);
	
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date);
	
	public String inserisciPrenotazione (String connectionCode, PrenotazioneDTO prenotazione);
	
	public boolean cambiaCredenziali (String connectionCode, Credenziali c);
	
	public boolean checkIfPlaceExists (String luogo);
	//Da refattorizzare
	public boolean aggiungiLuogo (String connectionCode, String tag, String nome, String descrizione, String collocazione, Set<String> tipiVisitaVal);

	public void setPrimaConfigurazione();
	
	//TODO assolutamente da refattorizzare
	public boolean aggiungiTipoVisite (String connectionCode, String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal);
	
	public boolean checkIfUserExists (String username);
	
	public boolean createNewFruitore(Credenziali c);
	
	public boolean checkIfVisitTypeExists (String tipo);
	
	public void impostaAmbitoTerritoriale (String s, String connectionCode);
	
	public boolean impostaCredenzialiNuovoConfiguratore(String connectionCode, String username, String password);
	
	public boolean modificaMaxPrenotazione (String connectionCode, int max);

	public Set<UserDTO> getListaUser(String connectionCode, int tipo_user);
	
	public List<String> getElencoLuoghiVisitabili (String connectionCode);
	
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String connectionCode);
}