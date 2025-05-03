package dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TipoVisitaDTO implements DTO{
	private String tag = null;
    private String titolo = null;
    private String luogo = null;
    private String descrizione = null;
    private String puntoIncontro = null;
    private String dataInizio = null;
    private String dataFine = null;
    private ArrayList<Integer> giorniPrenotabiliVal = null;
    private String oraInizio = null;
    private int durataVisita = 0;
    private boolean daAcquistare = false;
    private int minFruitore = 0;
    private int maxFruitore = 0;
    private ArrayList<String> volontariVal = null;

    
	public TipoVisitaDTO(String tag, String titolo, String luogo, String descrizione, String puntoIncontro,
			String dataInizio, String dataFine, ArrayList<Integer> giorniPrenotabiliVal, String oraInizio,
			int durataVisita, boolean daAcquistare, int minFruitore, int maxFruitore, ArrayList<String> volontariVal) {
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
		Map<String, List<String>> info = new HashMap<String, List<String>>();
		List<String> code = new ArrayList<String>();
		code.add(tag);
		info.put(titolo, code);
		return info;
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

	public ArrayList<Integer> getGiorniPrenotabiliVal() {
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

	public ArrayList<String> getVolontariVal() {
		return volontariVal;
	}
	
}
