package controller;

import archivio.Archivio;

public class ControllerArchivio {

	private Archivio d;
	
	public ControllerArchivio () {
		this.d = new Archivio(); //imposta database col quale interagire
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
	
	public int effettuaLogin (Credenziali c) {
		return d.getTipoUtente(c.getUsername()); 
	}
	
	public boolean checkPrimoAccesso (String username) {
		return (d.checkPrimoAccesso(username) == true); //se true è il primo accesso
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
	
	public void modificaMaxPrenotazione (int max) {
		d.impostaMaxPrenotazione(max);
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
