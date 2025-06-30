package client.controller_utente;

import java.util.List;
import archivio.AppManager;
import archivio.ArchivioFactory;
import archivio.UserInfoManager;
import client.app.App;
import client.log_events.AppEvent;
import dto.DataDisponibilitaDTO;
import dto.VisitaDTO;
import utility.MethodName;

public class HandlerVolontario extends ControllerUtente {
	//Precondizioni di tutti i metodi: param != null
	
	private AppManager appPlan;
	private UserInfoManager userInfo;
	
	public HandlerVolontario(App a, String connectionCode, int tipoApp) {
		super(tipoApp);
		this.a = a;
		a.setGu(this);
		this.connectionCode = connectionCode;
		userInfo = ArchivioFactory.createUserInfoManager(tipoApp);
		appPlan = ArchivioFactory.createAppManager(tipoApp);
	}
	
	@MethodName("Visualizza i tipi di visita a cui sei collegato")
 	public void visualizzaTipiVisita() {
		a.viewListTipoVisitaDTO(userInfo.getElencoTipiVisiteVolontario(connectionCode));
 	}
	
	private void visualListVisitDTO (List<VisitaDTO> visite) {
		if (visite.size() != 0) {
			a.viewListVisitaDTO(visite);
		}
		else a.catchEvent(AppEvent.NO_CONFIRMED_VISIT);
	}
	
	@MethodName("Visualizza le visite confermate che gestirai")
	public void vediVisiteConfermate () {
		List<VisitaDTO> visite = userInfo.visiteConfermateVolontario(connectionCode);
		visualListVisitDTO(visite);
	}
	//Postcondizione: disponibilità in Archivio
	@MethodName("Comunica le tue prossime disponibilità")
 	public void comunicaDisponibilita() {
 		if (appPlan.getPossibilitaDareDisponibilita()) { //se posso dare disponibilità	
 			List<DataDisponibilitaDTO> dateDisponibilita = userInfo.getDatePerDisponibilita(connectionCode); //prendi disponibilità possibili
 			if (dateDisponibilita == null) { //TODO se null significa che il volontario dovrebbe essere eliminato
 				a.catchEvent(AppEvent.NO_AVAILABILITY);
 			}
 			else { //se ho disponibilità
 				visualDateDisponibilita(dateDisponibilita);
 				do {
 					String data = a.richiediDataValida("data in cui dai disponibilità"); //inserisco data
 					if (userInfo.inserisciDisponibilita(connectionCode, data)) {
 						a.catchEvent(AppEvent.INSERTED_DISPONIBILITY);
 					}
 					else {
 						a.catchEvent(AppEvent.NOT_INSERTED_DISPONIBILITY); 
 					}
 				} while (a.chiediSioNo("Vuoi aggiungere altre disponibilità?"));
 				
 			}
 			
 		}
 		else a.catchEvent(AppEvent.CANT_ADD_DISPONIBILITY);
 	}
	
	private void visualDateDisponibilita(List<DataDisponibilitaDTO> dateDisponibilita) {
		a.viewListDataDisponibilitaDTO(dateDisponibilita);
	}
		
}
