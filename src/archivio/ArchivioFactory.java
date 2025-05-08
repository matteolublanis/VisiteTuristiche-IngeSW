package archivio;
 
 import utility.CostantiStruttura;
import archivio.model.AccessoManager;
import archivio.model.Archivio;
import archivio.model.UserManager;
import archivio.repository.json.ArchivioJSON;
 
 public class ArchivioFactory {
	
	private static final int RELEASE_DAY = 16; 
	private static  ArchivioJSON archivioJSON = new ArchivioJSON(RELEASE_DAY); 
	private static CredenzialiManager accessoManager = new AccessoManager(archivioJSON, archivioJSON);
	
	/*
	 * Al momento accessoManager fa da SingleTon
	 * Per risolvere, più modi, da capire da capire
	 */
	
 	public static CredenzialiManager createCredenzialiManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return accessoManager;
 		default:
 			return null;
 		}
 	}
 	
 	public static UserInfoManager createUserInfoManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new UserManager(accessoManager, archivioJSON, archivioJSON, archivioJSON);
 		default:
 			return null;
 		}
 	}
 	
 	public static AppManager createAppManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new Archivio(archivioJSON,archivioJSON,archivioJSON, accessoManager, createUserInfoManager(tipo));
 		default:
 			return null;
 		}
 	}
 	
 	public static AmbitoManager createAmbitoManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new Archivio(archivioJSON,archivioJSON,archivioJSON, accessoManager, createUserInfoManager(tipo));
 		default:
 			return null;
 		}
 	}
 }