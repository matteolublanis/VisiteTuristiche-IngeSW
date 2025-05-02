package client.controller_utente;

import java.util.List;
import java.util.Map;

import archivio.ArchivioFacade;
import client.app.App;
import client.log_events.AppEvent;
import dto.DTO;
import dto.VisitaDTO;
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
 				a.view("I tipi di visita a te associati non richiedono nuove disponibilità o c'è un problema con l'archivio, contatta un configuratore.");
 			}
 			else { //se ho disponibilità
 				visualDateDisponibilita(dateDisponibilita);
 				indicaDisponibilita();
 				
 			}
 			
 		}
 		else a.view("Non puoi al momento comunicare le tue disponibilità.");
 	}
	
	private void visualDateDisponibilita(List<DTO> dateDisponibilita) {
		a.viewListDTO(dateDisponibilita);
	}
	
	private void indicaDisponibilita () {
			a.view("Indica le tue disponibilità.");
			String data = "";
			boolean b = true;
			do { //DA ESTRARRE!
				data = a.richiediDataValida("data in cui dai disponibilità (dd-mm-yyyy)"); //inserisco data
				b = archivio.inserisciDisponibilita(connectionCode, data); //controllo se inserita
				if (b) { //se inserita chiedo se vuole continuare
					a.view("La tua disponibilità è stata inserita.");
					b = a.chiediSioNo("Vuoi aggiungere altre disponibilità?");
				}
				else { //se non inserita richiedo
					a.view("La tua disponibilità non è stata inserita, assicurati che sia una data corretta.");
					b = a.chiediSioNo("Vuoi continuare ad aggiungere?"); //do possibilità di chiudere loop
				}
			} while (b); //TODO se viene inserito un formato errato chiude
	}
	
}
