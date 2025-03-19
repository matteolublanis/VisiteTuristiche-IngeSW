package controller;

import archivio.Archivio;

public class ControllerArchivio {

	private Archivio d;
	
	public ControllerArchivio (Archivio d) {
		this.d = d; 
	}

	public boolean checkPrimoAvvio () {
		return d.checkPrimoAvvio();
	}
	
	public int getTipoUtente (String username) {
		return d.getTipoUtente(username);
	}

	
	public boolean checkPrimaConfigurazioneArchivio (String username) {
		return d.checkPrimaConfigurazioneArchivio(username);
	}
	
	public String getCredenzialiIniziali () {
		if (checkPrimoAvvio()) {
			d.setPrimoAvvio();
			return new String(d.getCredenzialiConfIniziale()); //TODO: problema logico, la Stringa dovrebbe essere del DB
		}
		else return ""; 
	}
	
	public boolean checkCredenzialiCorrette (Credenziali c) {
		return d.checkCredenzialiCorrette(c);
	}
	
	public boolean checkPrimoAccesso (String username) {
		return (d.checkPrimoAccesso(username) == true); //se true è il primo accesso
	}
	
	public boolean indicaDatePrecluse (String date) {
		return d.indicaDatePrecluse(date);
	}
	
	public boolean cambiaCredenziali (String username, Credenziali c) {
		if (d.modificaCredenziali(username, c)) {
			d.primoAccessoEseguito(c.getUsername());	
			return true;
		}
		
			
		return false;
	}
	
	public void setPrimaConfigurazione() {
		d.setPrimaConfigurazione();
	}
	
	public void impostaAmbitoTerritoriale (String s) {
		d.impostaAmbitoTerritoriale(s);
	}
	
	public boolean impostaCredenzialiNuovoConfiguratore(String username, String password) {
		return d.impostaCredenzialiNuovoConfiguratore(username, password);
	}
	
	public boolean modificaMaxPrenotazione (int max) {
		return d.impostaMaxPrenotazione(max);
	}
	
	public String getListaUser(int tipo_user) {
		return d.getElencoUser(tipo_user);
	}
	
	public String getElencoLuoghiVisitabili () {
		return d.getElencoLuoghiVisitabili();
	}
	
	public String getElencoTipiVisiteLuogo () {
		return d.getElencoTipiVisiteLuogo();
	}
	
}
