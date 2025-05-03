package dto;

import java.util.List;
import java.util.Map;

public class LuogoDTO implements DTO {
	
	private String titolo;
	private List<String> tipiAssociati; //si può usare TipoVisitaDTO e si dovrebbe
	
	public LuogoDTO(String titolo, List<String> tipiAssociati) {
		this.titolo = titolo;
		this.tipiAssociati = tipiAssociati;
	}
	
	@Override
	public Map<String, List<String>> infoDTO() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitolo() {
		return titolo;
	}

	public List<String> getTipiAssociati() {
		return tipiAssociati;
	}

}
