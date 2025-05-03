package client.controller_utente;

import java.util.List;
import archivio.ArchivioFacade;
import client.app.App;
import client.log_events.AppEvent;
import dto.DTO;
import utility.MethodName;

public class HandlerVolontario extends ControllerUtente {
	//Precondizioni di tutti i metodi: param != null
	
	public HandlerVolontario(ArchivioFacade gdb, App a, String connectionCode) {
		this.archivio = gdb;
		this.a = a;
		this.connectionCode = connectionCode;
	}
	
	@MethodName("Visualizza i tipi di visita a cui sei collegato")
 	public void visualizzaTipiVisita() {
		a.viewListDTO(archivio.getElencoTipiVisiteVolontario(connectionCode));
 	}
	
	private void visualListVisitDTO (List<DTO> visite) {
		if (visite.size() != 0) {
			a.viewListDTO(visite);
		}
		else a.catchEvent(AppEvent.NO_CONFIRMED_VISIT);
	}
	
	@MethodName("Visualizza le visite confermate che gestirai")
	public void vediVisiteConfermate () {
		List<DTO> visite = archivio.visiteConfermateVolontario(connectionCode);
		visualListVisitDTO(visite);
	}
	//Postcondizione: disponibilità in Archivio
	@MethodName("Comunica le tue prossime disponibilità")
 	public void comunicaDisponibilita() {
 		if (archivio.getPossibilitaDareDisponibilita()) { //se posso dare disponibilità	
 			List<DTO> dateDisponibilita = archivio.getDatePerDisponibilita(connectionCode); //prendi disponibilità possibili
 			if (dateDisponibilita == null) { //TODO se null significa che il volontario dovrebbe essere eliminato
 				a.catchEvent(AppEvent.NO_AVAILABILITY);
 			}
 			else { //se ho disponibilità
 				visualDateDisponibilita(dateDisponibilita);
 				do {
 					String data = a.richiediDataValida("data in cui dai disponibilità"); //inserisco data
 					if (archivio.inserisciDisponibilita(connectionCode, data)) {
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
	
	private void visualDateDisponibilita(List<DTO> dateDisponibilita) {
		a.viewListDTO(dateDisponibilita);
	}
		
}
