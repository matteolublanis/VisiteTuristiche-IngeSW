package controller;

import utility.MethodName;
import utility.ParamName;

public class HandlerConfiguratore extends ControllerUtente{	
	
	public HandlerConfiguratore (ControllerArchivio gdb, String username) {
		super(gdb, username);
	}
	
	@MethodName("Swag")
	public void primaConfigurazione () {

	}
	
	@MethodName("Imposta l'ambito territoriale")
	public void impostaAmbitoTerritoriale(@ParamName("Nome del ambito territoriale")String s) {
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public void modificaMaxPrenotazione(@ParamName("N max per prenotazione di un fruitore")int maxPrenotazione) {
		gdb.modificaMaxPrenotazione(maxPrenotazione);
	}
	
	@MethodName("Visualizza lista user.")
	public String getListaUser(int tipo_user) {
		 return gdb.getListaUser(tipo_user);
	}
	
	@MethodName("Visualizza elenco luoghi visitabili.")
	public String getElencoLuoghiVisitabili() {
		return gdb.getElencoLuoghiVisitabili();
	}
	
	@MethodName("Visualizza elenco tipi visite per luogo.")
	public String getElencoTipiVisiteLuogo() {
		return gdb.getElencoTipiVisiteLuogo();
	}
	
	@MethodName("Non lo so.")
	public void pubblicaPianoVisite() {
		
	}
	
	@MethodName("Indica date precluse del prossimo piano a quello successivo a questo.")
	public void indicaDatePrecluse() {
		
	}
	
	@MethodName("Aggiungi tipo visite.")
	public void aggiungiTipoVisite() {
		
	}
}
