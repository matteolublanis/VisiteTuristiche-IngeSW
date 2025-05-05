package archivio.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import archivio.repository.json.ArchivioJSON;

import java.util.ArrayList;
import java.util.List;

import dto.LuogoDTO;
import dto.TipoVisitaDTO;
import utility.CostantiStruttura;
import utility.Credenziali;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ArchivioTest {
	//TODO ArchivioFactory
	AmbitoRepository ambitoRep = new ArchivioJSON(16); 
	UserRepository userRep = new ArchivioJSON(16); 
	VisitsRepository visitRep = new ArchivioJSON(16); 

	
	@BeforeEach
	void setUp() {
		ambitoRep.aggiungiLuogo(new LuogoDTO("Milano", "Milano", "Milano", "Milano"));
		List<Integer> giorni = new ArrayList<>(); giorni.add(1);
		List<Credenziali> volontari = new ArrayList<>(); 
		volontari.add(new Credenziali("volontariosushi", "sushi"));
		volontari.add(new Credenziali("volontariotest1", "test")); 
		volontari.add(new Credenziali("volontariotest2", "test")); 
		TipoVisitaDTO obj1 = new TipoVisitaDTO("sushi", "sushi", "Milano", "sushi", "sushi",
				"08-09-2025", "08-12-2025", giorni, "10:30", 60, true, 5, 20, volontari);
		ambitoRep.tryAggiungiVisite(obj1);
		userRep.associaVolontariATipoVisita(volontari, "sushi");
	}
	
	@Test
	void testA_aggiuntaTipoVisita() { //after setup
		assertTrue(userRep.getTipoUtente("volontariosushi") == CostantiStruttura.VOLONTARIO);
		assertTrue(ambitoRep.checkIfVisitTypeExists("sushi"));
		
	}
	
	@Test
	void testB_RimozioneVolontari() {
		assertTrue(userRep.checkIfUserExists("volontariotest1"));
		assertTrue(userRep.checkIfUserExists("volontariotest2"));
		List<TipoVisitaDTO> j = userRep.getElencoTipiVisiteVolontario("volontariotest1");
        assertTrue(j.get(0).getTag().equals("sushi"));
        userRep.rimuoviVolontario("volontariotest1"); 
        userRep.rimuoviVolontario("volontariotest2");
        assertFalse(userRep.checkIfUserExists("volontariotest1"));
        assertFalse(userRep.checkIfUserExists("volontariotest2"));
        assertFalse(userRep.rimuoviVolontario("volontariotest1")); //non posso rimuove due volte
        
        userRep.rimuoviVolontario("volontariosushi");
		assertFalse(ambitoRep.checkIfVisitTypeExists("sushi"));
		assertFalse(ambitoRep.checkIfPlaceExists("Milano"));
	}
	
	@Test
	void testC_RimozioneTipiVisita() {
		ambitoRep.rimuoviTipo("sushi");
		assertFalse(userRep.checkIfUserExists("volontariotest1"));
		assertFalse(userRep.checkIfUserExists("volontariotest2"));
		assertFalse(userRep.checkIfUserExists("volontariosushi"));
		assertFalse(ambitoRep.checkIfPlaceExists("Milano"));
	}
	
	@Test
	void testD_rimozioneLuogo() { 
		ambitoRep.rimuoviLuogo("Milano");
		assertFalse(ambitoRep.checkIfVisitTypeExists("sushi"));
		assertFalse(userRep.checkIfUserExists("volontariosushi"));
		assertFalse(ambitoRep.checkIfPlaceExists("Milano"));

	}
	


	

}
