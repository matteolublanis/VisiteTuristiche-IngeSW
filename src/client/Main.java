package client;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.sound.sampled.LineUnavailableException;

/*
 * Progetto di ingegneria del software: Visite Turistiche
 * @author David Brembati (741041), Matteo Lublanis(736418), Singh Sahiljit (740552)
 */
public class Main {
	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException, IOException, LineUnavailableException {
    		App app = new App(); 
    		app.start();
    }
}