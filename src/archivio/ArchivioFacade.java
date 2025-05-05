package archivio;

import java.util.List;
import dto.DTO;
import dto.LuogoDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import utility.Credenziali;

public interface ArchivioFacade {
	public String makeConnection(Credenziali c);
	
	public boolean checkPrimoAvvio ();
	
	public int getTipoUtente (String username);
	
	public int getTipoLinkato(String connectionCode);
	
	public boolean pubblicaPiano(String connectionCode);
	
	public boolean chiudiRaccoltaDisponibilita (String connectionCode);
	
	public boolean apriRaccoltaDisponibilita(String connectionCode);
	
	public boolean associaVolontariATipoVisitaEsistente(String connectionCode, List<Credenziali> volontari, String tipoVisita);
	
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String connectionCode, String volontario, String tipoVisita);
	
	public boolean getPossibilitaDareDisponibilita();
	
    public boolean inserisciDisponibilita(String connectionCode, String data);
	
	public List<DTO> getDatePerDisponibilita(String connectionCode);

	public List<DTO> getElencoTipiVisite (String connectionCode);

 	public List<DTO> getElencoTipiVisiteVolontario (String connectionCode);
	
	public boolean impostaCredenzialiNuovoVolontario (String connectionCode, String username, String password, List<String> tipi_visiteVal, boolean tipiVisitaNecessario);
	
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
	
	public List<DTO> visiteConfermateVolontario (String connectionCode);
	
	public List<DTO> getElencoPrenotazioniFruitore (String connectionCode);
	
	public List<DTO> getElencoVisiteProposteConfermateCancellateFruitore();
	
	public List<DTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String connectionCode);

	public List<DTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (String connectionCode);
	
	public List<DTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date);
	
	public String inserisciPrenotazione (String connectionCode, PrenotazioneDTO prenotazione);
	
	public boolean cambiaCredenziali (String connectionCode, Credenziali c);
	
	public boolean checkIfPlaceExists (String luogo);
	//Da refattorizzare
	public boolean aggiungiLuogo (String connectionCode, LuogoDTO luogo);

	public void setPrimaConfigurazione();
	
	//TODO assolutamente da refattorizzare
	public boolean aggiungiTipoVisite (TipoVisitaDTO tipoVisita, String connectionCode);
	
	public boolean checkIfUserExists (String username);
	
	public boolean createNewFruitore(Credenziali c);
	
	public boolean checkIfVisitTypeExists (String tipo);
	
	public void impostaAmbitoTerritoriale (String s, String connectionCode);
	
	public boolean impostaCredenzialiNuovoConfiguratore(String connectionCode, String username, String password);
	
	public boolean modificaMaxPrenotazione (String connectionCode, int max);

	public List<DTO> getListaUser(String connectionCode, int tipo_user);
	
	public List<DTO> getElencoLuoghiVisitabili (String connectionCode);
	
	public List<DTO> getElencoTipiVisiteLuogo (String connectionCode);
}