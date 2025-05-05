package archivio.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import java.util.ArrayList;
import java.util.List;

import dto.LuogoDTO;
import dto.TipoVisitaDTO;
import utility.CostantiStruttura;
import utility.Credenziali;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ArchivioTest {
	
	ArchivioJSON arch = new ArchivioJSON(); //TODO ArchivioFactory
	
	@BeforeEach
	void setUp() {
		arch.aggiungiLuogo(new LuogoDTO("Milano", "Milano", "Milano", "Milano"));
		List<Integer> giorni = new ArrayList<>(); giorni.add(1);
		List<Credenziali> volontari = new ArrayList<>(); 
		volontari.add(new Credenziali("volontariosushi", "sushi"));
		volontari.add(new Credenziali("volontariotest1", "test")); 
		volontari.add(new Credenziali("volontariotest2", "test")); 
		TipoVisitaDTO obj1 = new TipoVisitaDTO("sushi", "sushi", "Milano", "sushi", "sushi",
				"08-09-2025", "08-12-2025", giorni, "10:30", 60, true, 5, 20, volontari);
		arch.tryAggiungiVisite(obj1);
		arch.associaVolontariATipoVisita(volontari, "sushi");
	}
	
	@Test
	void testA_aggiuntaTipoVisita() { //after setup
		assertTrue(arch.getTipoUtente("volontariosushi") == CostantiStruttura.VOLONTARIO);
		assertTrue(arch.checkIfVisitTypeExists("sushi"));
		
	}
	
	@Test
	void testB_RimozioneVolontari() {
		assertTrue(arch.checkIfUserExists("volontariotest1"));
		assertTrue(arch.checkIfUserExists("volontariotest2"));
		List<TipoVisitaDTO> j = arch.getElencoTipiVisiteVolontario("volontariotest1");
        assertTrue(j.get(0).getTag().equals("sushi"));
        arch.rimuoviVolontario("volontariotest1"); 
        arch.rimuoviVolontario("volontariotest2");
        assertFalse(arch.checkIfUserExists("volontariotest1"));
        assertFalse(arch.checkIfUserExists("volontariotest2"));
        assertFalse(arch.rimuoviVolontario("volontariotest1")); //non posso rimuove due volte
        
        arch.rimuoviVolontario("volontariosushi");
		assertFalse(arch.checkIfVisitTypeExists("sushi"));
		assertFalse(arch.checkIfPlaceExists("Milano"));
	}
	
	@Test
	void testC_RimozioneTipiVisita() {
		arch.rimuoviTipo("sushi");
		assertFalse(arch.checkIfUserExists("volontariotest1"));
		assertFalse(arch.checkIfUserExists("volontariotest2"));
		assertFalse(arch.checkIfUserExists("volontariosushi"));
		assertFalse(arch.checkIfPlaceExists("Milano"));
	}
	
	@Test
	void testD_rimozioneLuogo() { 
		arch.rimuoviLuogo("Milano");
		assertFalse(arch.checkIfVisitTypeExists("sushi"));
		assertFalse(arch.checkIfUserExists("volontariosushi"));
		assertFalse(arch.checkIfPlaceExists("Milano"));

	}
	


	

}
