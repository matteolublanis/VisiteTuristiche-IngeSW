package controller;

import java.lang.reflect.Method;
import java.util.ArrayList;

import utility.MethodName;
import utility.ParamName;

public class HandlerConfiguratore extends ControllerUtente{	
	
	public HandlerConfiguratore (ControllerArchivio gdb, String username) {
		super(gdb, username);
	}
	
	@MethodName("Imposta l'ambito territoriale")
	public void primaConfigurazione () {

	}
	
	@MethodName("Imposta l'ambito territoriale")
	public void impostaAmbitoTerritoriale(@ParamName("Nome del ambito territoriale")String msg, int tipo) {
		//String s = (String) "";
		//gdb.impostaAmbitoTerritoriale(s);
		System.out.println("Funziona");
	}
	
	@MethodName("Imposta l'ambito territoriale")
	public void modificaMaxPrenotazione(String msg, int tipo) {
		int maxPrenotazione = 0;
		gdb.modificaMaxPrenotazione(maxPrenotazione);
	}
	
	@MethodName("Imposta l'ambito territoriale")
	public String visualListaUser(int tipo_user) {
		 return gdb.getListaUser(tipo_user);
	}
	
	@MethodName("Imposta l'ambito territoriale")
	public String getElencoLuoghiVisitabili() {
		return gdb.getElencoLuoghiVisitabili();
	}
	
	@MethodName("Imposta l'ambito territoriale")
	public String getElencoTipiVisiteLuogo() {
		return gdb.getElencoTipiVisiteLuogo();
	}
	
	@MethodName("Imposta l'ambito territoriale")
	public void pubblicaPianoVisite() {
		
	}
	
	@MethodName("Imposta l'ambito territoriale")
	public void indicaDatePrecluse() {
		
	}
	
	@MethodName("Imposta l'ambito territoriale")
	public void aggiungiTipoVisite() {
		
	}
}
