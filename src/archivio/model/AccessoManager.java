package archivio.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import archivio.CredenzialiManager;
import archivio.repository.AmbitoRepository;
import archivio.repository.UserRepository;
import utility.CodiceGenerator;
import utility.CostantiStruttura;
import utility.Credenziali;

public class AccessoManager implements CredenzialiManager {
	
	private Map<String, String> usernameLinkati = new HashMap<>(); 
	private AmbitoRepository ambitoRep;
	private UserRepository userRep;
	
	public AccessoManager(AmbitoRepository ambitoRep, UserRepository userRep) {
		this.ambitoRep = ambitoRep;
		this.userRep = userRep;
	}

	@Override
	public String getLinkedUsername (String connectionCode) { return usernameLinkati.get(connectionCode); } 
	
	@Override	
	public String makeConnection(Credenziali c) {
		if (checkIfUsernameExists(c.getUsername())) if (!checkCredenzialiCorrette(c)) return null;
		if (!checkIfUsernameExists(c.getUsername())) {
			createNewFruitore(c);
		}
		if (usernameLinkati.values().contains(c.getUsername())) return null;
		else {
			while (true) {
				String connectionCode = CodiceGenerator.generateCode();
				if (!usernameLinkati.containsKey(connectionCode)) {
					usernameLinkati.put(connectionCode, c.getUsername());
					return connectionCode;
				}
			}

		}
	}

	@Override
	public boolean checkPrimoAvvio () { //OK
		return ambitoRep.checkPrimoAvvio();
	}

	@Override
	public boolean impostaCredenzialiNuovoVolontario (String connectionCode, String username, String password, List<String> tipi_visiteVal, boolean tipiVisitaNecessario) {
		if (userRep.getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE
				&& !checkIfUsernameExists(username)) {
			if (!checkIfTypeExistsInList(tipi_visiteVal)) return false;
		    if (tipiVisitaNecessario && tipi_visiteVal.size() == 0) return false;
			return userRep.impostaCredenzialiNuovoVolontario(username, password, tipi_visiteVal, tipiVisitaNecessario);

		}
		else return false;
	}
	private boolean checkIfTypeExistsInList (List<String> tipiVisitaVal) {
    	for (String tipo : tipiVisitaVal) {
	    	if (!(ambitoRep.checkIfVisitTypeExists(tipo)) && !tipo.equals("")) return false;
	    }
    	return true;
	}

	@Override
	public Credenziali getCredenzialiIniziali () {
		if (checkPrimoAvvio()) {
			ambitoRep.setPrimoAvvio();
			return (userRep.getCredenzialiConfIniziale()); 
		}
		else return null; 
	}

	@Override
	public boolean impostaCredenzialiNuovoConfiguratore(String connectionCode, String username, String password) {
		if (userRep.getTipoUtente(getLinkedUsername(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return userRep.impostaCredenzialiNuovoConfiguratore(username, password);
		else return false;
	}

	@Override
	public boolean checkIfUsernameExists (String username) {
		return userRep.checkIfUserExists(username);
	}

	private boolean createNewFruitore(Credenziali c) {
		return userRep.impostaCredenzialiNuovoFruitore(c.getUsername(), c.getPassword());
	}

	@Override
	public boolean checkCredenzialiCorrette(Credenziali c) {
		if (!userRep.checkIfUserExists(c.getUsername())) return false;
		return userRep.checkCredenzialiCorrette(c);
	}

	@Override
	public boolean checkPrimoAccesso (String connectionCode) {
		if (!checkIfUsernameExists(getLinkedUsername(connectionCode))) return false;
		else return (userRep.checkPrimoAccesso(getLinkedUsername(connectionCode))); 
	}

	@Override
	public boolean cambiaCredenziali (String connectionCode, Credenziali c) {
		if (!checkIfUsernameExists(getLinkedUsername(connectionCode))) return false; //non necessario?
		if (checkIfUsernameExists(c.getUsername())) return false;
		if (userRep.modificaCredenziali(usernameLinkati.get(connectionCode), c)) { 
			userRep.primoAccessoEseguito(c.getUsername());
			usernameLinkati.put(connectionCode, c.getUsername());
			return true;
		}	
		return false;
	}

	@Override
	public int getTipoLinkato(String connectionCode) {
		return userRep.getTipoUtente(getLinkedUsername(connectionCode));
	}

	@Override
	public void stopConnection(String connectionCode) {
		usernameLinkati.remove(connectionCode);
		
	}

}
