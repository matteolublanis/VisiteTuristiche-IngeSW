package controller;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class HandlerConfiguratore extends ControllerUtente{	
	
	public HandlerConfiguratore (ControllerArchivio gdb, String username) {
		super(gdb, username);
	}
	
	public void primaConfigurazione () {

	}
	
	public void impostaAmbitoTerritoriale(String msg, int tipo) {
		String s = (String) "";
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	public void modificaMaxPrenotazione(String msg, int tipo) {
		int maxPrenotazione = 0;
		gdb.modificaMaxPrenotazione(maxPrenotazione);
	}
	
	public String visualListaUser(int tipo_user) {
		 return gdb.getListaUser(tipo_user);
	}
	
	public String getElencoLuoghiVisitabili() {
		return gdb.getElencoLuoghiVisitabili();
	}
	
	public String getElencoTipiVisiteLuogo() {
		return gdb.getElencoTipiVisiteLuogo();
	}
	
	public void pubblicaPianoVisite() {
		
	}
	
	public void indicaDatePrecluse() {
		
	}
	
	public void aggiungiTipoVisite() {
		
	}

}
