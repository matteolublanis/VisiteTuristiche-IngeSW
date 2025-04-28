package dto;

import java.util.List;

public class VisitaDTO {
	private String tag = null;
    private String titolo = null;
    private String descrizione = null;
    private String puntoIncontro = null;
    private String giorno = null;
    private String oraInizio = null;
    private String daAcquistare = null;
    private String stato = null;
    private String luogo = null;
    private List<PrenotazioneDTO> prenotazioni = null;
    
    
    public VisitaDTO(String titolo, String giorno, String luogo, String stato) {
    	this.titolo = titolo;
    	this.giorno = giorno;
    	this.luogo = luogo;
    	this.stato = stato;
    }
    
    public VisitaDTO(String tag, String titolo, String descrizione, String puntoIncontro, String giorno, String oraInizio, boolean daAcquistare, String stato, List<PrenotazioneDTO> prenotazioni) {
    	this.tag = tag;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.puntoIncontro = puntoIncontro;
        this.giorno = giorno;
        this.oraInizio = oraInizio;
        this.daAcquistare = (daAcquistare ? "si" : "no");
        this.stato = stato;
        this.prenotazioni = prenotazioni;
    }
    
    public VisitaDTO(String tag, String titolo, String descrizione, String puntoIncontro, String giorno, String oraInizio, boolean daAcquistare, String stato) {
    	this.tag = tag;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.puntoIncontro = puntoIncontro;
        this.giorno = giorno;
        this.oraInizio = oraInizio;
        this.daAcquistare = (daAcquistare ? "si" : "no");
        this.stato = stato;
    }
    
    public String getTitolo() { return titolo; }
    public String getDescrizione() { return descrizione; }
    public String getPuntoIncontro() { return puntoIncontro; }
    public String getGiorno() { return giorno; }
    public String getOraInizio() { return oraInizio; }
    public String getDaAcquistare() { return daAcquistare; }
    public String getStato() { return stato; }
    public String getLuogo() { return luogo; }

	public String getTag() {
		return tag;
	}

	public List<PrenotazioneDTO> getPrenotazioni() {
		return prenotazioni;
	}
	
	public String toString () {
		if (!getStato().equals("cancellata")) {
			if (getDaAcquistare() == null) { //non impostato per configuratore
				String result = ("-----------") + ("\nTitolo: " +  getTitolo()) + ("\nGiorno: " +  getGiorno()) + 
						("\nLuogo: " +  getLuogo()) + ("\nStato: " +  getStato());
				return result;
			}
			else {
				String result = ("-----------") + ("\nTitolo: " +  getTitolo()) + ("\nDescrizione: " +  getDescrizione())
						+ ("\nPunto d'incontro: " +  getPuntoIncontro()) + ("\nGiorno: " +  getGiorno()) + ("\nOra d'inizio: " +  getOraInizio())
						+ ("\nDa acquistare: " +  getDaAcquistare()) + ("\nStato: " +  getStato()) + ("\nTag: " +  getTag());
				if (prenotazioni != null) { //impostato da volontario
					String codiciPrenotazioni = "";
					for (int i = 0 ; i < getPrenotazioni().size() ; i++) {
						codiciPrenotazioni += "\nCodice: " + getPrenotazioni().get(i).getCodice() + ", n. iscritti:" + getPrenotazioni().get(i).getNum_da_prenotare();
					}
					result += codiciPrenotazioni;
				}
				return result;
			}

		}
		else {
			String result =  ("-----------") + ("\nTitolo: " +  getTitolo()) + ("\nGiorno mancato svolgimento: " +  getGiorno())
					+ ("Stato: " +  getStato());
			return result;
		}
	}

}
