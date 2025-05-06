package dto;

import java.util.List;
import java.util.Map;

import utility.Credenziali;

public class TipoVisitaDTO implements DTO{
	private String tag = null;
    private String titolo = null;
    private String luogo = null;
    private String descrizione = null;
    private String puntoIncontro = null;
    private String dataInizio = null;
    private String dataFine = null;
    private List<Integer> giorniPrenotabiliVal = null;
    private String oraInizio = null;
    private int durataVisita = 0;
    private boolean daAcquistare = false;
    private int minFruitore = 0;
    private int maxFruitore = 0;
    private List<Credenziali> volontariVal = null;
    private List<String> giorniPrenotabili = null;

    
    
	public TipoVisitaDTO(String tag, String titolo, String luogo, String descrizione, String puntoIncontro,
			String dataInizio, String dataFine, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, List<Credenziali> volontariVal, 
			List<String> giorniPrenotabili) {
		this.tag = tag;
		this.titolo = titolo;
		this.luogo = luogo;
		this.descrizione = descrizione;
		this.puntoIncontro = puntoIncontro;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.oraInizio = oraInizio;
		this.durataVisita = durataVisita;
		this.daAcquistare = daAcquistare;
		this.minFruitore = minFruitore;
		this.maxFruitore = maxFruitore;
		this.volontariVal = volontariVal;
		this.giorniPrenotabili = giorniPrenotabili;
	}
    
	public TipoVisitaDTO(String tag, String titolo, String luogo, String descrizione, String puntoIncontro,
			String dataInizio, String dataFine, List<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, List<Credenziali> volontariVal) {
		this.tag = tag;
		this.titolo = titolo;
		this.luogo = luogo;
		this.descrizione = descrizione;
		this.puntoIncontro = puntoIncontro;
		this.dataInizio = dataInizio;
		this.dataFine = dataFine;
		this.giorniPrenotabiliVal = giorniPrenotabiliVal;
		this.oraInizio = oraInizio;
		this.durataVisita = durataVisita;
		this.daAcquistare = daAcquistare;
		this.minFruitore = minFruitore;
		this.maxFruitore = maxFruitore;
		this.volontariVal = volontariVal;
	}
    
    public TipoVisitaDTO(String tag, String titolo) {
    	this.tag = tag;
    	this.titolo = titolo;
    }
    
	@Override
	public Map<String, List<String>> infoDTO() {
		return null;
	}

	public String getTag() {
		return tag;
	}

	public String getTitolo() {
		return titolo;
	}
	
	public String getLuogo() {
		return luogo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public String getPuntoIncontro() {
		return puntoIncontro;
	}

	public String getDataInizio() {
		return dataInizio;
	}

	public String getDataFine() {
		return dataFine;
	}

	public List<Integer> getGiorniPrenotabiliVal() {
		return giorniPrenotabiliVal;
	}

	public String getOraInizio() {
		return oraInizio;
	}

	public int getDurataVisita() {
		return durataVisita;
	}

	public boolean isDaAcquistare() {
		return daAcquistare;
	}

	public int getMinFruitore() {
		return minFruitore;
	}

	public int getMaxFruitore() {
		return maxFruitore;
	}

	public List<Credenziali> getVolontariVal() {
		return volontariVal;
	}

	public List<String> getGiorniPrenotabili() {
		return giorniPrenotabili;
	}
	
}
