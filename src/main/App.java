package main;

import java.lang.reflect.Method;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import controller.Login;
import controller.ControllerUtente;
import utility.MethodName;

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
	
	public void start() {
		view("Benvenuto!");
		gestoreLogin.avvio(this);

		do {
			
			if (!scegliAzione()) break;
			
		} while (true); 
		
		view("Arrivederci!");
		
		sc.close();
	}
	
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
	
	private boolean eseguiAzione (String input, List<Method> azioniDisponibili) {
		try {
			int scelta = Integer.parseInt(input);
			if (scelta > 0 && scelta <= azioniDisponibili.size()) {
				Method metodo = azioniDisponibili.get(scelta - 1);
				metodo.invoke(controllerUtente, this);

			} else {
				view("Scelta non valida.");
			}
		} catch (Exception e) { //TODO migliorare gestione eccezioni
			view("Formato inserito non corretto."); //Tutte le eccezioni vengono catturate qua quando si esegue un metodo, difficile da debuggare
			System.err.println(e);
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
			//premendo CTRL Z si manda un EOF, non ho trovato come gestirlo
			throw e;
		}

	}
	
	public void view (String msg) {
		if (!msg.equals("")) System.out.println(msg);
	}
	
}
