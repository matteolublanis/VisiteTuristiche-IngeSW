package controller;

public class HandlerConfiguratore implements ControllerUtente{	
	
	private ControllerArchivio gdb;
	private String username;
	
	public HandlerConfiguratore (ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
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

	@Override
	public boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(username);
	}

	@Override
	public boolean checkCredenzialiCorrette(Credenziali c) {
		return gdb.checkCredenzialiCorrette(c);
	}

	@Override
	public boolean cambiaCredenziali(Credenziali c) {
		if (gdb.cambiaCredenziali(username, c)) return true;
		return false;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String comunicaAzioniDisponibili() {
		// TODO Auto-generated method stub
		return null;
	}
}
