package main;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import controller.Login;
import controller.ControllerUtente;
import controller.HandlerConfiguratore;
import utility.CostantiStruttura;
import utility.MethodName;
import utility.ParamName;

public class App { 
	
	private Scanner sc = new Scanner(System.in);
	private Login gl;
	private ControllerUtente gu; 
	private static final String INSERISCI_LE_TUE_CREDENZIALI = "Inserisci le tue credenziali:";
	
	public App(Login gl) {
		this.gl = gl;
	}
	
	public void start() {
		System.out.println(gl.avvio());
		accesso(); 
		if (gu.checkPrimoAccesso()) cambiaCredenziali();
		
		//TODO rivedere
		if (gu.getTipoUtente() == CostantiStruttura.CONFIGURATORE &&
				((HandlerConfiguratore)gu).checkPrimaConfigurazioneArchivio()) {
			configuraArchivio();
		}
		
		do {
			
			if (!scegliAzione()) break;
			
		} while (true); 
		
		stampa("Arrivederci!");
		
		sc.close();
	}
	
	private void configuraArchivio() {
		HandlerConfiguratore g = (HandlerConfiguratore)gu;
		System.out.println("Inserisci nome ambito territoriale:");
		String ambito = sc.nextLine();
		g.impostaAmbitoTerritoriale(ambito);
		System.out.println("Inserisci max prenotazione per fruitore:");
		do {
			if (sc.hasNextInt()) {
				g.modificaMaxPrenotazione(sc.nextInt());
				break;
			}
			else System.out.println("Formato non valido, reinserire:");
		} while (true);
		
		//ciclo aggiunta visite/tipi visite da implementare
		
	}
	
	
	
	private boolean scegliAzione () {
		stampa("Quale operazione desidera (ESC per uscire)?\n");
		
		LinkedList<Method> azioniDisponibili = new LinkedList<>(gu.getAzioniDisponibili());
		for (int i = 0; i < azioniDisponibili.size(); i++) {
			try {
				MethodName annotation = azioniDisponibili.get(i).getAnnotation(MethodName.class);
				stampa((i + 1) + ") " + annotation.value());
			}
			catch(Exception e){
				System.out.println("Non sono stati definiti correttamente i metodi, non pagare il team di sviluppo.");
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
				Parameter[] parameters = metodo.getParameters();
				Object[] args = new Object[parameters.length];
				for (int j = 0; j < parameters.length; j++) {
					args[j] = richiediParametro(parameters[j]);
				}
				System.out.println(metodo.invoke(gu, args));

			} else {
				stampa("Scelta non valida.");
			}
		} catch (Exception e) {
			stampa("Errore: formato invalido");
		}
		
		return true;
	}
	
	private Object richiediParametro(Parameter parametro) {
        ParamName annotation = parametro.getAnnotation(ParamName.class);
		String tipo = parametro.getType().getSimpleName();
		stampa("Inserisci " + annotation.value() + "--> ");
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
	
}
