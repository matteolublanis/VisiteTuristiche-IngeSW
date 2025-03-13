package main;

import java.lang.reflect.InvocationTargetException;
import controller.ControllerArchivio;
import controller.ControllerLogin;

public class Main {
	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException {
		
		System.out.println("Applicazione avviata...");
		ControllerArchivio gdb = new ControllerArchivio();
		ControllerLogin gl = new ControllerLogin(gdb);
		App app = new App(gl);
		app.start();

	}
	
	public static void stampa(Object s) {
		System.out.println(s);
	}
}
