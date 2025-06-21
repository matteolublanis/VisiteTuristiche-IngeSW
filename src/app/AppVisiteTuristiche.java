package app;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.LineUnavailableException;

import client.app.App;
import client.app.AppUI;

public class AppVisiteTuristiche {

	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException, IOException, LineUnavailableException {
		App app = new AppUI(); 
		app.start();
	}

}
