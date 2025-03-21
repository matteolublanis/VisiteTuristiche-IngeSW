package main;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.Scanner;
import controller.Login;
import controller.ControllerUtente;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.MethodName;

public class App { 
	
	private Scanner sc = new Scanner(System.in);
	private Login gl;
	private ControllerUtente gu; 
	
	public App(Login gl) {
		this.gl = gl;
	}
	
	public void setGu (ControllerUtente gu) {
		this.gu = gu;
	}
	
	public void start() {
		view("Benvenuto!");
		gl.avvio(this);

		do {
			
			if (!scegliAzione()) break;
			
		} while (true); 
		
		view("Arrivederci!");
		
		sc.close();
	}
	
	private boolean scegliAzione () {
		view("Quale operazione desidera (ESC per uscire)?\n");
		
		LinkedList<Method> azioniDisponibili = gu.getAzioniDisponibili();
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
	
	private boolean eseguiAzione (String input, LinkedList<Method> azioniDisponibili) {
		try {
			int scelta = Integer.parseInt(input);
			if (scelta > 0 && scelta <= azioniDisponibili.size()) {
				Method metodo = azioniDisponibili.get(scelta - 1);
				metodo.invoke(gu, this);

			} else {
				view("Scelta non valida.");
			}
		} catch (Exception e) {
			view(e.toString());
		}
		
		return true;
	}
	
	public String richiediVal (int tipo, String s) {
		view("Inserisci " + s + ":");
		switch (tipo) {
		case CostantiStruttura.STRING:
			s = sc.nextLine();
			return s;
		case CostantiStruttura.INT:
			while (!sc.hasNextInt()) {
				view("Formato non valido, reinserire.");
			}
			return sc.nextLine();
		case CostantiStruttura.BOOLEAN:
			while (!sc.hasNextBoolean()) {
				view("Formato non valido, reinserire.");
			}
			return sc.nextLine();
		default:
			view("Tipo non supportato.");
			return null;
		}
	}
	
	public Credenziali inserisciCredenziali () {
		view("Username:");
		String username = sc.nextLine();
		view("Password:");
		String password = sc.nextLine();
		return new Credenziali(username, password);
	}
	
	public void view (String msg) {
		if (!msg.equals("")) System.out.println(msg);
	}
	
}
