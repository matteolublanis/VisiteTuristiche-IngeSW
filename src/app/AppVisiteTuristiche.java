package app;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.sound.sampled.LineUnavailableException;

import client.app.ViewInterface;
import client.app.AppUI;
import client.app.ControllerMVC;
import client.app.Observable;
import client.login.Login;

public class AppVisiteTuristiche {

	private static final int TIPO_APP = 1; // Definisci il tipo di app appropriato
	
	public static void main(String args[]) throws IllegalAccessException, InvocationTargetException, IOException, LineUnavailableException {
		
		ViewInterface view = new AppUI();
		ControllerMVC controllerMVC = new ControllerMVC(view);
		Login login = new Login(view, TIPO_APP, controllerMVC);
		controllerMVC.setLogin(login);
		if (view instanceof Observable) {
			((Observable) view).attach(controllerMVC);
		}
		view.start();
	}
}