package testing;

import static org.junit.jupiter.api.Assertions.*;

import org.json.JSONArray;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.List;

//import static org.junit.jupiter.api.Assertions.*;
import archivio.repository.ArchivioJSON;
import dto.LuogoDTO;
import dto.TipoVisitaDTO;
import utility.CostantiStruttura;
import utility.Credenziali;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ArchivioTest {
	
	ArchivioJSON arch = new ArchivioJSON();
	
	@Test
	void testA_aggiuntaTipoVisita() {
		arch.aggiungiLuogo(new LuogoDTO("Milano", "Milano", "Milano", "Milano"));
		List<Integer> giorni = new ArrayList<>(); giorni.add(1);
		List<Credenziali> volontari = new ArrayList<>(); volontari.add(new Credenziali("volontariosushi", "sushi"));

		TipoVisitaDTO obj1 = new TipoVisitaDTO("sushi", "sushi", "Milano", "sushi", "sushi",
				"08-09-2025", "08-12-2025", giorni, "10:30", 60, true, 5, 20, volontari);
		arch.tryAggiungiVisite(obj1);
		
		assertTrue(arch.getTipoUtente("volontariosushi") == CostantiStruttura.VOLONTARIO);
		assertTrue(arch.checkIfVisitTypeExists("sushi"));
		
	}
	
	@Test
	void testB_rimozioneLuogo() {
		arch.rimuoviLuogo("Milano");
		assertFalse(arch.checkIfVisitTypeExists("sushi"));
		assertFalse(arch.checkIfUserExists("volontariosushi"));
	}
	
	@Test
	void testC_aggiuntaRimozioneVolontari() {
		List<Credenziali> volontari = new ArrayList<>(); 
		volontari.add(new Credenziali("volontariotest1", "test")); volontari.add(new Credenziali("volontariotest2", "test")); 
		arch.associaVolontariATipoVisita(volontari, "visita_capitolium");
		assertTrue(arch.checkIfUserExists("volontariotest1"));
		assertTrue(arch.checkIfUserExists("volontariotest2"));
		JSONArray j = arch.getTipiVisitaOfVolontario("volontariotest1");
        assertTrue(j.get(0).equals("visita_capitolium"));
	}

}
