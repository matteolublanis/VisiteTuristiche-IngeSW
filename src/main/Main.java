package main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import archivio.Archivio;
import controller.ControllerArchivio;
import controller.Login;


public class Main {
	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException, UnsupportedAudioFileException, IOException, LineUnavailableException {
    		Archivio d = new Archivio();
    		System.out.println("Applicazione avviata...");
    		ControllerArchivio gdb = new ControllerArchivio(d);
    		Login gl = new Login(gdb);
    		App app = new App(gl); 
    		app.start();
    }
}
