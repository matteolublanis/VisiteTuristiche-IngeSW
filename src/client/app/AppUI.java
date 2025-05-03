package client.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import client.controller_utente.ControllerUtente;
import client.log_events.AppEvent;
import client.login.Login;
import dto.DTO;
import dto.TipoVisitaDTO;
import utility.Credenziali;
import utility.Time;

public class AppUI implements App{ 
	
	private Scanner sc = new Scanner(System.in);
	private Login gestoreLogin;
	private ControllerUtente controllerUtente; 
	
	public AppUI() {
		this.gestoreLogin = new Login(this, 0);
	}
	
	//Precondizione: gu != null
	public void setGu (ControllerUtente gu) {
		this.controllerUtente = gu;
	}
	
	/*
	 * Funzione di start dell'applicazione, esegue il loop principale 
	 */
	public void start() {
		view("Benvenuto!");
		gestoreLogin.avvio(); //fase login

		do {
			
			if (!scegliAzione()) break;
			
		} while (true); 
		
		stop();
	}
	
	public void stop() {
		view("Arrivederci!");
		sc.close();
		System.exit(0);
	}
	
	/*
	 * L'app carica i metodi disponibili per il tipo di utente associato, in base all'input
	 * decide che azione compiere (@see eseguiAzione)
	 */
	private boolean scegliAzione () {
		view("Quale operazione desidera (ESC per uscire)?");
		visualNumberedListGeneric(controllerUtente.getAzioniDisponibiliConNomi());
		String input = richiediInput("l'azione da eseguire (da 1 a " + controllerUtente.getAzioniDisponibiliConNomi().size() + " o esc)");
		return controllerUtente.eseguiAzione(input);
	}

	//Precondizione: val != null
	public boolean chiediSioNo (String val) {
		view(val);
		do {
			String answer = (String) richiediInput("si o no");
			switch (answer.toLowerCase()) {
			case "si":
				return true;
			case "no":
				return false;
			default:
				view("Formato non valido, inserire si/no");
				break;
			}
		} while (true);

	}
	//Precondizione: msg != null
	public String richiediOraValida(String msg) {
	    String ora;
	    do {
	        ora = richiediInput(msg);
	        if (!Time.isValidHour(ora)) {
	            view("Formato non corretto, inserire tipo 10:30.");
	        }
	    } while (!Time.isValidHour(ora));
	    return ora;
	}
	//Precondizione: msg != null
	public int richiediNumeroConLimiteInferiore(String msg, int limit) {
	    int n;
	    do {
	        n = richiediInt(msg);
	        if (n <= limit) {
	            view("Non può essere più piccolo o uguale di " + limit + ".");
	        }
	    } while (n <= limit);
	    return n;
	}
	//Precondizione: messaggio != null
	public String richiediDataValida(String messaggio) {
	    String data;
	    do {
	        data = (String) richiediInput(messaggio);
	        if (!Time.isValidDate(data)) {
	            view("Formato data non valido, deve essere (dd-mm-yyyy)");
	        }
	    } while (!Time.isValidDate(data));
	    return data;
	}
	//Precondizione: s != null
	public int richiediInt (String s) {
		view("Inserisci " + s + ":");
		while (!sc.hasNextInt()) {
			view("Formato non valido, reinserire.");
			sc.nextLine();
		}
		int result = sc.nextInt();
		sc.nextLine();
		return result;
	}
	//Precondizione: s != null
	public String richiediInput (String s) {
		
		try {
			view("Inserisci " + s + ":");
			s = sc.nextLine();
			return s;
		}
		catch (NoSuchElementException e) {
			view("EOF individuato, arrivederci!");
			stop();
			return null;
		}

	}
	
	public Credenziali richiediCredenziali () {
		String username = richiediInput("username (ESC per tornare indietro)");
		if (username.equalsIgnoreCase("esc")) return null;
		String password = richiediInput("password");
		if (chiediSioNo("Confermi?")) { 
			return new Credenziali(username, password);
		}
		else return richiediCredenziali();
	}
	public <T> void visualSetGeneric (Set<T> list, String name) {
		view(name + ":");
		for (T x :list) view(x.toString());
	}
	
	public <T> void visualListGeneric (List<T> list, String name) {
		view(name + ":");
		for (T x : list) view(x.toString());
	}
	
	public <T> void visualNumberedListGeneric (List<T> list) {
		for (int i = 1 ; i <= list.size() ; i++) view(i + ") " + list.get(i-1).toString());
	}
	
	//Precondizione: msg != null
	public void view (String msg) throws NullPointerException {
		if (!msg.equals("")) System.out.println(msg);
	}

	@Override
	public void viewLogin(Credenziali credenzialiIniziali) {
		if (credenzialiIniziali != null) { 
			gestoreLogin.accesso();
		}
		else {
			if (chiediSioNo("Vuoi registrarti come nuovo utente?")) {
				gestoreLogin.registrazione();
			}
			else {
				gestoreLogin.accesso();
			}
		}
	}

	@Override
	public void catchEvent(AppEvent e) {
		view(e.getMessage()); 
		switch (e.getSeverity()) {
		case ERROR:
			stop();
			break;
		case INFO:
			break;
		case WARNING:
			break;
		default:
			break;
		}
		
	}
	@Override
	public void catchEvent(AppEvent e, String attachment) {
		view(e.getMessage(attachment));
		
	}

