package archivio.repository;

import java.util.List;
import org.json.JSONArray;

import dto.DTO;
import dto.LuogoDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import utility.Credenziali;

public interface Archivio {
	public boolean checkPrimoAvvio ();
	public int getTipoUtente (String username);
	//Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	public boolean tryPubblicaPiano();
	/*
	 * Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	 * Postcondizione: prodotte visite del mese i+1
	 */
	public boolean pubblicaPiano();
	//Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	public boolean tryChiudiRaccoltaDisponibilita ();
	public boolean chiudiRaccoltaDisponibilita();
	//Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	public boolean tryApriRaccoltaDisponibilita();
	public boolean apriRaccoltaDisponibilita();
	public boolean checkIfCanLinkVolontario(String volontario, String tipoVisita);
	
	//Precondizione: callerType = CostantiStruttura.CONFIGURATORE
	public boolean associaVolontarioEsistenteATipoVisitaEsistente(String volontario, String tipoVisita);
	public boolean associaVolontariATipoVisita(List<Credenziali> volontari, String tipoVisita);
	public boolean getPossibileDareDisponibilita();
	//Precondizione: callerTyper = CostantiStruttura.VOLONTARIO
    public boolean inserisciDisponibilita(String data, String username);
	//Precondizione: callerType = CostantiStruttura.VOLONTARIO
    public List<DTO> getDatePerDisponibilita(String username);
	public List<DTO> getElencoTipiVisite ();
 	public List<DTO> getElencoTipiVisiteVolontario (String username);
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public boolean tryImpostaCredenzialiNuovoVolontario (String username, String password, List<String> tipi_visiteVal, boolean tipiVisitaNecessario);
	
	/*
	 * Precondizione: callerType = CostantiStruttura.CONFIGURATORE
	 * Postcondizione: new Volontario in Archivio
	 */
	public boolean impostaCredenzialiNuovoVolontario (String username, String password, JSONArray tipi_visite, boolean tipiVisitaNecessari);
	//Precondizione: callerType = CostantiStruttura.CONFIGURATORE
	public List<DTO> getElencoTipiVisiteLuogo ();
	public List<DTO> getElencoLuoghiVisitabili ();
	/*
	 * Precondizione: 0 < tipo_user < 4
	 */
	public List<DTO> getListaUser(int tipo_user);
	//Precondizione: max > 0
	public boolean impostaMaxPrenotazione(int max);
	//Precondizione: callerType = CostantiStruttura.CONFIGURATORE
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password);
	public boolean isPrimaConfigurazione();
	public void impostaAmbitoTerritoriale(String nomeAmbito);
	public boolean checkIfUserExists (String username);
	public boolean impostaCredenzialiNuovoFruitore(String username, String password);
	public boolean checkIfVisitTypeExists (String tipo);
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public boolean tryAggiungiVisite (TipoVisitaDTO tipoVisita);
	public boolean checkIfPlaceExists (String luogo);
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public boolean aggiungiLuogo (LuogoDTO luogo);
	public void setPrimaConfigurazione();
	public boolean modificaCredenziali (String username, Credenziali c);
	public boolean primoAccessoEseguito (String user);
	
	/*
	 * Precondizione: callerType = CostantiStruttura.VOLONARIO
	 * Postcondizione: remove Prenotazione
	 */
	public boolean rimuoviPrenotazione(String username, String codicePrenotazione);
	//Precondizione: callerType = CostantiStruttura.VOLONTARIO
	public List<DTO> visiteConfermateVolontario (String username);
	//Precondizione: callerType = CostantiStruttura.FRUITORE
	public List<DTO> getElencoPrenotazioniFruitore (String username);
	public List<DTO> getElencoVisiteProposteConfermateCancellateFruitore();
	//Precondizione: callerType = CostantiStruttura.FRUITORE
	public List<DTO> getElencoVisiteProposteConfermateCancellatePrenotateDalFruitore (String username);
	public List<DTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate ();
	public List<DTO> getElencoVisiteProposteConfermateCancellateFruitoreGiornoDato (String date);
	
	/*
	 * Precondizione: callerType = CostantiStruttura.FRUITORE
	 * Postcondizione: new Prenotazione
	 */
	public String inserisciPrenotazione (String username, PrenotazioneDTO prenotazione);
	public boolean indicaDatePrecluse (String date);
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public void setPossibilitaDareDisponibilitaVolontari(boolean b);
	public boolean isReleaseOrLaterDay();
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public boolean isPrimaPubblicazione ();
	public boolean checkCredenzialiCorrette (Credenziali c);
	public boolean checkPrimoAccesso (String username);
	/*
	 * Precondizione: tipoVisita exists && associazioni esistenti
	 * Postcondizione: remove tipoVisita && remove associazioni
	 */
	public boolean rimuoviTipo (String tipo);
	/*
	 * Precondizione: luogo in Archivio && associazioni esistenti
	 * Postcondizione: remove Luogo && remove associazioniLuogo
	 */
	public boolean rimuoviLuogo (String luogo);
	
	/*
	 * Precondizione: callerType = CostantiStruttura.VOLONTARIO && tipiVisitaAssociati esistenti
	 * Postcondizione: remove Volontario && remove associazioni
	 */
	public boolean rimuoviVolontario (String volontario);
	//Precondizione: isPrimoAvvio
	public Credenziali getCredenzialiConfIniziale();
	public void setPrimoAvvio ();
	public boolean canAddOrRemove();
	public void removeVisiteEffettuateCancellate (); //controllo del DBMS?
	public void setVisiteCancellateConfermate (); //controllo del DBMS?
}
