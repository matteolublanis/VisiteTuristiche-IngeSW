package main;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Scanner;
import controller.Login;
import controller.ControllerUtente;
import utility.CostantiStruttura;

public class App { 
	
	private Scanner sc = new Scanner(System.in);
	private Login gl;
	private ControllerUtente gu; 
	private static final String INSERISCI_LE_TUE_CREDENZIALI = "Inserisci le tue credenziali:";
	
	public App(Login gl) {
		this.gl = gl;
	}
	
	public void start() {
		if (gl.checkPrimoAvvio()) System.out.println(gl.getCredenzialiIniziali());
		accesso(); 
		if (gu.checkPrimoAccesso()) cambiaCredenziali();
		do {
			stampa("Quale operazione desidera (ESC per uscire)?\n");
			
			ArrayList<Method> azioniDisponibili = gu.getAzioniDisponibili();
			for (int i = 0; i < azioniDisponibili.size(); i++) {
				stampa((i + 1) + ") " + azioniDisponibili.get(i).getName());
			}
			
			String input = sc.nextLine();
			if (input.equalsIgnoreCase("ESC")) break;
			
			try {
				int scelta = Integer.parseInt(input);
				if (scelta > 0 && scelta <= azioniDisponibili.size()) {
					Method metodo = azioniDisponibili.get(scelta - 1);
					
					Parameter[] parameters = metodo.getParameters();
					
					Object[] args = new Object[parameters.length];

					for (int j = 0; j < parameters.length; j++) {
						args[j] = richiediParametro(parameters[j]);
					}

					metodo.invoke(gu, args);

				} else {
					stampa("Scelta non valida.");
				}
			} catch (Exception e) {
				stampa("Errore: formato invalido");
			}
			
		} while (true); 
		
		stampa("Arrivederci!");
		
		sc.close();
	}
	
	private Object richiediParametro(Parameter parametro) {
		String tipo = parametro.getType().getSimpleName();
		stampa("Inserisci " + parametro.getName() + "--> ");
		String input = sc.nextLine();

		switch (tipo) {
			case "String":
				return input;
			case "int":
				return Integer.parseInt(input);
			case "boolean":
				return Boolean.parseBoolean(input);
			default:
				stampa("Tipo non supportato.");
				return null;
		}
	}
	
	public void accesso () {
		do {
			System.out.println(INSERISCI_LE_TUE_CREDENZIALI + "\nUsername:");
			String username = sc.nextLine();
			System.out.println("Password:");
			String password = sc.nextLine();
			gu = gl.accesso(username, password);
			if (gu == null) {
				System.out.println("Credenziali errate! Reinserire.");
			}
		} while (gu == null); 
		
	}
	
	public void cambiaCredenziali () {
		System.out.println("Cambia le tue credenziali:" + "\n");
 		do {
 			System.out.println("Username:");
 			String username = sc.nextLine();
 			System.out.println("Password:");
 			String password = sc.nextLine();
 			if (gu.cambiaCredenziali(username, password)) {
 				System.out.println("Credenziali cambiate.");
 				break;
 			}
 			else System.out.println("Credenziali non cambiate. Reinserire nuove credenziali (username già in uso).");
 		} while (true);
	}
	
	public void stampa (String msg) {
		if (!msg.equals("")) System.out.println(msg);
	}
	
	public Object richiediVal (String msg, int tipo) {
		
		switch (tipo) {
		case CostantiStruttura.INT:
	        System.out.println(msg);

			 while (!sc.hasNextInt()){
			        sc.nextLine();
			        if (!sc.hasNextInt()) System.out.println("Formato non corretto, reinserire.");
			}
			 return sc.nextInt();
		case CostantiStruttura.STRING:
			System.out.println(msg);
			return sc.nextLine();
		case CostantiStruttura.BOOLEAN:
			System.out.println(msg);
			do {
				sc.nextLine(); 
				if (!sc.hasNextBoolean()) System.out.println("Formato non corretto, reinserire.");
			} while (!sc.hasNextBoolean()); //TODO da cambiare
		}
		
		return null;

	}
	
}
