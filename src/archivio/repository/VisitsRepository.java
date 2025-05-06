package archivio.repository;

import java.util.List;

import dto.VisitaDTO;

public interface VisitsRepository {
	public void removeVisiteEffettuateCancellate (); //controllo del DBMS?
	public void setVisiteCancellateConfermate (); //controllo del DBMS?

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate ();

	public boolean indicaDatePrecluse (String date);
	/*
	 * Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	 * Postcondizione: prodotte visite del mese i+1
	 */
	public boolean isUltimaPubblicazioneMeseScorso();
	public boolean setPrimaPubblicazione();
	public int getUltimoMesePubblicazione(); //TODO rendere data unica
	public boolean isUltimoPianoPubblicato();
	public int getUltimoAnnoPubblicazione();
	public boolean pubblicaPiano(); 
	//Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	public boolean chiudiRaccoltaDisponibilita();
	//Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	public boolean apriRaccoltaDisponibilita();
	
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public void setPossibilitaDareDisponibilitaVolontari(boolean b); //Semplifica gli altri due
	
	public boolean getPossibileDareDisponibilita();
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public boolean isPrimaPubblicazione ();
}	
