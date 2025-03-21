package controller;

import main.App;
import utility.CostantiStruttura;
import utility.MethodName;


public class HandlerConfiguratore extends ControllerUtente{	
		
	public HandlerConfiguratore () {
		
	}

	public HandlerConfiguratore(ControllerArchivio gdb, String username, App a) {
		this.gdb = gdb;
		this.username = username;
		checkPrimoAccesso(a);
		if (checkPrimaConfigurazioneArchivio(a)) configuraArchivio(a);
	}
	
	private boolean checkPrimaConfigurazioneArchivio (App a) {
		return gdb.checkPrimaConfigurazioneArchivio(username);
	}
	
	private void configuraArchivio(App a) {
		String ambito = (String)a.richiediVal(CostantiStruttura.STRING, "nome ambito territoriale");
		impostaAmbitoTerritoriale(ambito);
		modificaMaxPrenotazione(a);
		a.view("Inizio fase creazione luoghi dell'ambito territoriale.");
		aggiungiLuogo(a);
		
	}
	
	
	@MethodName("Aggiungi nuovo volontario")
	public void impostaCredenzialiNuovoVolontario (App a) {
		String username = (String) a.richiediVal(CostantiStruttura.STRING, "username del nuovo volontario");
		String password = (String) a.richiediVal(CostantiStruttura.STRING, "password del nuovo volontario");
		String tipi_visiteVal = (String) a.richiediVal(CostantiStruttura.STRING, "tipi delle visite associate al nuovo volontario (tipo1, tipo2,...)");
		if (gdb.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal)) a.view("Inserito nuovo volontario.");
		else a.view("Non è stato inserito il nuovo volontario.");	
	}
	
	private void impostaAmbitoTerritoriale(String s) {
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	private boolean impostaMaxPrenotazione(int maxPrenotazione) {
		return (gdb.modificaMaxPrenotazione(maxPrenotazione));
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public void modificaMaxPrenotazione (App a) {
		int max = (int) a.richiediVal(CostantiStruttura.INT, "max prenotazione per fruitore");
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
	public boolean indicaDatePrecluse(App a) {
		String data = (String)a.richiediVal(CostantiStruttura.STRING, "data preclusa"); //TODO comunicare per quale mese deve essere
		return (gdb.indicaDatePrecluse(data));
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
		int durataVisita = (int)a.richiediVal(CostantiStruttura.STRING, "durata della visita in minuti (ad esempio 120 sono 120 minuti, quindi 2 ore)");
		String ticket = (String)a.richiediVal(CostantiStruttura.STRING, "se è da acquistare o no un biglietto (si/no)");
		int minFruitore =(int) a.richiediVal(CostantiStruttura.STRING, "minimo fruitori per confermare la visita");
		int maxFruitore = (int)a.richiediVal(CostantiStruttura.STRING, "massimo fruitori per completare la visita");
		String volontari = (String)a.richiediVal(CostantiStruttura.STRING, "volontari che gestiranno la visita (volontario1, volontario2,...)");
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
		String luogo = (String)a.richiediVal(CostantiStruttura.STRING, "luogo della visita");
		String tipoVisita = (String)a.richiediVal(CostantiStruttura.STRING, "tag del tipo della visita");
		String titolo = (String)a.richiediVal(CostantiStruttura.STRING, "titolo della visita");
		String descrizione = (String)a.richiediVal(CostantiStruttura.STRING, "descrizione riassuntiva della visita");
		String puntoIncontro = (String)a.richiediVal(CostantiStruttura.STRING, "punto di incontro della visita (locazione geografica)");
		String dataInizio = (String)a.richiediVal(CostantiStruttura.STRING, "apertura del periodo della visita");
		String dataFine = (String)a.richiediVal(CostantiStruttura.STRING, "chiusura del periodo della visita");
		String giorniPrenotabili = (String)a.richiediVal(CostantiStruttura.STRING, "giorni prenotabili della visita (1, 3, 7 indicano lun, mer, dom)");
		String oraInizio = (String)a.richiediVal(CostantiStruttura.STRING, "ora d'inizio visita");
		int durataVisita = (int)a.richiediVal(CostantiStruttura.STRING, "durata della visita in minuti (ad esempio 120 sono 120 minuti, quindi 2 ore)");
		boolean ticket = chiediSioNo(a, "se è da acquistare o no un biglietto (si/no)");
		int minFruitore =(int) a.richiediVal(CostantiStruttura.STRING, "minimo fruitori per confermare la visita");
		int maxFruitore = (int)a.richiediVal(CostantiStruttura.STRING, "massimo fruitori per completare la visita");
		String volontari = (String)a.richiediVal(CostantiStruttura.STRING, "volontari che gestiranno la visita (volontario1, volontario2,...)");
		
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
	public String getElencoVisiteProposteCompleteConfermateCancellateEffettuate (App a) {
		
		/*
		 * 
		 * Le visite proposte sono nel piano pubblicato del mese i+1
		 * Le visite complete sono nello stesso piano di proposte ma non accettano fruitori (confronto MaxFruitore e prenotazioni)
		 * Le visite confermate sono nello stesso piano di proposte di prima ma non accettano più nulla anche se sotto maxfruitore
		 * Le visite cancellate sono come le confermate ma non vengono successivamente memorizzate nello storico una volta fatte
		 * Le visite effettuate sono quelle confermate che superano la data quando viene effettuata
		 * 
		 */
		
		return "";
	}
	private boolean chiediSioNo (App a, String val) {
		do {
			String answer = (String)a.richiediVal(CostantiStruttura.STRING, "");
			switch (answer.toLowerCase()) {
			case "si":
				return true;
			case "no":
				return false;
			default:
				a.view("Formato non valido, inserire si/no");
			}
		} while (true);
		

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
	@MethodName("Aggiungi nuova visita nel piano da pubblicare")
	public void aggiungiVisita () {
		
	}
	
}