	@Override
	public void log(String msg) {
		view(msg);
	}

	@Override
	public void viewPrimoAccesso() {
		view("Primo accesso eseguito.");
		view("Cambia le tue credenziali:");
		
	}

	@Override
	public void viewListDTO(List<DTO> list) {
		for (DTO dto : list) {
			Map<String, List<String>> info = dto.infoDTO();
			for (String dettaglio : info.keySet()) {
				List<String> attributi = info.get(dettaglio);
				view(dettaglio + ": " + attributi);
			}
		}
		
	}

	@Override
	public TipoVisitaDTO richiediTipoVisita() {
		String tipoVisita = richiediNuovoTipoVisita(); 
		String titolo = richiediInput("titolo della visita");
		String descrizione = richiediInput("descrizione riassuntiva della visita");
		String puntoIncontro = richiediInput("punto di incontro della visita (locazione geografica)");
		String dataInizio = richiediDataValida("apertura del periodo della visita (dd-mm-yyyy)");
		String dataFine = "";
		do {
			dataFine = richiediDataValida("chiusura del periodo della visita (dd-mm-yyyy)");
			if (Time.comesBefore(dataFine, dataInizio)) view("Non può finire prima che inizi."); 
		} while (Time.comesBefore(dataFine, dataInizio));
		ArrayList<Integer> giorniPrenotabili = richiediGiorniPrenotabili();
		String oraInizio = richiediOraValida("ora d'inizio visita (hh-mm)");
		int durataVisita = richiediInt("durata della visita in minuti (ad esempio 120 sono 120 minuti, quindi 2 ore)");
		boolean ticket = chiediSioNo("è da acquistare o no un biglietto?");
		int minFruitore = richiediNumeroConLimiteInferiore("minimo fruitori per confermare la visita", 0);
		int maxFruitore = richiediNumeroConLimiteInferiore("massimo fruitori per completare la visita", minFruitore);
		ArrayList<String> volontari = richiediVolontari();
		TipoVisitaDTO result = new TipoVisitaDTO(tipoVisita, );
		return result;
	}
	
	public ArrayList<String> richiediVolontari() {
		ArrayList<String> volontari = new ArrayList<>();
        boolean continua = true;
		do {
		    if (chiediSioNo("Vuoi associare un nuovo volontario per questo tipo di visita?")) {
		        boolean volontarioCreato;
		        do {
		        	volontarioCreato = impostaNuovoVolontarioConTipoVisitaScelto(null, volontari); //controller ma non giusto metodo
		        	//volontarioCreato = controllerUtente.canAddVolontario(null volontari)
		        	if (volontarioCreato) {
		        		
		        	}
		        	else view("Non è stato inserito il nuovo volontario, username già in uso.");
		        } while (!volontarioCreato);
	    		continua = chiediSioNo("Vuoi inserire un altro volontario?");
		    } 
		    else {
		    	String volontario = richiediInput("volontario che gestirà la visita");
		    	if (!archivio.checkIfUserExists(volontario)) {
		    		view("L'username inserito non è associato a nessun volontario.");
		    		continua = true;
		    	} else if (!volontari.contains(volontario)) {
		    		volontari.add(volontario);
		    		continua = chiediSioNo("Vuoi inserire un altro volontario?");
		    	} else {
		    		view("Volontario già inserito!");
		    		continua = true;
		    	}
		    }
		} while (continua);
	    return volontari;
	}
	
	
	
	private String richiediNuovoTipoVisita() {
	    String tipoVisita;
	    do {
	        tipoVisita = richiediInput("tag del tipo della visita");
	        if (controllerUtente.checkIfVisitTypeExists(tipoVisita)) {
	            catchEvent(AppEvent.TAG_ALREADY_EXISTS);
	        }
	    } while (controllerUtente.checkIfVisitTypeExists(tipoVisita));
	    return tipoVisita;
	}
	
	public String richiediTipoVisitaEsistente() {
	    String tipo;
	    do {
	        tipo = richiediInput("tag del tipo della visita");
	        if (controllerUtente.checkIfVisitTypeExists(tipo)) {
	            catchEvent(AppEvent.VISITTYPE_NON_EXISTENT);
	        }
	    } while (controllerUtente.checkIfVisitTypeExists(tipo));
	    return tipo;
	}
	
	private ArrayList<Integer> richiediGiorniPrenotabili() {
		ArrayList<Integer> giorni = new ArrayList<>();
	    boolean continua = true;
	    do {
	        int giorno = richiediInt("giorno prenotabile della visita (1-7)");
	        if (giorno < 1 || giorno > 7) {
	            view("Numero inserito non valido, deve essere tra 1 e 7.");
	        } else if (!giorni.contains(giorno)) { //se non contiene
	            giorni.add(giorno); //aggiungi giorno
	            continua = chiediSioNo("Vuoi aggiungere un altro giorno prenotabile?");
	        } else {
	            view("Giorno già inserito!");
	            continua = true;
	        }
	        if (giorni.size() == 7) continua = false; //inseriti TUTTI i giorni della settimana
	    } while (continua);
	    return giorni;
	}
	
}
