package controller;

public class ControllerUtente {
	
	private String user; 
	private ControllerArchivio gdb;	

	public ControllerUtente (ControllerArchivio gdb, String user) {
		this.gdb = gdb;
		this.user = user;
	}
	
	public void primaConfigurazione () {

	}
	
	public void impostaAmbitoTerritoriale(String msg, int tipo) {
		String s = (String) richiediVal(msg, tipo);
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	public void modificaMaxPrenotazione(String msg, int tipo) {
		int maxPrenotazione = (int) richiediVal(msg, tipo);
		gdb.modificaMaxPrenotazione(maxPrenotazione);
	}
	
	public Object richiediVal(String msg, int tipo) {
		return null;
	}
	
	public boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(user);
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		return gdb.checkCredenzialiCorrette(c); 
	}
	
	public boolean cambiaCredenziali (Credenziali c) { 
		return (gdb.cambiaCredenziali(user, c));
	} 
	
	public void setUsername(String username) {
		user = username;;
	}
	
	public String getUsername() {
		return user;
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
	
	public void pubblicaPiano() {
		
	}
	
	public void aggiungiTipoVisite() {
		
	}

}
