package archivio;

import java.util.List;

import utility.Credenziali;

public interface CredenzialiManager {
	
	public String makeConnection(Credenziali c);
	
	public boolean checkPrimoAvvio ();
	
	public int getTipoLinkato(String connectionCode);
	
	public String getLinkedUsername (String connectionCode);
	
	public boolean impostaCredenzialiNuovoVolontario (String connectionCode, String username, String password, List<String> tipi_visiteVal, boolean tipiVisitaNecessario);
	
	public Credenziali getCredenzialiIniziali ();
	
	public boolean impostaCredenzialiNuovoConfiguratore(String connectionCode, String username, String password);
	
	public boolean checkIfUsernameExists (String username);
		
	public boolean checkCredenzialiCorrette (Credenziali c);
	
	public boolean checkPrimoAccesso (String connectionCode);
	
	public boolean cambiaCredenziali (String connectionCode, Credenziali c);

}
