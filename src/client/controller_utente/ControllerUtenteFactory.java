package client.controller_utente;

import archivio.ArchivioFacade;
import client.app.App;
import utility.CostantiStruttura;

public class ControllerUtenteFactory {
	
	public static ControllerUtente createControllerUtente (int tipo, ArchivioFacade archivio, App a, String connectionCode) {
		
		switch (tipo) {
		case CostantiStruttura.CONFIGURATORE:
			return new HandlerConfiguratore(archivio, a, connectionCode);
		case CostantiStruttura.VOLONTARIO:
			return new HandlerVolontario(archivio, a, connectionCode);
		case CostantiStruttura.FRUITORE:
			return new HandlerFruitore(archivio, a, connectionCode);
		default: 
			return null;
		}
	}
	
}
