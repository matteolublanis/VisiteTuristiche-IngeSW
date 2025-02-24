package user;

class Configuratore extends Utente {
	
	private boolean primoAccesso = false;
	
	public Configuratore (String username) {
		this.username = username;
	}
	
	public void modificaMaxPrenotazione () {
		
	}
	
	public void indicaDatePrecluse () {
		
	}
	
	public void pubblicaPianoVisite () {
		
	}
	/*
	 * 
	 * Questi metodi devono essere riutilizzati, possono ritornare la stringa già formattata
	 * TODO: assicurarsi che sia riutilizzabile in futuro
	 * 
	 * 
	public void visualElencoVolontari () {
		
	}
	
	//TODO: oggetto luogo deve avere tipi visita associati
	public void visualElencoVisitaAssociatiLuogo () {
		
	}
	
	public void visualElencoLuoghiVisitabili () {
		
	}
	
	public void visualVisite () {
	
	}
	*/
}
