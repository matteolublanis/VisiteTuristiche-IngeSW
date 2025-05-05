package archivio.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import archivio.CredenzialiManager;
import utility.Credenziali;

public class CredenzialiDatabase implements CredenzialiManager {
	
	private Map<String, String> usernameLinkati = new HashMap<>(); 
	
	@Override
	public String makeConnection(Credenziali c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean impostaCredenzialiNuovoVolontario(String connectionCode, String username, String password,
			List<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Credenziali getCredenzialiIniziali() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean impostaCredenzialiNuovoConfiguratore(String connectionCode, String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkIfUserExists(String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createNewFruitore(Credenziali c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkCredenzialiCorrette(Credenziali c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkPrimoAccesso(String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cambiaCredenziali(String connectionCode, Credenziali c) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected String getLinkedUsername (String connectionCode) { return usernameLinkati.get(connectionCode); } 

}
