package controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dto.*;
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
		String ambito = a.richiediInput("nome ambito territoriale");
		impostaAmbitoTerritoriale(ambito);
		modificaMaxPrenotazione(a);
		a.view("Inizio fase creazione luoghi dell'ambito territoriale.");
		aggiungiLuogo(a);
		gdb.setPrimaConfigurazione();
	}
	
	private String impostaNuovoVolontarioConUnTipoVisitaScelto (App a, String tipo) {
		String username = a.richiediInput("username del nuovo volontario");
		String password = a.richiediInput("password del nuovo volontario");
		Set<String> tipi_visiteVal = new HashSet<>();
		tipi_visiteVal.add(tipo);
		if (gdb.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, false)) {
			a.view("Inserito nuovo volontario.");
			return username;
		}
		else {
			a.view("Non è stato inserito il nuovo volontario, username già in uso.");
			return "";
		}
	}
	
	private String impostaNuovoVolontarioConTipoVisitaScelto (App a, Set<String> tipi_visiteVal) {
		String username = a.richiediInput("username del nuovo volontario");
		String password = a.richiediInput("password del nuovo volontario");
		if (gdb.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, false)) {
			a.view("Inserito nuovo volontario.");
			return username;
		}
		else {
			a.view("Non è stato inserito il nuovo volontario, username già in uso.");
			return "";
		}
	}
	@MethodName("Rimuovi luogo")
	public void rimuoviLuogo (App a) {
		if (gdb.canAddOrRemove(username)) {
			boolean rimosso = gdb.rimuoviLuogo(a.richiediInput("luogo da rimuovere"), username);
			a.view(rimosso ? "Luogo rimosso con successo, controllare conseguenze." : "Luogo non rimosso, controllare di aver inserito i dati correttamente.");
		}
		else a.view("Non puoi attuare queste modifiche attualmente.");
	}

	@MethodName("Rimuovi volontario")
	public void rimuoviVolontario (App a) {
		if (gdb.canAddOrRemove(username)) {
			boolean rimosso = (gdb.rimuoviVolontario(a.richiediInput("username del volontario da rimuovere"), username));
			a.view(rimosso ? "Volontario rimosso con successo, controllare conseguenze." : "Volontario non rimosso, controllare di aver inserito i dati correttamente.");
		}
		else a.view("Non puoi attuare queste modifiche attualmente.");
	}

	@MethodName("Rimuovi tipo di visita")
	public void rimuoviTipo (App a) {
		if (gdb.canAddOrRemove(username)) {
			boolean rimosso = gdb.rimuoviTipo(a.richiediInput("tipo da rimuovere"), username);
			a.view(rimosso ? "Tipo visita rimosso con successo, controllare conseguenze." : "Tipo visita non rimosso, controllare di aver inserito i dati correttamente.");
		}
		else a.view("Non puoi attuare queste modifiche attualmente.");
	}
	
	
	@MethodName("Aggiungi nuovo volontario")
	public void impostaCredenzialiNuovoVolontario (App a) {
		if (canAddOrRemove(a)) {
			String username = a.richiediInput("username del nuovo volontario");
			String password = a.richiediInput("password del nuovo volontario");
			Set<String> tipi_visiteVal = new HashSet<>();
			do {
				tipi_visiteVal.add(a.richiediInput("tipo delle visite associate al nuovo volontario"));
			} while (a.chiediSioNo("Vuoi aggiungere altri tipi di visite?"));
			boolean impostato = gdb.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, true);
			a.view(impostato ? "Inserito nuovo volontario." : "Non è stato inserito il nuovo volontario, username in uso o non sono stati inseriti tipi di visita esistenti.");
		}
	}
	
	private void impostaAmbitoTerritoriale(String s) {
		gdb.impostaAmbitoTerritoriale(s, username);
	}
	
	private boolean impostaMaxPrenotazione(int maxPrenotazione) {
		return (gdb.modificaMaxPrenotazione(username, maxPrenotazione));
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public void modificaMaxPrenotazione (App a) {
		int max = a.richiediInt("max prenotazione per fruitore");
		a.view(impostaMaxPrenotazione(max) ? "Modificato valore max prenotazione." : "Valore max prenotazione non modificato.");
	}
	
	@MethodName("Visualizza lista volontari")
	public void getListaVolontari(App a) {
		for (UserDTO user : gdb.getListaUser(username, CostantiStruttura.VOLONTARIO)) {
			a.view("Volontario: " + user.getUsername());
			List<String> tipiList = user.getTipi_visite();
			String tipiString = "";
			for (String tipo : tipiList) tipiString += tipo + " ";
			a.view("Tipi associati: " + tipiString);
		}
	}
	
	@MethodName("Visualizza elenco luoghi visitabili")
	public void getElencoLuoghiVisitabili(App a) {
		List<String> luoghiVisitabili = gdb.getElencoLuoghiVisitabili(username);
		a.view("Luoghi visitabili:");
		for (String luogo : luoghiVisitabili) a.view(luogo);
	}
	
	@MethodName("Visualizza elenco tipi visite per luogo")
	public void getElencoTipiVisiteLuogo(App a) {
		Map<String, List<String>> elencoTipiVisiteLuoghi = gdb.getElencoTipiVisiteLuogo(username);
		for (String luogo : elencoTipiVisiteLuoghi.keySet()) {
			a.view("Luogo: " + luogo);
			List<String> tipiVisita = elencoTipiVisiteLuoghi.get(luogo);
			a.view("Tipi associati:");
			for (String tipo : tipiVisita) {
				a.view(tipo);
			}
		}
	}
	
	@MethodName("Pubblica il piano delle visite")
	public void pubblicaPianoVisite(App a) {
		if (gdb.isReleaseOrLaterDay()) {
			if (gdb.isPrimaPubblicazione()) {
				a.view("Applicazione ufficialmente aperta.");
				gdb.pubblicaPiano(username);
				gdb.apriRaccoltaDisponibilita(username);
			}
			else {
				a.view(gdb.pubblicaPiano(username) ? "Piano pubblicato." : "Piano non pubblicato.");
			}
		}
		else a.view("Non è possibile pubblicare adesso il piano.");
	}
	
	@MethodName("Indica date precluse del prossimo piano a quello successivo a questo")
	public void indicaDatePrecluse(App a) {
		String data = a.richiediInput("data preclusa (dd-mm-yyyy)"); 
		if ((gdb.indicaDatePrecluse(data))) a.view("La data preclusa è stata inserita.");
		else a.view("La data preclusa non è stata inserita, assicurarsi che sia nel formato e nel periodo corretto.");
	}
	
	@MethodName("Apri la raccolta delle disponibilità dei volontari")
	public void apriRaccoltaDisponibilita (App a) {
		if (gdb.apriRaccoltaDisponibilita(username)) {
			a.view("La raccolta delle disponibilità è stata aperta.");
		}
		else {
			a.view("La raccolta delle disponibilità non è stata aperta, bisogna prima pubblicare il piano.");
		}
	}
	
	@MethodName("Chiudi la raccolta delle disponibilità dei volontari")
	public void chiudiRaccoltaDisponibilita (App a) {
		if (gdb.chiudiRaccoltaDisponibilita(username)) {
			a.view("La raccolta delle disponibilità è stata chiusa.");
		}
		else {
			a.view("La raccolta delle disponibilità non è stata chiusa, non è ancora il momento.");
		}
	}
	
	private String richiediTipoVisita(App a) {
	    String tipoVisita;
	    do {
	        tipoVisita = a.richiediInput("tag del tipo della visita");
	        if (gdb.checkIfVisitTypeExists(tipoVisita)) {
	            a.view("Il tag inserito esiste già.");
	        }
	    } while (gdb.checkIfVisitTypeExists(tipoVisita));
	    return tipoVisita;
	}
	
	private ArrayList<Integer> richiediGiorniPrenotabili(App a) {
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
	
	private String richiediOraValida(App a) {
	    String ora;
	    do {
	        ora = a.richiediInput("ora d'inizio visita (hh-mm)");
	        if (!Time.isValidHour(ora)) {
	            a.view("Formato non corretto, inserire tipo 10:30.");
	        }
	    } while (!Time.isValidHour(ora));
	    return ora;
	}
	
	private int richiediNumeroMinimo(App a) {
	    int min;
	    do {
	        min = a.richiediInt("minimo fruitori per confermare la visita");
	        if (min <= 0) {
	            a.view("Il numero minimo di fruitori non può essere minore o uguale a 0.");
	        }
	    } while (min <= 0);
	    return min;
	}
	
	private int richiediNumeroMassimo(App a, int min) {
	    int max;
	    do {
	        max = a.richiediInt("massimo fruitori per completare la visita");
	        if (max < min) {
	            a.view("Il numero massimo di fruitori non può essere minore del minimo.");
	        }
	    } while (max < min);
	    return max;
	}
	
	private ArrayList<String> richiediVolontari(App a) {
		ArrayList<String> volontari = new ArrayList<>();
	    if (a.chiediSioNo("Vuoi associare un nuovo volontario per questo tipo di visita?")) {
	        String volontario;
	        do {
	        	volontario = impostaNuovoVolontarioConTipoVisitaScelto(a, null);
	        } while (volontario.equals(""));
	        volontari.add(volontario);
	    } else {
	        boolean continua;
	        do {
	            String volontario = a.richiediInput("volontario che gestirà la visita");
	            if (!gdb.checkIfUserExists(volontario)) {
	                a.view("L'username inserito non è associato a nessun volontario.");
	                continua = true;
	            } else if (!volontari.contains(volontario)) {
	                volontari.add(volontario);
	                continua = a.chiediSioNo("Vuoi inserire un altro volontario?");
	            } else {
	                a.view("Volontario già inserito!");
	                continua = true;
	            }
	        } while (continua);
	    }
	    return volontari;
	}
	
	private boolean aggiungiTipoVisitePartendoDaLuogo (App a, String luogo) {
		String tipoVisita = richiediTipoVisita(a);
		String titolo = a.richiediInput("titolo della visita");
		String descrizione = a.richiediInput("descrizione riassuntiva della visita");
		String puntoIncontro = a.richiediInput("punto di incontro della visita (locazione geografica)");
		String dataInizio = richiediDataValida(a, "apertura del periodo della visita (dd-mm-yyyy)");
		String dataFine = "";
		do {
			dataFine = richiediDataValida(a, "chiusura del periodo della visita (dd-mm-yyyy)");
			if (Time.comesBefore(dataFine, dataInizio)) a.view("Non può finire prima che inizi.");
		} while (Time.comesBefore(dataFine, dataInizio));
		ArrayList<Integer> giorniPrenotabili = richiediGiorniPrenotabili(a);
		String oraInizio = richiediOraValida(a);
		int durataVisita = a.richiediInt("durata della visita in minuti (ad esempio 120 sono 120 minuti, quindi 2 ore)");
		boolean ticket = a.chiediSioNo("è da acquistare o no un biglietto?");
		int minFruitore = richiediNumeroMinimo(a);
		int maxFruitore = richiediNumeroMassimo(a, minFruitore);
		ArrayList<String> volontari = richiediVolontari(a);
		boolean aggiunto = (gdb.aggiungiTipoVisite(luogo, tipoVisita, titolo, descrizione, puntoIncontro, dataInizio, dataFine, giorniPrenotabili, oraInizio, durataVisita, ticket, minFruitore, maxFruitore, volontari));
		a.view(aggiunto ? "Il nuovo tipo di visita è stato aggiunto." : "Il nuovo tipo di visita non è stato aggiunto.");
		return aggiunto;
	}
	
	@MethodName("Aggiungi tipo visite")
	public void aggiungiTipoVisite(App a) {
		if (canAddOrRemove(a)) {
			String luogo = "";
			do {
				luogo = a.richiediInput("luogo della visita");
				if (!gdb.checkIfPlaceExists(luogo)) a.view("Il luogo inserito è inesistente.");
			} while (!gdb.checkIfPlaceExists(luogo));
			
			aggiungiTipoVisitePartendoDaLuogo(a, luogo);
		}
		
	}
	@MethodName("Aggiungi credenziali nuovo configuratore")
	public void impostaCredenzialiNuovoConfiguratore (App a) {
		String username = a.richiediInput("username del nuovo configuratore");
		String password = a.richiediInput("password del nuovo configuratore");
		if (gdb.impostaCredenzialiNuovoConfiguratore(this.username, username, password)) a.view("Aggiunto nuovo configuratore.");
		else a.view("Non è stato aggiunto un nuovo configuratore, username già in utilizzo.");
				
	}
	@MethodName("Visualizza visite proposte, complete, confermate, cancellate e effettuate")
	public void getElencoVisiteProposteCompleteConfermateCancellateEffettuate (App a) {
		for (VisitaDTO v : gdb.getElencoVisiteProposteCompleteConfermateCancellateEffettuate()) {
			a.view("Titolo: " +  v.getTitolo());
			a.view("Giorno: " +  v.getGiorno());
			a.view("Luogo: " +  v.getLuogo());
			a.view("Stato: " +  v.getStato());

		}
	}
	
	private void associaVolontarioEsistente(App a, String tipo) {
	    getListaVolontari(a);
	    String volontario;
	    do {
	        volontario = a.richiediInput("volontari da associare");
	        if (!gdb.checkIfUserExists(volontario)) {
	            a.view("Il volontario inserito non esiste, reinserire.");
	        } else if (!gdb.associaVolontarioEsistenteATipoVisitaEsistente(volontario, tipo)) {
	            a.view("Problema nell'inserimento del volontario, potrebbe esserci un conflitto con i giorni.");
	        } else {
	            break;
	        }
	    } while (true);
	} 
	
	@MethodName("Aggiungi volontari ad un tipo di visita esistente")
	public void aggiungiVolontariATipiVisita (App a) {
		if (canAddOrRemove(a)) {
			Set<String> s = gdb.getElencoTipiVisite();
			a.view("Elenco dei tag delle visite esistenti:");
			for(String i : s) {
				a.view(i);
			}
			String tipo = richiediVisitaEsistente(a, "tag del tipo della visita a cui associare i volontari");
			boolean continua;
			do {
				if (a.chiediSioNo("Vuoi creare un nuovo volontario da associare?")) {
					while (impostaNuovoVolontarioConUnTipoVisitaScelto(a, tipo).isEmpty());
				}
				else {
					associaVolontarioEsistente(a, tipo);
				}
				continua = a.chiediSioNo("Vuoi aggiungere altri volontari?");
			} while (continua);
		}
		else a.view("Non puoi apportare modifiche al momento.");
	}
	
	private boolean canAddOrRemove(App a) {
		if (gdb.canAddOrRemove(username)) {
			return true;
		}
		else {
			a.view("Al momento non puoi aggiungere o rimuovere nulla, quando verrà pubblicato il piano sarà possibile intervenire sull'archivio.");
			return false;
		}
	}
	
	@MethodName("Aggiungi luogo")
	public void aggiungiLuogo (App a) {
		if (canAddOrRemove(a)) {
			String tag = a.richiediInput("tag del luogo");
			String nome = a.richiediInput("nome del luogo");
			String collocazione = a.richiediInput("collocazione del luogo");
			if (gdb.aggiungiLuogo(tag, nome, collocazione, null)) a.view("Aggiunto un nuovo luogo.");
			boolean finished = false;
			do {
				finished = aggiungiTipoVisitePartendoDaLuogo(a, tag);
				if (finished) {
					finished = a.chiediSioNo("Vuoi terminare l'aggiunta di tipi di visite?"); //se non vuole inserire ha finito
				}
			} while (!finished);
		}
		
		
	}
	
}
