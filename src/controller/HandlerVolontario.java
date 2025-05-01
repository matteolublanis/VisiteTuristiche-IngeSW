package controller;

import java.util.List;
import java.util.Map;

import archivio.ArchivioFacade;
import dto.VisitaDTO;
import main.App;
import utility.MethodName;

public class HandlerVolontario extends ControllerUtente {
	//Precondizioni di tutti i metodi: param != null
	
	public HandlerVolontario(ArchivioFacade gdb, String username, App a) {
		this.archivio = gdb;
		this.username = username;
	}
	
	@MethodName("Visualizza i tipi di visita a cui sei collegato")
 	public void visualizzaTipiVisita(App a) {
		a.visualListGeneric(archivio.getElencoTipiVisiteVolontario(this), "Tipi visita associati");
 	}
	
	private void visualListVisitDTO (List<VisitaDTO> visite, App a) {
		if (visite.size() != 0) {
			a.visualListGeneric(visite, "Elenco visite");
		}
		else a.view("Nessuna visita confermata.");
	}
	
	@MethodName("Visualizza le visite confermate che gestirai")
	public void vediVisiteConfermate (App a) {
		List<VisitaDTO> visite = archivio.visiteConfermateVolontario(this);
		visualListVisitDTO(visite, a);
	}
	//Postcondizione: disponibilità in Archivio
	@MethodName("Comunica le tue prossime disponibilità")
 	public void comunicaDisponibilita(App a) {
 		if (archivio.getPossibilitaDareDisponibilita()) { //se posso dare disponibilità	
 			Map<String, List<String>> dateDisponibilita = archivio.getDatePerDisponibilita(this); //prendi disponibilità possibili
 			if (dateDisponibilita == null) { //TODO se null significa che il volontario dovrebbe essere eliminato
 				a.view("I tipi di visita a te associati non richiedono nuove disponibilità o c'è un problema con l'archivio, contatta un configuratore.");
 			}
 			else { //se ho disponibilità
 				visualDateDisponibilita(a, dateDisponibilita);
 				indicaDisponibilita(a);
 				
 			}
 			
 		}
 		else a.view("Non puoi al momento comunicare le tue disponibilità.");
 	}
	
	private void visualDateDisponibilita(App a, Map<String, List<String>> dateDisponibilita) {
		for (String k : dateDisponibilita.keySet()) {
				a.visualListGeneric(dateDisponibilita.get(k), k.equals("Date precluse") ? k : "Giorni tipo " + k);
			}
	}
	
	private void indicaDisponibilita (App a) {
			a.view("Indica le tue disponibilità.");
			String data = "";
			boolean b = true;
			do { //DA ESTRARRE!
				data = a.richiediDataValida("data in cui dai disponibilità (dd-mm-yyyy)"); //inserisco data
				b = archivio.inserisciDisponibilita(this, data, username); //controllo se inserita
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
