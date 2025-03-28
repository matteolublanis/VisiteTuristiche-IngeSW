package controller;

import java.util.HashMap;
import java.util.List;

import main.App;
import utility.CostantiStruttura;
import utility.MethodName;
import utility.Time;

public class HandlerVolontario extends ControllerUtente {
	
	public HandlerVolontario(ControllerArchivio gdb, String username, App a) {
		this.gdb = gdb;
		this.username = username;
		if (checkPrimoAccesso()) primoAccesso(a);
	}
	
	@MethodName("Visualizza i tipi di visita a cui sei collegato")
 	public void visualizzaTipiVisita(App a) {
		List<String> listaTipi = gdb.getElencoTipiVisiteVolontario(username);
		for (String tipo : listaTipi) {
			a.view(tipo);
		}
 	}
	
	@MethodName("Comunica le tue prossime disponibilità")
 	public void comunicaDisponibilita(App a) {
 		if (gdb.getPossibilitaDareDisponibilita()) { //se posso dare disponibilità	
 			HashMap<String, List<String>> dateDisponibilita = gdb.getDatePerDisponibilita(username); //prendi disponibilità possibili
 			if (dateDisponibilita == null) { //TODO se null significa che il volontario dovrebbe essere eliminato
 				a.view("I tipi di visita a te associati non richiedono nuove disponibilità o c'è un problema con l'archivio, contatta un configuratore.");
 			}
 			else { //se ho disponibilità
 				for (String k : dateDisponibilita.keySet()) {
 					a.view("Giorni tipo " + k + ": "); //visualizzo le disponibilità
 					String days = "";
 					for (String i : dateDisponibilita.get(k)) {
 						days += i + " ";
 					}
 					a.view(days);
 				}
 				a.view("Indica le tue disponibilità.");
 				String data = "";
 				boolean b = true;
 				do { //le inserisco
 					data = a.richiediVal(CostantiStruttura.STRING, "data in cui dai disponibilità"); //inserisco data
 					if (!Time.isValidDate(data)) a.view("Il formato inserito non è corretto."); //se non valida si rifa
 					else { //se valida
 						b = gdb.inserisciDisponibilita(data, username); //controllo se inserita
 						if (b) { //se inserita chiedo se vuole continuare
 							a.view("La tua disponibilità è stata inserita.");
 							b = a.chiediSioNo("Vuoi aggiungere altre disponibilità?");
 						}
 						else { //se non inserita richiedo
 							a.view("La tua disponibilità non è stata inserita, assicurati che sia una data corretta.");
 							b = a.chiediSioNo("Vuoi continuare ad aggiungere?"); //do possibilità di chiudere loop
 						}
 					}
 				} while (b); //TODO se viene inserito un formato errato chiude
 				
 			}
 			
 		}
 		else a.view("Non puoi al momento comunicare le tue disponibilità.");
 	}
	
}
