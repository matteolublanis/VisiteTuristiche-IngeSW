package main;

import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import controller.Login;
import controller.ControllerUtente;
import utility.MethodName;
import utility.Time;

public class App { 
	
	private Scanner sc = new Scanner(System.in);
	private Login gestoreLogin;
	private ControllerUtente controllerUtente; 
	
	public App(Login gl) {
		this.gestoreLogin = gl;
	}
	
	public void setGu (ControllerUtente gu) {
		this.controllerUtente = gu;
	}
	
	/*
	 * Funzione di start dell'applicazione, esegue il loop principale 
	 */
	public void start() {
		view("Benvenuto!");
		gestoreLogin.avvio(this); //fase login

		do {
			
			if (!scegliAzione()) break;
			
		} while (true); 
		
		view("Arrivederci!");
		
		sc.close();
	}
	
	/*
	 * L'app carica i metodi disponibili per il tipo di utente associato, in base all'input
	 * decide che azione compiere (@see eseguiAzione)
	 */
	private boolean scegliAzione () {
		view("Quale operazione desidera (ESC per uscire)?");
		
		List<Method> azioniDisponibili = controllerUtente.getAzioniDisponibili();
		for (int i = 0; i < azioniDisponibili.size(); i++) {
			try {
				MethodName annotation = azioniDisponibili.get(i).getAnnotation(MethodName.class);
				view((i + 1) + ") " + annotation.value());
			}
			catch(Exception e){
				view("Non sono stati definiti correttamente i metodi, non pagare il team di sviluppo.");
			}
		}
		
		String input = sc.nextLine();
		if (input.equalsIgnoreCase("ESC")) return false;
		else return eseguiAzione(input, azioniDisponibili);
		
	}
	/*
	 * @param String input, List<Method> azioni
	 * @throw Exception e Qui vengono catturate tutte le eccezioni all'interno del programma, da cambiare nella raffinatura
	 */
	private boolean eseguiAzione (String input, List<Method> azioniDisponibili) {
		try {
			int scelta = Integer.parseInt(input);
			if (scelta > 0 && scelta <= azioniDisponibili.size()) {
				Method metodo = azioniDisponibili.get(scelta - 1);
				metodo.invoke(controllerUtente, this);
			} 
			else {
				view("Scelta non valida.");
			}
		} catch (Exception e) { 
			//view("Formato inserito non corretto."); 
			System.err.println(e);
			System.exit(0);
		}
		
		return true;
	}
	
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
	
	public String richiediInput (String s) {
		
		try {
			view("Inserisci " + s + ":");
			s = sc.nextLine();
			return s;
		}
		catch (NoSuchElementException e) {
			view("EOF individuato, arrivederci!");
			System.exit(0);
			return null;
		}

	}
	
	public void view (String msg) {
		if (!msg.equals("")) System.out.println(msg);
	}
	
}
