package client.app;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import client.controller_utente.*;
import utility.Credenziali;

class FunzioniUtenteTest {
	
	private App app = new AppUI();
	
	private int getIndexAzione(String azione) {
		return app.getGu().getAzioniDisponibiliConNomi().indexOf(azione);
	}
	
	@AfterEach
	void setUp() {
		app.getGl().stopConnection();
		app = new AppUI(); 
	}
	
	@BeforeEach
	void setUpLogin() {
		app.getGl().inviaCredenziali(new Credenziali("conf1", "admin"));
	}
	
	@Test
	void testLogin() {
		assertTrue(app.getGu() instanceof HandlerConfiguratore);
		assertTrue(app.scegliAzione("6")); //true azione eseguita o ignorata
		assertFalse(app.scegliAzione("esc")); //false errore o esc
		assertTrue(app.scegliAzione("25"));
	}
	
	@Test
	void testConfiguratoreView() {
		int i = getIndexAzione("Visualizza visite proposte, complete, confermate, cancellate e effettuate");
		app.scegliAzione(String.valueOf(i));
		i = getIndexAzione("Visualizza elenco tipi visite per luogo");
		app.scegliAzione(String.valueOf(i));
		i = getIndexAzione("Visualizza elenco luoghi visitabili");
		app.scegliAzione(String.valueOf(i));
		i = getIndexAzione("Visualizza lista volontari");
		app.scegliAzione(String.valueOf(i));
	}
	
	@Test
	void testAggiuntaCredenzialiConfiguratore() {
		int i = getIndexAzione("Aggiungi credenziali nuovo configuratore");
		app.scegliAzione(String.valueOf(i));
		assertTrue(app.getGu().checkIfUserExists("conftest"));
	}

}
