package archivio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import dto.PrenotazioneDTO;
import dto.UserDTO;
import dto.VisitaDTO;
import utility.Credenziali;

public interface Archivio {
	public boolean checkPrimoAvvio ();
	public int getTipoUtente (String username);
	public boolean tryPubblicaPiano(String username);
	public boolean pubblicaPiano();
	public boolean tryChiudiRaccoltaDisponibilita (String username);
	public boolean chiudiRaccoltaDisponibilita();
	public boolean tryApriRaccoltaDisponibilita(String username);
	public boolean apriRaccoltaDisponibilita();
	public boolean checkIfCanLinkVolontario(String volontario, String tipoVisita);
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String volontario, String tipoVisita);
	public boolean getPossibileDareDisponibilita();
    public boolean inserisciDisponibilita(String data, String username);
	public Map<String, List<String>> getDatePerDisponibilita(String username);
	public Set<String> getElencoTipiVisite ();
 	public List<String> getElencoTipiVisiteVolontario (String username);
	public boolean tryImpostaCredenzialiNuovoVolontario (String username, String password, Set<String> tipi_visiteVal, boolean tipiVisitaNecessario);
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, JSONArray tipi_visite, boolean tipiVisitaNecessari);
	public Map<String, List<String>> getElencoTipiVisiteLuogo (String username);
	public List<String> getElencoLuoghiVisitabili (String username);
	public Set<UserDTO> getListaUser(String username, int tipo_user);
	public boolean impostaMaxPrenotazione(int max);
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password);
	public boolean isPrimaConfigurazione();
	public void impostaAmbitoTerritoriale(String nomeAmbito);
	public boolean checkIfUserExists (String username);
	public boolean impostaCredenzialiNuovoFruitore(String username, String password);
	public boolean checkIfVisitTypeExists (String tipo);
	public boolean tryAggiungiVisite (String luogo, String tipoVisita, String titolo, String descrizione, String puntoIncontro, 
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal);
	public boolean checkIfPlaceExists (String luogo);
	public boolean aggiungiLuogo (String tag, String nome, String collocazione, Set<String> tipiVisitaVal);
	public void setPrimaConfigurazione();
	public boolean modificaCredenziali (String username, Credenziali c);
	public boolean primoAccessoEseguito (String user);
	public boolean rimuoviPrenotazione(String username, String codicePrenotazione);
	public List<VisitaDTO> visiteConfermateVolontario (String username);
	public List<PrenotazioneDTO> getElencoPrenotazioniFruitore (String username);
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitore();
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String username);
	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate ();
	public List<VisitaDTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date);
	public String inserisciPrenotazione (String username, PrenotazioneDTO prenotazione);
	public boolean indicaDatePrecluse (String date);
	public void setPossibilitaDareDisponibilitaVolontari(boolean b);
	public boolean isReleaseOrLaterDay();
	public boolean isPrimaPubblicazione ();
	public boolean checkCredenzialiCorrette (Credenziali c);
	public boolean checkPrimoAccesso (String username);
	public boolean rimuoviTipo (String tipo);
	public boolean rimuoviLuogo (String luogo);
	public boolean rimuoviVolontario (String volontario);
	public Credenziali getCredenzialiConfIniziale();
	public void setPrimoAvvio ();
	public boolean canAddOrRemove();
	public void removeVisiteEffettuateCancellate (); //controllo del DBMS?
	public void setVisiteCancellateConfermate (); //controllo del DBMS?
}
