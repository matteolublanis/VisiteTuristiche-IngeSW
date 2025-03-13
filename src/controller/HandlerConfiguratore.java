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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkCredenzialiCorrette(Credenziali c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cambiaCredenziali(Credenziali c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUsername(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String comunicaAzioniDisponibili() {
		// TODO Auto-generated method stub
		return null;
	}
}
