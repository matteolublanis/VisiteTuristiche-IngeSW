package main;

import database.GestoreDatabase;
import user.GestoreUtente;

public class Main {
	public static void main (String args[]) {
		System.out.println("Applicazione avviata...");
		GestoreDatabase gdb = new GestoreDatabase();
		GestoreUtente gu = new GestoreUtente(gdb);
		gu.start();
	}
}
