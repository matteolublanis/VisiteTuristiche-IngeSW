package main;

import java.util.Scanner;

import controller.GestoreArchivio;
import controller.GestoreUtente;
import user.Credenziali;

public class AppCntrl { //può essere ampliata per poter gestire le interazioni del db
	
	private Scanner sc = new Scanner(System.in);
	private static final String INSERISCI_LE_TUE_CREDENZIALI = "Inserisci le tue credenziali:";
	private GestoreUtente gu;
	
	public AppCntrl(GestoreArchivio gdb) {
		gu = new GestoreUtente(gdb); //solo il controller utente ha la reference
									 //AppCntrl NON deve interagire con gdb
	}
	
	public void start() {
		if (gu.checkPrimoAvvio()) System.out.println(gu.comunicaCredenzialiIniziali());
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
		} while (!gu.checkCredenzialiCorrette(c)); //login effettuato
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
	
}
