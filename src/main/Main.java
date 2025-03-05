package main;

import controller.GestoreArchivio;

public class Main {
	public static void main (String args[]) {
		System.out.println("Applicazione avviata...");
		GestoreArchivio gdb = new GestoreArchivio();
		AppCntrl app = new AppCntrl(gdb);
		app.start();
	}
}
