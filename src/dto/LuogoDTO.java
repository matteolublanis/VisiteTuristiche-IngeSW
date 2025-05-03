package dto;

import java.util.List;
import java.util.Map;

public class LuogoDTO implements DTO {
	private String tag;
	private String descrizione;
	private String collocazione;
	private String titolo;
	private List<String> tipiAssociati; //si può usare TipoVisitaDTO e si dovrebbe
	
	public LuogoDTO(String titolo, List<String> tipiAssociati) {
		this.titolo = titolo;
		this.tipiAssociati = tipiAssociati;
	}
	
	public LuogoDTO(String tag, String nome, String descrizione, String collocazione) {
		this.tag = tag;
		titolo = nome;
		this.descrizione = descrizione;
		this.collocazione = collocazione;
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
	
	public String getTag() {
		return tag;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public String getCollocazione() {
		return collocazione;
	}
}
