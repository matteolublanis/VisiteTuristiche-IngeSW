package main;

import java.lang.reflect.InvocationTargetException;

import archivio.Archivio;
import controller.ControllerArchivio;
import controller.Login;
import controller.ControllerUtente;

public class Main {
	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException {
		/*
		System.out.println("Applicazione avviata...");
		ControllerArchivio gdb = new ControllerArchivio();
		Login gl = new Login(gdb);
		App app = new App(gl); 
		app.start();
		*/
		Archivio a = new Archivio();
		System.out.println(a.usernameEsiste("admin"));

	}
	
	public static void stampa(Object s) {
		System.out.println(s);
  }

}
