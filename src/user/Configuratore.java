package user;

import controller.ControllerUtente;
import utility.CostantiStruttura;

public class Configuratore extends Utente {
	
	public Configuratore (String username, int tipo, ControllerUtente gu) {
		this.username = username;
		this.tipo = tipo;
		this.gu = gu;
		actionMethods((i, map, method) -> map.put(i, method)); //mappa metodi
	}
	
	
	public void modificaMaxPrenotazione () {
		int maxPrenotazioneFruitore = (Integer) gu.richiediVal("Inserisci numero max prenotazioni per fruitore:", CostantiStruttura.INT);
		//TODO comunica a GU di voler modificare un valore intero
		//TODO prima di implementare assicurarsi di ridurre al minimo il codice con funzioni atomiche
	}
	
	public void indicaDatePrecluse () { //TODO completare
		String data_preclusa;
		do {
			data_preclusa = (String) gu.richiediVal("Inserisci data preclusa (gg-mm-aaaa):", CostantiStruttura.STRING);
		} while (true); // !Time.isValidDate(data) && Time.isDateNextTwoMonths(date)
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

}
