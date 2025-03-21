package controller;

import main.App;
import utility.CostantiStruttura;
import utility.MethodName;
import utility.ParamName;


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
		a.view("Inserisci nome ambito territoriale:");
		String ambito = (String)a.richiediVal(CostantiStruttura.STRING);
		impostaAmbitoTerritoriale(ambito);
		a.view("Inserisci max prenotazione per fruitore:");
		int max = (int) a.richiediVal(CostantiStruttura.INT);
		modificaMaxPrenotazione(max);
		
		//ciclo creazione luoghi
		
	}
	
	
	@MethodName("Aggiungi nuovo volontario")
	public boolean impostaCredenzialiNuovoVolontario (@ParamName("Username")String username, 
			@ParamName("Password")String password, 
			@ParamName("Tipi visite associati (tipo1, tipo2,...)")String tipi_visiteVal) {
		return gdb.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal);
	}
	
	@MethodName("Imposta ambito territoriale")
	private void impostaAmbitoTerritoriale(String s) {
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public boolean modificaMaxPrenotazione(@ParamName("N max per prenotazione di un fruitore")int maxPrenotazione) {
		return (gdb.modificaMaxPrenotazione(maxPrenotazione));
	}
	@MethodName("Visualizza lista volontari")
	public String getListaVolontari() {
		 return gdb.getListaUser(CostantiStruttura.VOLONTARIO);
	}
	
	@MethodName("Visualizza elenco luoghi visitabili")
	public String getElencoLuoghiVisitabili() {
		return gdb.getElencoLuoghiVisitabili();
	}
	
	@MethodName("Visualizza elenco tipi visite per luogo")
	public String getElencoTipiVisiteLuogo() {
		return gdb.getElencoTipiVisiteLuogo();
	}
	
	@MethodName("Pubbica il piano delle visite")
	public void pubblicaPianoVisite() {
		
	}
	
	@MethodName("Indica date precluse del prossimo piano a quello successivo a questo")
	public boolean indicaDatePrecluse(@ParamName("Giorno precluso del mese successivo al prossimo")String data) {
		return (gdb.indicaDatePrecluse(data));
	}
	
	@MethodName("Aggiungi tipo visite")
	public boolean aggiungiTipoVisite(@ParamName("Luogo associato") String luogo,
			@ParamName("Codice id visita")String tipoVisita, 
			@ParamName("Titolo visita")String titolo, 
			@ParamName("Descrizione riassuntiva")String descrizione,
			@ParamName("Punto d'incontro")String puntoIncontro,
			@ParamName("Apertura periodo visita (gg-mm-yyyy)")String dataInizio,
			@ParamName("Chiusura periodo visita (gg-mm-yyyy)")String dataFine,
			@ParamName("Giorni prenotabili (es. '1, 2, 3' per 'lunedì, martedì, mercoledì')")String giorniPrenotabili,
			@ParamName("Ora inizio (hh:mm)") String oraInizio,
			@ParamName("Durata visita in minuti") int durataVisita,
			@ParamName("Acquistabile (si/no)") String ticket,
			@ParamName("Minimo fruitori per confermarla") int minFruitore,
			@ParamName("Massimo fruitori per questa visita") int maxFruitore,
			@ParamName("Volontari associati (volontario1, volontario2,...") String volontari) {
		boolean daAcquistare = false;
		switch (ticket.toLowerCase()) {
		case "si":
			daAcquistare = true;
			break;
		case "no":
			break;
		default:
				return false;
		}
		return (gdb.aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabili, oraInizio, durataVisita, daAcquistare, minFruitore, maxFruitore, volontari));
		
	}
	@MethodName("Aggiungi credenziali nuovo configuratore")
	public boolean impostaCredenzialiNuovoConfiguratore (@ParamName("Username")String username, @ParamName("Password")String password) {
		return (gdb.impostaCredenzialiNuovoConfiguratore(username, password));
				
	}
	@MethodName("Visualizza visite proposte, complete, confermate, cancellate e effettuate")
	public String getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		
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
	@MethodName("Aggiungi luogo")
	public boolean aggiungiLuogo (@ParamName("Tag")String tag, 
			@ParamName("Nome")String nome, 
			@ParamName("Collocazione")String collocazione, 
			@ParamName("Tipi visita (tipo1, tipo2, ...)")String tipiVisitaVal) {
		return (gdb.aggiungiLuogo(tag, nome, collocazione, tipiVisitaVal));
	}
	@MethodName("Aggiungi nuova visita nel piano da pubblicare")
	public void aggiungiVisita () {
		
	}
	
}
