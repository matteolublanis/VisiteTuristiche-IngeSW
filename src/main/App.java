package main;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Scanner;
import controller.Login;
import controller.Credenziali;
import controller.ControllerUtente;
import utility.CostantiStruttura;

public class App { 
	
	private Scanner sc = new Scanner(System.in);
	private Login gl;
	private ControllerUtente gu; 
	private static final String INSERISCI_LE_TUE_CREDENZIALI = "Inserisci le tue credenziali:";
	private ArrayList<Method> azioniDisponibili;

	
	public App(Login gl) {
		this.gl = gl;
	}
	
	public void start() {
		if (gl.checkPrimoAvvio()) System.out.println(gl.getCredenzialiIniziali());
		accesso(); 
		if (isPrimoAccesso()) cambiaCredenziali();
		do {
			stampa("Quale operazione desidera (ESC per uscire)?\n");
			
			for (Method azione:azioniDisponibili) {
				stampa(azione.getName());
				stampa("\n");
			}
			String input = sc.nextLine();
			if (input.equalsIgnoreCase("ESC")) break;
			try {
	            boolean found = false;

	            for (Method method : azioniDisponibili) {
	                if (method.getName().equalsIgnoreCase(input)) {
	                    method.invoke(gu);
	                    found = true;
	                    break;
	                }
	            }

	            if (!found) {
	                System.out.println("Operazione non valida. Riprova.");
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("Errore durante l'esecuzione del metodo.");
	        }
		} while (true); 
		
		stampa("Arrivederci!");
		
		sc.close();
	}
	
	public void accesso () {
		String username;
		String password;
		do {
			System.out.println(INSERISCI_LE_TUE_CREDENZIALI + "\nUsername:");
			username = sc.nextLine();
			System.out.println("Password:");
			password = sc.nextLine();
			gl.inserisciCredenziali(username, password);
			if (gl.checkCredenzialiCorrette()) {
				gu = gl.configureHandlerUtente();
				azioniDisponibili = gu.getAzioniDisponibili();
			}
			else System.out.println("Credenziali errate! Reinserire.");
		} while ((!gl.checkCredenzialiCorrette())); 
		
	}
	
	public boolean isPrimoAccesso () {
		return gu.checkPrimoAccesso();
	}
	
	public void cambiaCredenziali () {
		Credenziali c;
		System.out.println("Cambia le tue credenziali:" + "\nUsername:");
		String username = sc.nextLine();
		System.out.println("Password:");
		String password = sc.nextLine();
		c = new Credenziali(username, password);
		if (gu.cambiaCredenziali(c)) System.out.println("Credenziali cambiate."); 
		else System.out.println("Credenziali non cambiate.");
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
