package user;

import java.util.Scanner;

import database.GestoreDatabase;

public class GestoreUtente {
	
	private static final String INSERISCI_LE_TUE_CREDENZIALI = "Inserisci le tue credenziali:";
	private GestoreDatabase gdb;
	private int tipo = -1; 
	private Utente user;
	
	public GestoreUtente (GestoreDatabase gdb) {
		this.gdb = gdb;	
	}
	
	//TODO: ho visto AppCntrl, sarà da gestire il tutto con quello, al momento start serve per il testing delle fasi
	public void start () {
		String primoAvvio = gdb.comunicaCredenzialiIniziali();
		if (!primoAvvio.equals("")) {
			System.out.println(primoAvvio);
		}
		inserisciCredenziali(); //effettua login una volta terminato
		if (gdb.checkPrimoAccesso(user)) {
			cambiaCredenziali();
			System.out.println("Credenziali cambiate, benvenuto " + user.getUsername() + ".\n");
		}
		else System.out.println("Benvenuto, " + user.getUsername() + ".\n");
		//TODO da ciclare azioni NON ESCLUSIVO AL CONFIGURATORE, PROBABILE SWITCH SUL TIPO DI UTENTE PER LE AZIONI
	}
	
	public void inserisciCredenziali () {
		Scanner sc = new Scanner(System.in);
		Credenziali c;
		String username;
		do {
			System.out.println(INSERISCI_LE_TUE_CREDENZIALI + "\nUsername:");
			username = sc.nextLine();
			System.out.println("Password:");
			String password = sc.nextLine();
			c = new Credenziali(username, password);
			if (!gdb.checkCredenziali(c, this)) System.out.println("Credenziali errate! Reinserire.");
		} while (!gdb.checkCredenziali(c, this));
	}
	
	public void setUtente (String username, int tipo) {
		this.tipo = tipo;
		switch ((int)tipo) {
		case 1:
			user = new Configuratore(username);
			break;
		case 2:
			user = new Volontario(username);
			break;
		case 3:
			user = new Fruitore(username);
			break;
		}
	}
	
	public void setTipoUtente (int tipo) {
		this.tipo = tipo;
	}
	
	//in un'ottica futura questo metodo prenderà in ingresso da altri Stream i dati
	public void cambiaCredenziali () { 
		Scanner sc = new Scanner(System.in);
		Credenziali c;
		
		System.out.println("Cambia le tue credenziali:" + "\nUsername:");
		String username = sc.nextLine();
		System.out.println("Password:");
		String password = sc.nextLine();
		c = new Credenziali(username, password);
		gdb.cambiaCredenziali(this, c);
	}
	
	public void setUsername(String username) { 
		user.setUsername(username);
	}
	
	public Utente getUser() {
		return user;
	}
}
