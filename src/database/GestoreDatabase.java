package database;

import java.util.HashMap;
import user.Credenziali;
import user.GestoreUtente;
import user.Utente;

public class GestoreDatabase {
	
	private HashMap<GestoreUtente, String> gu = new HashMap<>(); //Un gestore utente è legato ad un suo username in seguito ad un login OR tipo al posto di string
	private static final String CREDENZIALI_CONF_INIZIALE = "PRIMO AVVIO, CREDENZIALI CONFIGURATORE\n"
			+ "Username: admin Password: admin";
	Database d;
	
	public GestoreDatabase () {
		this.d = new Database(); //imposta database col quale interagire
	}
	
	public String comunicaCredenzialiIniziali () {
		if (d.checkPrimoAvvio()) {
			d.setPrimoAvvio();
			return new String(CREDENZIALI_CONF_INIZIALE); //TODO: problema logico, la Stringa dovrebbe essere del DB
		}
		else return "";
	}
	
	public boolean checkCredenziali (Credenziali c, GestoreUtente gu) {
		if (d.credenzialiCorrette(c)) effettuaLogin(gu, c);
		return d.credenzialiCorrette(c);
	}
	
	public void effettuaLogin (GestoreUtente gu, Credenziali c) {
		this.gu.put(gu, c.getUsername());
		gu.setUtente(c.getUsername(), d.getTipoUtente(c));
	}
	
	public boolean checkPrimoAccesso (Utente u) {
		return (d.checkPrimoAccesso(u) == true); //se true è il primo accesso
	}
	
	public void cambiaCredenziali (GestoreUtente gu, Credenziali c) {
		//se l'username associato al client è uguale all'username che mi comunica al quale esso è associato permetto il cambio credenziali
		if (this.gu.get(gu).equals(gu.getUser().getUsername())) { //TODO rivedere, secondo me ha senso ma si può migliorare, si può anche togliere
			d.modificaCredenziali(gu.getUser(), c); //cambia credenziali
			gu.setUsername(c.getUsername()); //in un'ottica di client bisogna comunicare al client l'effettivo successo di cambio nickname
		}
		else System.out.println("Utente non associato a client.");
	}
	
}
