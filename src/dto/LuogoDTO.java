package dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LuogoDTO implements DTO {
	private String tag;
	private String descrizione;
	private String collocazione;
	private String titolo;
	private List<String> tagTipiAssociati; //si può usare TipoVisitaDTO e si dovrebbe
	private List<TipoVisitaDTO> tipiVisitaAssociati;
	
	public LuogoDTO(String tag, String titolo, List<String> tipiAssociati) {
		this.tag = tag;
		this.titolo = titolo;
		this.tagTipiAssociati = tipiAssociati;
	}
	
	public LuogoDTO(String titolo, List<String> tipiAssociati) {
		this.titolo = titolo;
		this.tagTipiAssociati = tipiAssociati;
	}
	
	public LuogoDTO(String tag, String titolo, String descrizione, String collocazione) {
		this.tag = tag;
		this.titolo = titolo;
		this.descrizione = descrizione;
		this.collocazione = collocazione;
	}
	
	@Override
	public Map<String, List<String>> infoDTO() {
		Map<String, List<String>> infoDTO = new HashMap<>();
		return infoDTO;
	}

	public String getTitolo() {
		return titolo;
	}

	public List<String> getTipiAssociati() {
		return tagTipiAssociati;
	}
	
	public String getTag() {
		return tag;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public String getCollocazione() {
		return collocazione;
	}

	public List<TipoVisitaDTO> getTipiVisitaAssociati() {
		return tipiVisitaAssociati;
	}

	public void setTipiVisitaAssociati(List<TipoVisitaDTO> tipiVisitaAssociati) {
		this.tipiVisitaAssociati = tipiVisitaAssociati;
	}
}
