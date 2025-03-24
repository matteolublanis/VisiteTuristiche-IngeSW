package main;

import java.lang.reflect.InvocationTargetException;

import archivio.Archivio;
import controller.ControllerArchivio;
import controller.Login;
import utility.Time;


public class Main {
	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException {
		Archivio d = new Archivio();
		System.out.println("Applicazione avviata...");
		ControllerArchivio gdb = new ControllerArchivio(d);
		Login gl = new Login(gdb);
		App app = new App(gl); 
		app.start();
	}
}
