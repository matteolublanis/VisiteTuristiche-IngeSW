package archivio.repository;

import java.util.List;

import dto.VisitaDTO;

public interface VisitsRepository {
	public void removeVisiteEffettuateCancellate (); //controllo del DBMS?
	public void setVisiteCancellateConfermate (); //controllo del DBMS?

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate ();

	public boolean indicaDatePrecluse (String date);
	public boolean tryPubblicaPiano();
	/*
	 * Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	 * Postcondizione: prodotte visite del mese i+1
	 */
	public boolean pubblicaPiano(); //?????
	//Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	public boolean tryChiudiRaccoltaDisponibilita ();
	public boolean chiudiRaccoltaDisponibilita();
	//Precondizione: CallerType = CostantiStruttura.CONFIGURATORE
	public boolean tryApriRaccoltaDisponibilita();
	public boolean apriRaccoltaDisponibilita();
	
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public void setPossibilitaDareDisponibilitaVolontari(boolean b); //Semplifica gli altri due
	
	public boolean getPossibileDareDisponibilita();
	public boolean isReleaseOrLaterDay();
	//Precondizione: callerTyper = CostantiStruttura.CONFIGURATORE
	public boolean isPrimaPubblicazione ();
}	
