package controller;

import main.App;
import utility.MethodName;

public class HandlerFruitore extends ControllerUtente {

	public HandlerFruitore(ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
	}
	
	@MethodName("Visualizza visite proposte, confermate e cancellate")
	public void getElencoVisiteProposteConfermateCancellate (App a) {
		a.view(gdb.getElencoVisiteProposteConfermateCancellateFruitore());
	}
	
}
