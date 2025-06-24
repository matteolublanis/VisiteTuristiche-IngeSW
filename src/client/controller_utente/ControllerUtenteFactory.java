package client.controller_utente;

import client.app.ViewInterface;
import utility.CostantiStruttura;

public class ControllerUtenteFactory {
	
	public static ControllerUtente createControllerUtente (int tipo, int tipoApp, ViewInterface a, String connectionCode) {
		switch (tipoApp) {
		case CostantiStruttura.STANDALONE:
			switch (tipo) {
			case CostantiStruttura.CONFIGURATORE:
				return new HandlerConfiguratore(a, connectionCode, tipoApp);
			case CostantiStruttura.VOLONTARIO:
				return new HandlerVolontario(a, connectionCode, tipoApp);
			case CostantiStruttura.FRUITORE:
				return new HandlerFruitore(a, connectionCode, tipoApp);
			default: 
				return null;
			}
		}
		
		return null;
	}
	
}
