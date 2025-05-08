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
import dto.DataDisponibilitaDTO;
import dto.LuogoDTO;
import dto.PrenotazioneDTO;
import dto.TipoVisitaDTO;
import dto.UserDTO;
import dto.VisitaDTO;
import utility.Credenziali;
import utility.Time;

public class AppUI implements App{ 
	
	private Scanner sc = new Scanner(System.in);
	private Login gestoreLogin;
	private ControllerUtente controllerUtente; 
	
	public AppUI() {
		this.gestoreLogin = new Login(this, 0);
	}
	
	public Login getGl() {
		return gestoreLogin;
	}
	
	//Precondizione: gu != null
	public ControllerUtente getGu () {
		return controllerUtente;
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
		gestoreLogin.stopConnection();
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
		try {
			int i = Integer.parseInt(input);
			return controllerUtente.eseguiAzione(String.valueOf(i - 1));
		}
		catch (NumberFormatException e) {
			return controllerUtente.eseguiAzione(input);
		}

	}
	
	public boolean scegliAzione(String azione) {
		return controllerUtente.eseguiAzione(azione);
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
	public TipoVisitaDTO richiediTipoVisita(String luogo) {
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
		ArrayList<Credenziali> volontari = richiediVolontari();
		TipoVisitaDTO result = new TipoVisitaDTO(tipoVisita, titolo, luogo, descrizione, puntoIncontro, dataInizio,
				dataFine, giorniPrenotabili, oraInizio, durataVisita, ticket, minFruitore,
				maxFruitore, volontari);
		return result;
	}
	
	public ArrayList<Credenziali> richiediVolontari() {
		ArrayList<Credenziali> volontari = new ArrayList<>();
		boolean continua = true;
		do {
		    if (chiediSioNo("Vuoi associare un nuovo volontario per questo tipo di visita?")) {
		        do {
		        	view("Inserisci le credenziali del nuovo volontario.");
		        	Credenziali c = richiediCredenziali();		        	
		        	if (!controllerUtente.checkIfUserExists(c.getUsername())) {
		        		volontari.add(c);
		        		break;
		        	}
		        	else view("Non è stato inserito il nuovo volontario, username già in uso.");
		        } while (true);
		    } 
		    else {
		    	String volontario = richiediInput("volontario che gestirà la visita");
		    	if (!controllerUtente.checkIfUserExists(volontario)) {
		    		view("L'username inserito non è associato a nessun volontario.");
		    	} else {
		    		boolean add = true;
		    		for (Credenziali v : volontari) if (v.getUsername() == volontario) {
		    			view("Volontario già inserito!");
		    			add = false;
		    		}
		    		if (add) volontari.add(new Credenziali(volontario, null));
		    	} 
		    }
		    continua = (volontari.size() == 0);
		    if (!continua) continua = chiediSioNo("Vuoi inserire un altro volontario?");
		} while (continua);
	    return volontari;
	}
	
	public LuogoDTO richiediLuogo () {
		String tag = richiediInput("tag del luogo");
		String nome = richiediInput("nome del luogo");
		String descrizione = richiediInput("descrizione del luogo");
		String collocazione = richiediInput("collocazione del luogo");
		return new LuogoDTO(tag, nome, descrizione, collocazione);
	}
	
	@Override
	public String richiediVisitaEsistente() {
	    String tipo;
	    do {
	        tipo = richiediInput("tag della visita da associare");
	        if (!controllerUtente.checkIfVisitTypeExists(tipo)) {
	            view("Non esiste il tipo inserito, reinserisci i dati.");
	        }
	    } while (!controllerUtente.checkIfVisitTypeExists(tipo)); 
	    return tipo;
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
	        if (!controllerUtente.checkIfVisitTypeExists(tipo)) {
	            catchEvent(AppEvent.VISITTYPE_NON_EXISTENT);
	        }
	        else break;
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
	public void viewListDataDisponibilitaDTO(List<DataDisponibilitaDTO> list) {
		for (DataDisponibilitaDTO data : list) {
			view("Giorni " + data.getTag() + ": " + data.getGiorni().toString());
		}
		
	}

	@Override
	public void viewListLuogoDTO(List<LuogoDTO> list) {
		for (LuogoDTO luogo : list) {
			if (luogo.getTipiAssociati() == null) {
				view(luogo.getTitolo());
			}
			else {
				view(luogo.getTitolo() + ": " + luogo.getTipiAssociati().toString());
			}
		}
		
	}

	@Override
	public void viewListPrenotazioneDTO(List<PrenotazioneDTO> list) {
		for (PrenotazioneDTO prenotazione : list) {
			view("Prenotazione " + prenotazione.getCodice() + ": " + prenotazione.getGiorno() + ", " + prenotazione.getTag_visita());
			view("N. iscritti: " + prenotazione.getNum_da_prenotare());
		}
		
	}

	@Override
	public void viewListTipoVisitaDTO(List<TipoVisitaDTO> list) {
		for (TipoVisitaDTO tipo : list) {
			view(tipo.getTag() + " " + tipo.getTitolo());
		}
		
	}

	@Override
	public void viewListUserDTO(List<UserDTO> list) {
		for (UserDTO user : list) {
			view(user.toString()); //TODO al momento è usato come soluzione momentanea, si cambia
		}
		
	}

	@Override
	public void viewListVisitaDTO(List<VisitaDTO> list) {
		for (VisitaDTO visita : list) {
			if (!(visita.getStato().equals("cancellata"))) {
				if (visita.getDaAcquistare() == null) { //non impostato per configuratore
					String result = ("-----------") + ("\nTitolo: " +  visita.getTitolo()) + 
							("\nGiorno: " +  visita.getGiorno()) + 
							("\nLuogo: " +  visita.getLuogo()) + 
							("\nStato: " +  visita.getStato());
					view(result);
				}
				else {
					String result = ("-----------") + ("\nTitolo: " +  visita.getTitolo()) + 
							("\nDescrizione: " +  visita.getDescrizione())
							+ ("\nPunto d'incontro: " +  visita.getPuntoIncontro()) + 
							("\nGiorno: " +  visita.getGiorno()) + 
							("\nOra d'inizio: " +  visita.getOraInizio())
							+ ("\nDa acquistare: " +  visita.getDaAcquistare()) + 
							("\nStato: " +  visita.getStato()) + ("\nTag: " +  visita.getTag());
					if (visita.getPrenotazioni() != null) { //impostato da volontario
						String codiciPrenotazioni = "";
						for (int i = 0 ; i < visita.getPrenotazioni().size() ; i++) {
							codiciPrenotazioni += "\nCodice: " + visita.getPrenotazioni().get(i).getCodice() + 
									", n. iscritti:" + visita.getPrenotazioni().get(i).getNum_da_prenotare();
						}
						result += codiciPrenotazioni;
					}
					view(result);
				}
			}
			else {
				String result =  ("-----------") + ("\nTitolo: " +  visita.getTitolo()) + ("\nGiorno mancato svolgimento: " +  visita.getGiorno())
						+ ("Stato: " +  visita.getStato());
				view(result);
			}
		}
	}
	
}
