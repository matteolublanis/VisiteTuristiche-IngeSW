package main;

import java.util.Scanner;

import controller.ControllerUtente;
import user.Credenziali;
import utility.CostantiStruttura;

public class AppCntrl { 
	
	private Scanner sc = new Scanner(System.in);
	private static final String INSERISCI_LE_TUE_CREDENZIALI = "Inserisci le tue credenziali:";
	private ControllerUtente gu;
	
	public AppCntrl(ControllerUtente gu) {
		this.gu = gu; 
		gu.setAppCntrl(this);
	}
	
	public void start() {
		if (gu.checkPrimoAvvio()) {
			gu.comunicaCredenzialiIniziali();
		}
		accesso();
		if (isPrimoAccesso()) cambiaCredenziali();
		//ciclo azioni da implementare
		
		sc.close();
	}
	
	public void accesso () {
		Credenziali c;
		String username;
		do {
			System.out.println(INSERISCI_LE_TUE_CREDENZIALI + "\nUsername:");
			username = sc.nextLine();
			System.out.println("Password:");
			String password = sc.nextLine();
			c = new Credenziali(username, password);
			if (!gu.checkCredenzialiCorrette(c)) System.out.println("Credenziali errate! Reinserire.");
		} while (!gu.checkCredenzialiCorrette(c)); 
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
		gu.cambiaCredenziali(c);
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
