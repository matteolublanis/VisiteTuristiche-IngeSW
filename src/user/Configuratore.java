package user;

import controller.ControllerUtente;
import utility.CostantiStruttura;

public class Configuratore extends Utente {
	
	public Configuratore (String username, ControllerUtente gu) {
		this.username = username;
		this.tipo = CostantiStruttura.CONFIGURATORE;
		this.gu = gu;
	}
	
	public void impostaAmbitoTerritoriale() {
		gu.impostaAmbitoTerritoriale("Inserisci nome ambito terrioriale:", CostantiStruttura.STRING);
	}
	
	public void modificaMaxPrenotazione () {
		gu.modificaMaxPrenotazione("Inserisci numero max prenotazioni per fruitore:", CostantiStruttura.INT);
	}
	
	public void indicaDatePrecluse () { //TODO completare
		String data_preclusa = "";
		do {
			data_preclusa = (String) gu.richiediVal("Inserisci data preclusa (gg-mm-aaaa):", CostantiStruttura.STRING);
		} while (false); // !Time.isValidDate(data) && Time.isDateNextTwoMonths(date)
		//System.out.println(data_preclusa);
	}
	
	public void pubblicaPianoVisite () {
		if ((boolean)gu.richiediVal("Confermi di voler pubblicare il piano visite?", CostantiStruttura.BOOLEAN)) gu.pubblicaPiano();
	}

	public void visualElencoVolontari () {
		gu.visualListaUser(CostantiStruttura.VOLONTARIO);
	}
	
	public void getElencoLuoghiVisitabili () {
		gu.getElencoLuoghiVisitabili();
	}
	
	public void getElencoTipiVisiteLuogo () {
		gu.getElencoTipiVisiteLuogo();
	}
	
	public void visualStatusVisite () {
		//TODO implementare logica visite, necessario questione tempo e ritocco archivio
	}
	
	public void aggiungiTipoVisite() {
		
	}
	
	public void definisciAmbitoTerritoriale() {
		
	}

}
