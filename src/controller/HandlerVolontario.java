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
 		a.view(gdb.getElencoTipiVisiteVolontario(username));
 	}
	
	@MethodName("Comunica le tue prossime disponibilità")
 	public void comunicaDisponibilita(App a) {
 		if (gdb.getPossibilitaDareDisponibilita()) {
 			
 			HashMap<String, List<String>> dateDisponibilita = gdb.getDatePerDisponibilita(username);
 			if (dateDisponibilita == null) {
 				a.view("I tipi di visita a te associati non richiedono nuove disponibilità o c'è un problema con l'archivio, contatta un configuratore.");
 			}
 			else {
 				for (String k : dateDisponibilita.keySet()) {
 					a.view("Giorni tipo " + k + ": ");
 					String days = "";
 					for (String i : dateDisponibilita.get(k)) {
 						days += i + " ";
 					}
 					a.view(days);
 				}
 				a.view("Indica le tue disponibilità.");
 				String data = "";
 				boolean validDate = true;
 				boolean b = true;
 				do {
 					data = a.richiediVal(CostantiStruttura.STRING, "data in cui dai disponibilità");
 					validDate = Time.isValidDate(data);
 					if (!validDate) a.view("Il formato inserito non è corretto.");
 					else {
 						b = gdb.inserisciDisponibilita(data, username);
 						if (b) {
 							a.view("La tua disponibilità è stata inserita.");
 							b = chiediSioNo(a, "Vuoi aggiungere altre disponibilità?");
 						}
 						else {
 							a.view("La tua disponibilità non è stata inserita, assicurati che sia una data corretta.");
 							b = true;
 						}
 					}
 				} while (validDate && b);
 				
 			}
 			
 		}
 		else a.view("Non puoi al momento comunicare le tue disponibilità.");
 	}
	
}
