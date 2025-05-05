package archivio;

import java.util.List;

import utility.Credenziali;

public interface CredenzialiManager {
	
	public String makeConnection(Credenziali c);
	
	public boolean impostaCredenzialiNuovoVolontario (String connectionCode, String username, String password, List<String> tipi_visiteVal, boolean tipiVisitaNecessario);
	
	public Credenziali getCredenzialiIniziali ();
	
	public boolean impostaCredenzialiNuovoConfiguratore(String connectionCode, String username, String password);
	
	public boolean checkIfUserExists (String username);
	
	public boolean createNewFruitore(Credenziali c);
	
	public boolean checkCredenzialiCorrette (Credenziali c);
	
	public boolean checkPrimoAccesso (String connectionCode);
	
	public boolean cambiaCredenziali (String connectionCode, Credenziali c);

}
