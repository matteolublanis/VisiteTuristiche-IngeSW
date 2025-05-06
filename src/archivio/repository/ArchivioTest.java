package archivio.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.FixMethodOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;

import archivio.model.Archivio;
import archivio.repository.json.ArchivioJSON;

import java.util.ArrayList;
import java.util.List;

import dto.LuogoDTO;
import dto.TipoVisitaDTO;
import utility.CostantiStruttura;
import utility.Credenziali;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ArchivioTest {
	ArchivioJSON archivio; 
	AmbitoRepository ambitoRep;
	UserRepository userRep;
	VisitsRepository visitRep;
	Archivio arch;

	
	@BeforeEach
	void setUp() {
		archivio = new ArchivioJSON(16); 
		ambitoRep = archivio;
		userRep = archivio;
		visitRep = archivio;
		arch = new Archivio(ambitoRep, userRep, visitRep);
		ambitoRep.aggiungiLuogo(new LuogoDTO("milano", "Milano", "Milano", "Milano"));
		List<Integer> giorni = new ArrayList<>(); giorni.add(1);
		List<Credenziali> volontari = new ArrayList<>(); 
		volontari.add(new Credenziali("volontariosushi", "sushi"));
		volontari.add(new Credenziali("volontariotest1", "test")); 
		volontari.add(new Credenziali("volontariotest2", "test")); 
		TipoVisitaDTO obj1 = new TipoVisitaDTO("sushi", "sushi", "milano", "sushi", "sushi",
				"08-09-2025", "08-12-2025", giorni, "10:30", 60, true, 5, 20, volontari);
		ambitoRep.aggiungiTipoVisite(obj1);
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
        assertThrows(NullPointerException.class, () -> userRep.rimuoviVolontario("volontariotest2"));
        userRep.rimuoviVolontario("volontariosushi");
		assertFalse(ambitoRep.checkIfVisitTypeExists("sushi"));
		assertFalse(ambitoRep.checkIfPlaceExists("milano"));
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
		ambitoRep.rimuoviLuogo("milano");
		assertFalse(ambitoRep.checkIfVisitTypeExists("sushi"));
		assertFalse(userRep.checkIfUserExists("volontariosushi"));
		assertFalse(ambitoRep.checkIfPlaceExists("Milano"));

	}
	
	@Test
	void testE_getElencoTipiVisiteLuogo () {
		for (LuogoDTO luogo : ambitoRep.getElencoTipiVisiteLuogo()) {
			System.out.println(luogo.getTitolo());
			for (TipoVisitaDTO tipo : luogo.getTipiVisitaAssociati()) {
				System.out.println(tipo.getTag() + " " + tipo.getGiorniPrenotabiliVal().toString());
			}
			
		}
	}
	
	@Test
	void testF_aggiuntaModelStessoTag () {
		String connectionCode = arch.makeConnection(new Credenziali("conf1", "admin"));
		List<Integer> giorniStessoTag = new ArrayList<>(); giorniStessoTag.add(2);
		List<Credenziali> volontariStessoTag = new ArrayList<>(); 
		volontariStessoTag.add(new Credenziali("volontariosushi", null));
		TipoVisitaDTO tipoStessoTag = new TipoVisitaDTO("sushi", "sushi", "milano", "sushi", "sushi",
				"08-09-2025", "08-12-2025", giorniStessoTag, "10:30", 60, true, 5, 20, volontariStessoTag);
		assertFalse(arch.aggiungiTipoVisite(tipoStessoTag, connectionCode));
	}

	@Test
	void testG_aggiuntaModelStessoGiorno () {
		String connectionCode = arch.makeConnection(new Credenziali("conf1", "admin"));
		List<Integer> giornoStessiGiornoSushi = new ArrayList<>(); giornoStessiGiornoSushi.add(1);
		List<Credenziali> volontari = new ArrayList<>(); 
		volontari.add(new Credenziali("volontariotest", null)); //Non deve entrare nell'aggiunta, quindi non lancia eccezione
		TipoVisitaDTO tipoStessoTag = new TipoVisitaDTO("sushitest", "sushi", "milano", "sushi", "sushi",
				"08-09-2025", "08-12-2025", giornoStessiGiornoSushi, "10:30", 60, true, 5, 20, volontari);
		assertFalse(arch.aggiungiTipoVisite(tipoStessoTag, connectionCode));
	}

	@Test
	void testH_volontarioNonAssociabile() {
		System.out.println("Test G");
		String connectionCode = arch.makeConnection(new Credenziali("conf1", "admin"));
		List<Integer> giornoStessiGiornoSushi = new ArrayList<>(); giornoStessiGiornoSushi.add(3);
		List<Credenziali> volontari = new ArrayList<>(); 
		volontari.add(new Credenziali("volontario2", null)); //Non deve entrare nell'aggiunta, quindi non lancia eccezione
		TipoVisitaDTO tipoStessoTag = new TipoVisitaDTO("sushitest", "sushi", "milano", "sushi", "sushi",
				"08-09-2025", "08-12-2025", giornoStessiGiornoSushi, "10:30", 60, true, 5, 20, volontari);
		assertTrue(arch.aggiungiTipoVisite(tipoStessoTag, connectionCode));
	}
	
	@AfterEach
	void reset() {
		if (ambitoRep.checkIfPlaceExists("milano"))ambitoRep.rimuoviLuogo("milano");
	}

	

}
