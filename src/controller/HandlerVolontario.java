package controller;

import main.App;
import utility.MethodName;

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
	
}
