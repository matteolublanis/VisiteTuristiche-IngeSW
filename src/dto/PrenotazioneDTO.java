package dto;

public class PrenotazioneDTO {
	
	private int num_da_prenotare = 0;
	private String tag_visita = null;
	private String giorno = null;
	private String codice = null;
	
	public PrenotazioneDTO (String codice, String giorno, String tag_visita, int num_da_prenotare) {
		this.codice = codice;
		this.giorno = giorno;
		this.tag_visita = tag_visita;
		this.num_da_prenotare = num_da_prenotare;
	}
		
	public PrenotazioneDTO (String giorno, String tag_visita, int num_da_prenotare) {
		this.giorno = giorno;
		this.tag_visita = tag_visita;
		this.num_da_prenotare = num_da_prenotare;
	}
	
	public String getGiorno() {
		return giorno;
	}
	public String getTag_visita() {
		return tag_visita;
	}
	public int getNum_da_prenotare() {
		return num_da_prenotare;
	}

	public String getCodice() {
		return codice;
	}
	
	public String toString () {
		String result = ("-----------") + ("\nCodice: " + getCodice() + ", giorno: " + getGiorno()) +
				("\nTag visita: " + getTag_visita());
		return result;
	}
	
}
