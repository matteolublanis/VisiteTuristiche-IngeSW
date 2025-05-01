package archivio;
 
 import utility.CostantiStruttura;
 import controller.ControllerArchivio;
 
 public class ArchivioFactory {
 	public static ArchivioFacade createArchivio(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new ControllerArchivio(new ArchivioJSON());
 		default:
 			return null;
 		}
 	}
 }