package controller;

import main.App;
import utility.CostantiStruttura;
import utility.MethodName;
import utility.Time;


public class HandlerConfiguratore extends ControllerUtente{	
		
	public HandlerConfiguratore () {
		
	}

	public HandlerConfiguratore(ControllerArchivio gdb, String username, App a) {
		this.gdb = gdb;
		this.username = username;
		if (checkPrimoAccesso()) primoAccesso(a);
		if (checkPrimaConfigurazioneArchivio()) configuraArchivio(a);
	}
	
	private boolean checkPrimaConfigurazioneArchivio () {
		return gdb.checkPrimaConfigurazioneArchivio(username);
	}
	
	private void configuraArchivio(App a) {
		String ambito = (String)a.richiediVal(CostantiStruttura.STRING, "nome ambito territoriale");
		impostaAmbitoTerritoriale(ambito);
		modificaMaxPrenotazione(a);
		a.view("Inizio fase creazione luoghi dell'ambito territoriale.");
		aggiungiLuogo(a);
		
	}
	
	private String impostaNuovoVolontarioPerNuovoTipoVisita (App a, String tipo) {
		String username = (String) a.richiediVal(CostantiStruttura.STRING, "username del nuovo volontario");
		String password = (String) a.richiediVal(CostantiStruttura.STRING, "password del nuovo volontario");
		String tipi_visiteVal = tipo;
		if (gdb.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, false)) {
			a.view("Inserito nuovo volontario.");
			return username;
		}
		else {
			a.view("Non è stato inserito il nuovo volontario, username già in uso.");
			return "";
		}
	}
	
	@MethodName("Aggiungi nuovo volontario")
	public void impostaCredenzialiNuovoVolontario (App a) {
		String username = (String) a.richiediVal(CostantiStruttura.STRING, "username del nuovo volontario");
		String password = (String) a.richiediVal(CostantiStruttura.STRING, "password del nuovo volontario");
		String tipi_visiteVal = (String) a.richiediVal(CostantiStruttura.STRING, "tipi delle visite associate al nuovo volontario (tipo1, tipo2,...)");
		if (gdb.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, true)) a.view("Inserito nuovo volontario.");
		else a.view("Non è stato inserito il nuovo volontario, username in uso o non sono stati inseriti tipi di visita esistenti.");	
	}
	
	private void impostaAmbitoTerritoriale(String s) {
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	private boolean impostaMaxPrenotazione(int maxPrenotazione) {
		return (gdb.modificaMaxPrenotazione(maxPrenotazione));
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public void modificaMaxPrenotazione (App a) {
		int max = Integer.parseInt(a.richiediVal(CostantiStruttura.INT, "max prenotazione per fruitore"));
		if (impostaMaxPrenotazione(max)) a.view("Modificato valore max prenotazione.");
		else a.view("Valore max prenotazione non modificato.");
	}
	
	@MethodName("Visualizza lista volontari")
	public void getListaVolontari(App a) {
		 a.view(gdb.getListaUser(CostantiStruttura.VOLONTARIO));
	}
	
	@MethodName("Visualizza elenco luoghi visitabili")
	public void getElencoLuoghiVisitabili(App a) {
		a.view(gdb.getElencoLuoghiVisitabili());
	}
	
	@MethodName("Visualizza elenco tipi visite per luogo")
	public void getElencoTipiVisiteLuogo(App a) {
		a.view(gdb.getElencoTipiVisiteLuogo());
	}
	
	@MethodName("Pubbica il piano delle visite")
	public void pubblicaPianoVisite(App a) {
		
	}
	
	@MethodName("Indica date precluse del prossimo piano a quello successivo a questo")
	public void indicaDatePrecluse(App a) {
		String data = (String)a.richiediVal(CostantiStruttura.STRING, "data preclusa"); 
		if ((gdb.indicaDatePrecluse(data))) a.view("La data preclusa è stata inserita.");
		else a.view("La data preclusa non è stata inserita, assicurarsi che sia nel formato e nel periodo corretto.");
	}
	
	private boolean aggiungiTipoVisitePartendoDaLuogo (App a, String luogo) {
		String tipoVisita = (String)a.richiediVal(CostantiStruttura.STRING, "tag del tipo della visita");
		String titolo = (String)a.richiediVal(CostantiStruttura.STRING, "titolo della visita");
		String descrizione = (String)a.richiediVal(CostantiStruttura.STRING, "descrizione riassuntiva della visita");
		String puntoIncontro = (String)a.richiediVal(CostantiStruttura.STRING, "punto di incontro della visita (locazione geografica)");
		String dataInizio = (String)a.richiediVal(CostantiStruttura.STRING, "apertura del periodo della visita");
		String dataFine = (String)a.richiediVal(CostantiStruttura.STRING, "chiusura del periodo della visita");
		String giorniPrenotabili = (String)a.richiediVal(CostantiStruttura.STRING, "giorni prenotabili della visita (1, 3, 7 indicano lun, mer, dom)");
		String oraInizio = (String)a.richiediVal(CostantiStruttura.STRING, "ora d'inizio visita");
		int durataVisita = Integer.parseInt(a.richiediVal(CostantiStruttura.STRING, "durata della visita in minuti (ad esempio 120 sono 120 minuti, quindi 2 ore)"));
		String ticket = (String)a.richiediVal(CostantiStruttura.STRING, "se è da acquistare o no un biglietto (si/no)");
		int minFruitore =Integer.parseInt(a.richiediVal(CostantiStruttura.STRING, "minimo fruitori per confermare la visita"));
		int maxFruitore = Integer.parseInt(a.richiediVal(CostantiStruttura.STRING, "massimo fruitori per completare la visita"));
		String volontari = "";
		if (chiediSioNo(a, "Vuoi associare un nuovo volontario per questo tipo di visita?")) {
			do {
				volontari = impostaNuovoVolontarioPerNuovoTipoVisita(a, "");
			} while (volontari.equals(""));
		}
		else {
			volontari = (String)a.richiediVal(CostantiStruttura.STRING, "volontari che gestiranno la visita (volontario1, volontario2,...)");
		}
		boolean daAcquistare = false;
		switch (ticket.toLowerCase()) {
		case "si":
			daAcquistare = true;
			break;
		case "no":
			daAcquistare = false;
			return false;
		default:
			a.view("Formato acquistabile errato, non è stata aggiunto il nuovo tipo di visita.");
				return false;
		}
		if (gdb.aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabili, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, volontari)) {
			a.view("Il nuovo tipo di visita è stato aggiunto.");
			return true;
		}
		else {
			a.view("Il nuovo tipo di visita non è stato aggiunto.");
			return false;
		}
	}
	
	@MethodName("Aggiungi tipo visite")
	public boolean aggiungiTipoVisite(App a) {
		String luogo = "";
		do {
			luogo = (String)a.richiediVal(CostantiStruttura.STRING, "luogo della visita");
			if (!gdb.checkIfPlaceExists(luogo)) a.view("Il luogo inserito è inesistente.");
		} while (!gdb.checkIfPlaceExists(luogo));
		
		String tipoVisita = "";
		do {
			tipoVisita = (String)a.richiediVal(CostantiStruttura.STRING, "tag del tipo della visita");
			if (gdb.checkIfVisitTypeExists(tipoVisita)) a.view("Il tag inserito esiste già.");
		} while (gdb.checkIfVisitTypeExists(tipoVisita));
		
		String titolo = (String)a.richiediVal(CostantiStruttura.STRING, "titolo della visita");
		String descrizione = (String)a.richiediVal(CostantiStruttura.STRING, "descrizione riassuntiva della visita");
		String puntoIncontro = (String)a.richiediVal(CostantiStruttura.STRING, "punto di incontro della visita (locazione geografica)");
		String dataInizio = "";
		do {
			dataInizio = (String)a.richiediVal(CostantiStruttura.STRING, "apertura del periodo della visita");
			if (!Time.isValidDate(dataInizio)) a.view("Formato data non valido");
		} while (!Time.isValidDate(dataInizio));
		String dataFine = "";
		do {
			dataFine = (String)a.richiediVal(CostantiStruttura.STRING, "chiusura del periodo della visita");
			if (!Time.isValidDate(dataFine)) a.view("Formato data non valido");
			if (Time.comesBefore(dataFine, dataInizio)) a.view("Non può finire prima che inizi.");
		} while (!Time.isValidDate(dataFine) || Time.comesBefore(dataFine, dataInizio));
		//^\d+(,\d+)*$
		String giorniPrenotabili = "";
		boolean b = true;
		do {
			String giorno = (a.richiediVal(CostantiStruttura.INT, "giorno prenotabile della visita (da 1 a 7, da lun a dom)"));
			if (Integer.parseInt(giorno) < 1 || Integer.parseInt(giorno) > 7) {
				b = true;
				a.view("Numero inserito non valido, deve essere tra 1 e 7.");
			}
			else {
				if (!giorniPrenotabili.contains(giorno)) giorniPrenotabili += giorno + ",";
				b = chiediSioNo(a, "Vuoi aggiungere un altro giorno prenotabile?");
			}
		} while (b);
		String oraInizio = "";
		do {
			oraInizio = (String)a.richiediVal(CostantiStruttura.STRING, "ora d'inizio visita (hh-mm)");
			if (!Time.isValidHour(oraInizio)) a.view("Formato non corretto, inserire tipo 10:30.");
		} while (!Time.isValidHour(oraInizio));
		int durataVisita = Integer.parseInt(a.richiediVal(CostantiStruttura.STRING, "durata della visita in minuti (ad esempio 120 sono 120 minuti, quindi 2 ore)"));
		boolean ticket = chiediSioNo(a, "se è da acquistare o no un biglietto (si/no)");
		int minFruitore = 0;
		do {
			minFruitore =Integer.parseInt(a.richiediVal(CostantiStruttura.STRING, "minimo fruitori per confermare la visita"));
			if (minFruitore <= 0) a.view("Il numero minimo di fruitori non può essere minore o uguale a 0.");
		} while (minFruitore <= 0);
		int maxFruitore = 0;
		do {
			maxFruitore = Integer.parseInt(a.richiediVal(CostantiStruttura.STRING, "massimo fruitori per completare la visita"));
			if (maxFruitore < minFruitore) a.view("Il numero massimo di fruitori non può essere minore del minimo.");
		} while (maxFruitore < minFruitore);
		String volontari = "";
		if (chiediSioNo(a, "Vuoi associare un nuovo volontario per questo tipo di visita?")) {
			do {
				volontari = impostaNuovoVolontarioPerNuovoTipoVisita(a, "");
			} while (volontari.equals(""));
		}
		else {
			volontari = "";
			String volontario = "";
			boolean bool = true;
			do {
				volontario = (String)a.richiediVal(CostantiStruttura.STRING, "volontario che gestirà la visita");
				if (!gdb.checkIfVolontarioExists(volontario)) a.view("L'username inserito non è associato a nessun volontario.");
				else {
					if (!volontari.contains(volontario)) {
						volontari += volontario + ",";
						bool = chiediSioNo(a, "Vuoi inserire un altro volontario?");
					}
					else {
						a.view("Volontario già inserito!");
						bool = true;
					}
				}
			} while (!gdb.checkIfVolontarioExists(volontario) || bool);
		}
		if (gdb.aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabili, oraInizio, durataVisita, ticket, minFruitore, maxFruitore, volontari)) {
			a.view("Il nuovo tipo di visita è stato aggiunto.");
			return true;
		}
		else {
			a.view("Il nuovo tipo di visita non è stato aggiunto.");
			return false;
		}
		
	}
	@MethodName("Aggiungi credenziali nuovo configuratore")
	public void impostaCredenzialiNuovoConfiguratore (App a) {
		String username = (String)a.richiediVal(CostantiStruttura.STRING, "username del nuovo configuratore");
		String password = (String)a.richiediVal(CostantiStruttura.STRING, "password del nuovo configuratore");
		if (gdb.impostaCredenzialiNuovoConfiguratore(username, password)) a.view("Aggiunto nuovo configuratore.");
		else a.view("Non è stato aggiunto un nuovo configuratore, username già in utilizzo.");
				
	}
	@MethodName("Visualizza visite proposte, complete, confermate, cancellate e effettuate")
	public void getElencoVisiteProposteCompleteConfermateCancellateEffettuate (App a) {
		a.view(gdb.getElencoVisiteProposteCompleteConfermateCancellateEffettuate());
	}

	@MethodName("Aggiungi luogo")
	public void aggiungiLuogo (App a) {
		String tag = (String)a.richiediVal(CostantiStruttura.STRING, "tag del luogo");
		String nome = (String)a.richiediVal(CostantiStruttura.STRING, "nome del luogo");
		String collocazione = (String)a.richiediVal(CostantiStruttura.STRING, "collocazione del luogo");
		String tipiVisitaVal = ""; //TODO per i luoghi sono da creare i tipi di visita, non da riutilizzare
		if (gdb.aggiungiLuogo(tag, nome, collocazione, tipiVisitaVal)) a.view("Aggiunto un nuovo luogo.");
		boolean notFinished = true;
		do {
			notFinished = !aggiungiTipoVisitePartendoDaLuogo(a, tag);
			if (!notFinished) {
				a.view("Vuoi inserire altri tipi di visite?");
				notFinished = chiediSioNo(a, ""); //se si, allora ha finito
			}
		} while (notFinished);
		
	}
	
}
