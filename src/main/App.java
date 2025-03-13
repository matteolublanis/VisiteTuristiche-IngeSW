package main;

import java.util.Scanner;
import controller.Login;
import controller.Credenziali;
import controller.ControllerUtente;
import utility.CostantiStruttura;

public class App { 
	
	private Scanner sc = new Scanner(System.in);
	private Login gl;
	private ControllerUtente gu; //swag
	private static final String INSERISCI_LE_TUE_CREDENZIALI = "Inserisci le tue credenziali:";

	
	public App(Login gl) {
		this.gl = gl;
	}
	
	public void start() {
		if (gl.checkPrimoAvvio()) System.out.println(gl.getCredenzialiIniziali());
		accesso(); 
		if (isPrimoAccesso()) cambiaCredenziali(); //TODO confermare logica posizionale
		do {
			
			//do something

		} while (false); 
		
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
		//gu.cambiaCredenziali(c); //deve essere un'azione di utente che comunica col gu
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
