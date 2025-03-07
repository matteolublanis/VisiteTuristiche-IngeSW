package main;

import java.lang.reflect.InvocationTargetException;

import controller.ControllerArchivio;
import controller.ControllerUtente;
import user.Configuratore;

public class Main {
	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException {
		System.out.println("Applicazione avviata...");
		ControllerArchivio gdb = new ControllerArchivio();
		ControllerUtente gu = new ControllerUtente(gdb);
		AppCntrl app = new AppCntrl(gu);
		app.start();
		Configuratore c = new Configuratore("user", 3, gu);
		c.getElencoTipiVisiteLuogo();
	}
}
