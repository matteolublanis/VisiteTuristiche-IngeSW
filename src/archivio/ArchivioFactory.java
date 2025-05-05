package archivio;
 
 import utility.CostantiStruttura;
import archivio.model.ControllerArchivio;
import archivio.repository.ArchivioJSON;
 
 public class ArchivioFactory {
 	public static CredenzialiManager createCredenzialiManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new ControllerArchivio(new ArchivioJSON());
 		default:
 			return null;
 		}
 	}
 	
 	public static UserInfoManager createUserInfoManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new ControllerArchivio(new ArchivioJSON());
 		default:
 			return null;
 		}
 	}
 	
 	public static AppManager createAppManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new ControllerArchivio(new ArchivioJSON());
 		default:
 			return null;
 		}
 	}
 	
 	public static AmbitoManager createAmbitoManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new ControllerArchivio(new ArchivioJSON());
 		default:
 			return null;
 		}
 	}
 }