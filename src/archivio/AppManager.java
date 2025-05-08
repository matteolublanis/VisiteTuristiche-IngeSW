package archivio;

public interface AppManager {
		
	public boolean pubblicaPiano(String connectionCode);
	
	public boolean chiudiRaccoltaDisponibilita (String connectionCode);
	
	public boolean apriRaccoltaDisponibilita(String connectionCode);
	
	public boolean getPossibilitaDareDisponibilita();
		
	public void setPossibilitaDareDisponibilitaVolontari(boolean b);
	
	public boolean isReleaseOrLaterDay(String connectionCode);
	
	public boolean isPrimaPubblicazione ();
}
