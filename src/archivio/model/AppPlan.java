package archivio.model;

import archivio.AppManager;
import archivio.repository.AmbitoRepository;

public class AppPlan implements AppManager {
	
	AmbitoRepository ambitoRep;
	
	@Override
	public boolean checkPrimoAvvio () { //OK
		return false;
	}

	@Override
	public boolean pubblicaPiano(String connectionCode) { 
		return false;
	}

	@Override
	public boolean chiudiRaccoltaDisponibilita(String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean apriRaccoltaDisponibilita(String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getPossibilitaDareDisponibilita() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isReleaseOrLaterDay(String connectionCode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPrimaPubblicazione() {
		// TODO Auto-generated method stub
		return false;
	}

}
