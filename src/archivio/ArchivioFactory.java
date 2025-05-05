package archivio;
 
 import utility.CostantiStruttura;
import archivio.model.Archivio;
import archivio.repository.json.ArchivioJSON;
 
 public class ArchivioFactory {
	
	private static final int RELEASE_DAY = 16;
	private static Archivio archivio = new Archivio(new ArchivioJSON(RELEASE_DAY));
	
	
 	public static CredenzialiManager createCredenzialiManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return archivio;
 		default:
 			return null;
 		}
 	}
 	
 	public static UserInfoManager createUserInfoManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return archivio;
 		default:
 			return null;
 		}
 	}
 	
 	public static AppManager createAppManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return archivio;
 		default:
 			return null;
 		}
 	}
 	
 	public static AmbitoManager createAmbitoManager(int tipo) {
 		switch (tipo) {
 		case CostantiStruttura.STANDALONE:
 			return new Archivio(new ArchivioJSON(RELEASE_DAY));
 		default:
 			return null;
 		}
 	}
 }