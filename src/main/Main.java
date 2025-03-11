package main;

import utility.Event;
import java.lang.reflect.InvocationTargetException;

import controller.ControllerArchivio;
import controller.ControllerUtente;
import user.Configuratore;
import utility.*;

public class Main {
	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException {
		
		System.out.println("Applicazione avviata...");
		ControllerArchivio gdb = new ControllerArchivio();
		ControllerUtente gu = new ControllerUtente(gdb);
		App app = new App(gu);
		app.start();

		

	}
	
	public static void stampa(Object s) {
		System.out.println(s);
	}
}
