package archivio.repository;

import java.util.List;

import dto.LuogoDTO;
import dto.TipoVisitaDTO;

public interface AmbitoRepository {
	public boolean checkPrimoAvvio ();
	//Precondizione: CallerType = CostantiStruttura.CONFIGURATORE

	public List<TipoVisitaDTO> getElencoTipiVisite ();
	
	//Precondizione: callerType = CostantiStruttura.CONFIGURATORE
	public List<LuogoDTO> getElencoTipiVisiteLuogo ();
	public List<LuogoDTO> getElencoLuoghiVisitabili ();
	public List<String> getGiorniPrenotabiliTipoVisita(TipoVisitaDTO tipoVisita);
	
	//Precondizione: max > 0
	public boolean impostaMaxPrenotazione(int max);
	//Precondizione: callerType = CostantiStruttura.CONFIGURATORE
	public boolean isPrimaConfigurazione();
	public void impostaAmbitoTerritoriale(String nomeAmbito);
	public boolean checkIfVisitTypeExists (String tipo);
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public boolean aggiungiTipoVisite (TipoVisitaDTO tipoVisita); //TODO logica di Model, va in archivio
	public boolean checkIfPlaceExists (String luogo);
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public boolean aggiungiLuogo (LuogoDTO luogo);
	public void setPrimaConfigurazione();

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
	
	public void setPrimoAvvio ();
}
