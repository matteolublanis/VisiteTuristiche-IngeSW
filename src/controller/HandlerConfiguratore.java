package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import archivio.ArchivioFacade;
import main.App;
import utility.CostantiStruttura;
import utility.MethodName;
import utility.Time;


public class HandlerConfiguratore extends ControllerUtente{	
	//Precondizione tutti i metodi: param != null
	
	public HandlerConfiguratore(ArchivioFacade archivio, App a, String connectionCode) {
		this.archivio = archivio;
		this.a = a;
		this.connectionCode = connectionCode;
	}
	
	protected void checkPrimoAccesso () {
		super.checkPrimoAccesso();
		if (checkPrimaConfigurazioneArchivio()) configuraArchivio();
	}
	
	private boolean checkPrimaConfigurazioneArchivio () {
		return archivio.checkPrimaConfigurazioneArchivio(connectionCode);
	}
	//Postcondizione: nome ambito impostato, max prenotazione impostato, luoghi aggiunti, tipi visita aggiunti, volontari nuovi o esistenti associati
	private void configuraArchivio() {
		String ambito = null;
		do {
			ambito = a.richiediInput("nome ambito territoriale");
		} while (!a.chiediSioNo("Confermi di voler chiamare l'ambito " + ambito + " ?"));
		impostaAmbitoTerritoriale(ambito);
		modificaMaxPrenotazione(a);
		a.view("Inizio fase creazione luoghi dell'ambito territoriale.");
		aggiungiLuogo();
	}
	//Postcondizione: volontario creato con tipo visita associato
	private String impostaNuovoVolontarioConUnTipoVisitaScelto (String tipo) {
		String username = a.richiediInput("username del nuovo volontario");
		String password = a.richiediInput("password del nuovo volontario");
		Set<String> tipi_visiteVal = new HashSet<>();
		tipi_visiteVal.add(tipo);
		if (archivio.impostaCredenzialiNuovoVolontario(connectionCode, username, password, tipi_visiteVal, false)) {
			a.view("Inserito nuovo volontario.");
			return username;
		}
		else {
			a.view("Non è stato inserito il nuovo volontario, username già in uso.");
			return "";
		}
	}
	
	//Postcondizione: volontario creato con tipo visita associato
	private boolean impostaNuovoVolontarioConTipoVisitaScelto (App a, Set<String> tipi_visiteVal, List<String> volontari) {
		String username = a.richiediInput("username del nuovo volontario");
		String password = a.richiediInput("password del nuovo volontario");
		if (archivio.impostaCredenzialiNuovoVolontario(connectionCode, username, password, tipi_visiteVal, false)) {
			volontari.add(username);
			return true;
		}
		else {
			return false;
		}
	}
	
	//Postcondizione: luogo rimosso da Archivio, associazioni rimosse
	@MethodName("Rimuovi luogo")
	public void rimuoviLuogo () {
		if (canAddOrRemove()) {
			boolean rimosso = archivio.rimuoviLuogo(a.richiediInput("luogo da rimuovere"), connectionCode);
			a.view(rimosso ? "Luogo rimosso con successo, controllare conseguenze." : "Luogo non rimosso, controllare di aver inserito i dati correttamente.");
		}
	}
	
	//Postcondizione: volontario rimosso da Archivio, associazioni rimosse
	@MethodName("Rimuovi volontario")
	public void rimuoviVolontario () {
		if (canAddOrRemove()) {
			boolean rimosso = (archivio.rimuoviVolontario(a.richiediInput("username del volontario da rimuovere"), connectionCode));
			a.view(rimosso ? "Volontario rimosso con successo, controllare conseguenze." : "Volontario non rimosso, controllare di aver inserito i dati correttamente.");
		}
	}
	
	//Postcondizione: tipo visita rimosso da Archivio, associazioni rimosse
	@MethodName("Rimuovi tipo di visita")
	public void rimuoviTipo () {
		if (canAddOrRemove()) {
			boolean rimosso = archivio.rimuoviTipo(a.richiediInput("tipo da rimuovere"), connectionCode);
			a.view(rimosso ? "Tipo visita rimosso con successo, controllare conseguenze." : "Tipo visita non rimosso, controllare di aver inserito i dati correttamente.");
		}
	}
	
