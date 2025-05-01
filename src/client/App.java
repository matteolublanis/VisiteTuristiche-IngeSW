package client;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import client.controller_utente.ControllerUtente;
import client.login.Login;
import utility.Time;

public class App { 
	
	private Scanner sc = new Scanner(System.in);
	private Login gestoreLogin;
	private ControllerUtente controllerUtente; 
	
	public App() {
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
	public int richiediNumeroConLimite(String msg, int limit) {
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
	
}
