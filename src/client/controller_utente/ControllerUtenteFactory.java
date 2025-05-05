package client.controller_utente;

import client.app.App;
import utility.CostantiStruttura;

public class ControllerUtenteFactory {
	
	public static ControllerUtente createControllerUtente (int tipo, int tipoApp, App a, String connectionCode) {
		switch (tipoApp) {
		case CostantiStruttura.STANDALONE:
			switch (tipo) {
			case CostantiStruttura.CONFIGURATORE:
				return new HandlerConfiguratore(a, connectionCode);
			case CostantiStruttura.VOLONTARIO:
				return new HandlerVolontario(a, connectionCode);
			case CostantiStruttura.FRUITORE:
				return new HandlerFruitore(a, connectionCode);
			default: 
				return null;
			}
		}
		
		return null;
	}
	
}