	/*
	@MethodName("Aggiungi nuovo volontario")
	public void impostaCredenzialiNuovoVolontario (App a) {
		if (canAddOrRemove(a)) {
			String username = a.richiediInput("username del nuovo volontario");
			String password = a.richiediInput("password del nuovo volontario");
			Set<String> tipi_visiteVal = new HashSet<>();
			do {
				tipi_visiteVal.add(a.richiediInput("tipo delle visite associate al nuovo volontario"));
			} while (a.chiediSioNo("Vuoi aggiungere altri tipi di visite?"));
			boolean impostato = gdb.impostaCredenzialiNuovoVolontario(connectionCode, username, password, tipi_visiteVal, true);
			a.view(impostato ? "Inserito nuovo volontario." : "Non è stato inserito il nuovo volontario, username in uso o non sono stati inseriti tipi di visita esistenti.");
		}
	}
	*/
	
	//Postcondizione: impostato nome ambito territoriale
	private void impostaAmbitoTerritoriale(String s) {
		archivio.impostaAmbitoTerritoriale(s, connectionCode);
	}
	
	//Precondizione: maxPrenotazione > 0
	//Postcondizione: impostato max prenotazione
	private boolean impostaMaxPrenotazione(int maxPrenotazione) {
		return (archivio.modificaMaxPrenotazione(connectionCode, maxPrenotazione));
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public void modificaMaxPrenotazione (App a) {
		int max = 0;
		do {
			max = a.richiediInt("max prenotazione per fruitore");
			if (max < 1) a.view("Valore inserito non valido.");
			else if (a.chiediSioNo("Confermi?")) {
				break;
			}
			else continue;
		} while (true);
		a.view(impostaMaxPrenotazione(max) ? "Modificato valore max prenotazione." : "Valore max prenotazione non modificato.");
	}
	
	@MethodName("Visualizza lista volontari")
	public void getListaVolontari(App a) {
		a.visualSetGeneric(archivio.getListaUser(connectionCode, CostantiStruttura.VOLONTARIO), "Lista volontari");
	}
	
	@MethodName("Visualizza elenco luoghi visitabili")
	public void getElencoLuoghiVisitabili(App a) {
		List<String> luoghiVisitabili = archivio.getElencoLuoghiVisitabili(connectionCode);
		a.visualListGeneric(luoghiVisitabili, "Luoghi visitabili");
	}
	
	@MethodName("Visualizza elenco tipi visite per luogo")
	public void getElencoTipiVisiteLuogo(App a) {
		Map<String, List<String>> elencoTipiVisiteLuoghi = archivio.getElencoTipiVisiteLuogo(connectionCode);
		for (String luogo : elencoTipiVisiteLuoghi.keySet()) {
			a.visualListGeneric(elencoTipiVisiteLuoghi.get(luogo), "Luogo: " + luogo + ", tipi associati");
		}
	}
	
	//Precondizione: isTodays(16) o dopo
	//Postcondizione: piano pubblicato
 	@MethodName("Pubblica il piano delle visite")
	public void pubblicaPianoVisite(App a) {
		if (archivio.isReleaseOrLaterDay(connectionCode)) {
			if (archivio.isPrimaPubblicazione()) {
				a.view("Applicazione ufficialmente aperta.");
				archivio.pubblicaPiano(connectionCode);
				archivio.apriRaccoltaDisponibilita(connectionCode);
			}
			else {
				a.view(archivio.pubblicaPiano(connectionCode) ? "Piano pubblicato." : "Piano non pubblicato.");
			}
		}
		else a.view("Non è possibile pubblicare adesso il piano.");
	}
	
	@MethodName("Indica date precluse del prossimo piano a quello successivo a questo")
	public void indicaDatePrecluse() {
		String data = null; 
		do {
			data = a.richiediDataValida("data preclusa (dd-mm-yyyy)"); 
			if (a.chiediSioNo("Confermi di inserire " + data + " come data preclusa?")) {
				break;
			}
			else return;
		} while (true);
		if ((archivio.indicaDatePrecluse(connectionCode, data))) a.view("La data preclusa è stata inserita.");
		else a.view("La data preclusa non è stata inserita, assicurarsi che sia nel periodo corretto o di aver pubblicato l'app.");
	}
	
	@MethodName("Apri la raccolta delle disponibilità dei volontari")
	public void apriRaccoltaDisponibilita () {
		if (archivio.apriRaccoltaDisponibilita(connectionCode)) {
			a.view("La raccolta delle disponibilità è stata aperta.");
		}
		else {
			a.view("La raccolta delle disponibilità non è stata aperta, bisogna prima pubblicare il piano.");
		}
	}
	
	@MethodName("Chiudi la raccolta delle disponibilità dei volontari")
	public void chiudiRaccoltaDisponibilita () {
		if (archivio.chiudiRaccoltaDisponibilita(connectionCode)) {
			a.view("La raccolta delle disponibilità è stata chiusa.");
		}
		else {
			a.view("La raccolta delle disponibilità non è stata chiusa, non è ancora il momento.");
		}
	}
	
	private String richiediTipoVisita() {
	    String tipoVisita;
	    do {
	        tipoVisita = a.richiediInput("tag del tipo della visita");
	        if (archivio.checkIfVisitTypeExists(tipoVisita)) {
	            a.view("Il tag inserito esiste già.");
	        }
	    } while (archivio.checkIfVisitTypeExists(tipoVisita));
	    return tipoVisita;
	}
	
	private ArrayList<Integer> richiediGiorniPrenotabili() {
		ArrayList<Integer> giorni = new ArrayList<>();
	    boolean continua = true;
	    do {
	        int giorno = a.richiediInt("giorno prenotabile della visita (1-7)");
	        if (giorno < 1 || giorno > 7) {
	            a.view("Numero inserito non valido, deve essere tra 1 e 7.");
	        } else if (!giorni.contains(giorno)) { //se non contiene
	            giorni.add(giorno); //aggiungi giorno
	            continua = a.chiediSioNo("Vuoi aggiungere un altro giorno prenotabile?");
	        } else {
	            a.view("Giorno già inserito!");
	            continua = true;
	        }
	        if (giorni.size() == 7) continua = false; //inseriti TUTTI i giorni della settimana
	    } while (continua);
	    return giorni;
	}
	
	private ArrayList<String> richiediVolontari() {
		ArrayList<String> volontari = new ArrayList<>();
        boolean continua = true;
		do {
		    if (a.chiediSioNo("Vuoi associare un nuovo volontario per questo tipo di visita?")) {
		        boolean volontarioCreato;
		        do {
		        	volontarioCreato = impostaNuovoVolontarioConTipoVisitaScelto(a, null, volontari);
		        	if (volontarioCreato) {
		        		a.view("Inserito nuovo volontario.");
		        	}
		        	else a.view("Non è stato inserito il nuovo volontario, username già in uso.");
		        } while (!volontarioCreato);
	    		continua = a.chiediSioNo("Vuoi inserire un altro volontario?");
		    } 
		    else {
		    	String volontario = a.richiediInput("volontario che gestirà la visita");
		    	if (!archivio.checkIfUserExists(volontario)) {
		    		a.view("L'username inserito non è associato a nessun volontario.");
		    		continua = true;
		    	} else if (!volontari.contains(volontario)) {
		    		volontari.add(volontario);
		    		continua = a.chiediSioNo("Vuoi inserire un altro volontario?");
		    	} else {
		    		a.view("Volontario già inserito!");
		    		continua = true;
		    	}
		    }
		} while (continua);
	    return volontari;
	}
	
	//Precondizione: luogo diverso da null (presente nell'archivio)
	//Postcondizione: tipo visita inserito e inserito associazione
	private boolean aggiungiTipoVisitePartendoDaLuogo (String luogo) {
		String tipoVisita = richiediTipoVisita();
		String titolo = a.richiediInput("titolo della visita");
		String descrizione = a.richiediInput("descrizione riassuntiva della visita");
		String puntoIncontro = a.richiediInput("punto di incontro della visita (locazione geografica)");
		String dataInizio = a.richiediDataValida("apertura del periodo della visita (dd-mm-yyyy)");
		String dataFine = "";
		do {
			dataFine = a.richiediDataValida("chiusura del periodo della visita (dd-mm-yyyy)");
			if (Time.comesBefore(dataFine, dataInizio)) a.view("Non può finire prima che inizi.");
		} while (Time.comesBefore(dataFine, dataInizio));
		ArrayList<Integer> giorniPrenotabili = richiediGiorniPrenotabili();
		String oraInizio = a.richiediOraValida("ora d'inizio visita (hh-mm)");
		int durataVisita = a.richiediInt("durata della visita in minuti (ad esempio 120 sono 120 minuti, quindi 2 ore)");
		boolean ticket = a.chiediSioNo("è da acquistare o no un biglietto?");
		int minFruitore = a.richiediNumeroConLimite("minimo fruitori per confermare la visita", 0);
		int maxFruitore = a.richiediNumeroConLimite("massimo fruitori per completare la visita", minFruitore);
		ArrayList<String> volontari = richiediVolontari();
		return (archivio.aggiungiTipoVisite(connectionCode, luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabili, oraInizio, durataVisita, ticket, minFruitore, maxFruitore, volontari));
	}
	
	@MethodName("Aggiungi tipo visite")
	public void aggiungiTipoVisite() {
		if (canAddOrRemove()) {
			String luogo = "";
			do {
				luogo = a.richiediInput("luogo della visita");
				if (!archivio.checkIfPlaceExists(luogo)) a.view("Il luogo inserito è inesistente.");
			} while (!archivio.checkIfPlaceExists(luogo));
			
			a.view((aggiungiTipoVisitePartendoDaLuogo(luogo)) ? "Il nuovo tipo di visita è stato aggiunto." : "Il nuovo tipo di visita non è stato aggiunto.");
		}
		
	}
	@MethodName("Aggiungi credenziali nuovo configuratore")
	public void impostaCredenzialiNuovoConfiguratore () {
		String username = a.richiediInput("username del nuovo configuratore");
		String password = a.richiediInput("password del nuovo configuratore");
		if (archivio.impostaCredenzialiNuovoConfiguratore(connectionCode, username, password)) a.view("Aggiunto nuovo configuratore.");
		else a.view("Non è stato aggiunto un nuovo configuratore, username già in utilizzo.");
				
	}
	@MethodName("Visualizza visite proposte, complete, confermate, cancellate e effettuate")
	public void getElencoVisiteProposteCompleteConfermateCancellateEffettuate (App a) {
		a.visualListGeneric(archivio.getElencoVisiteProposteCompleteConfermateCancellateEffettuate(connectionCode), "Elenco visite");
	}
	
	private void associaVolontarioEsistente(String tipo) {
	    getListaVolontari(a);
	    String volontario;
	    do {
	        volontario = a.richiediInput("volontario da associare (esc per tornare indietro)");
	        if (volontario.equalsIgnoreCase("esc")) return;
	        if (!archivio.checkIfUserExists(volontario)) {
	            a.view("Il volontario inserito non esiste, reinserire.");
	        } else if (!archivio.associaVolontarioEsistenteATipoVisitaEsistente(connectionCode, volontario, tipo)) {
	            a.view("Problema nell'inserimento del volontario, potrebbe esserci un conflitto con i giorni.");
	        } else {
	            break;
	        }
	    } while (true);
	}
	
	@MethodName("Aggiungi volontari ad un tipo di visita esistente")
	public void aggiungiVolontariATipiVisita () {
		if (canAddOrRemove()) {
			Set<String> s = archivio.getElencoTipiVisite(connectionCode);
			a.view("Elenco dei tag delle visite esistenti:");
			for(String i : s) {
				a.view(i);
			}
			String tipo = richiediVisitaEsistente("tag del tipo della visita a cui associare i volontari");
			boolean continua;
			do {
				if (a.chiediSioNo("Vuoi creare un nuovo volontario da associare?")) {
					while (impostaNuovoVolontarioConUnTipoVisitaScelto(tipo).isEmpty());
				}
				else {
					associaVolontarioEsistente(tipo);
				}
				continua = a.chiediSioNo("Vuoi aggiungere altri volontari?");
			} while (continua);
		}
		else a.view("Non puoi apportare modifiche al momento.");
	}
	
	private boolean canAddOrRemove() {
		if (archivio.canAddOrRemove(connectionCode)) {
			return true;
		}
		else {
			a.view("Al momento non puoi aggiungere o rimuovere nulla, quando verrà pubblicato il piano sarà possibile intervenire sull'archivio.");
			return false;
		}
	}
	
	@MethodName("Aggiungi luogo")
	public void aggiungiLuogo () {
		if (canAddOrRemove()) { 
			do {
				String tag = a.richiediInput("tag del luogo");
				String nome = a.richiediInput("nome del luogo");
				String descrizione = a.richiediInput("descrizione del luogo");
				String collocazione = a.richiediInput("collocazione del luogo");
				if (archivio.aggiungiLuogo(connectionCode, tag, nome, descrizione, collocazione, null)) {
					a.view("Aggiunto un nuovo luogo.");
					boolean aggiunto = false;
					do {
						aggiunto = aggiungiTipoVisitePartendoDaLuogo(tag);
						a.view(aggiunto ? "Il nuovo tipo di visita è stato aggiunto." : "Il nuovo tipo di visita non è stato aggiunto.");
						if (aggiunto) {
							aggiunto = !a.chiediSioNo("Vuoi continuare con l'aggiunta di tipi di visite?"); //se non vuole inserire ha finito
						}
					} while (!aggiunto);
				}
				else a.view("Il luogo non è stato aggiunto, controllare il tag inserito.");
			} while (a.chiediSioNo("Vuoi aggiungere un altro luogo?"));
		}
		
	}
	
}
