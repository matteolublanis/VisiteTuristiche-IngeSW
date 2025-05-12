package archivio.model;

import java.util.List;
import archivio.*;
import archivio.repository.AmbitoRepository;
import archivio.repository.UserRepository;
import archivio.repository.VisitsRepository;
import dto.*;
import utility.CostantiStruttura;
import utility.Time;

public class Archivio implements AmbitoManager, AppManager {
	//Precondizione per ogni metodo di modifica Archivio del configuratore: canAddOrRemove (a parte aggiungi Configuratore, imposta max prenotazione)
	//Precondizione per tutto: param != null
	
	private AmbitoRepository ambitoRep;
	private UserRepository userRep;
	private VisitsRepository visitRep;
	private UserInfoManager userManager;
	private CredenzialiManager accessoManager;
	private static int RELEASE_DAY = 16;

	public Archivio (AmbitoRepository ambito, UserRepository users, VisitsRepository visits, CredenzialiManager accessoManager, UserInfoManager userManager) {
		ambitoRep = ambito;
		userRep = users;
		visitRep = visits;
		this.accessoManager = accessoManager;
		this.userManager = userManager;
	}
	
	public boolean pubblicaPiano(String connectionCode) { 
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
			if(isPrimaPubblicazione()) {
				visitRep.apriRaccoltaDisponibilita();
				return visitRep.setPrimaPubblicazione(); 
			}
			if (Time.getActualDateValue(Time.DAY) >= RELEASE_DAY && //SE ULTIMO PIANO PUBBLICATO MESE SCORSO PUBBLICA
					visitRep.isUltimaPubblicazioneMeseScorso() && !(visitRep.getPossibileDareDisponibilita())) {
				return visitRep.pubblicaPiano();
			}
			else return false;
		}
		else return false;
	}
	
	public boolean chiudiRaccoltaDisponibilita (String connectionCode) { //OK
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
			if (!isPrimaPubblicazione() && Time.getActualDateValue(Time.DAY) >= RELEASE_DAY &&
					visitRep.isUltimaPubblicazioneMeseScorso()) {
			if (visitRep.getPossibileDareDisponibilita()) return visitRep.chiudiRaccoltaDisponibilita(); 
			else return false;
			}
			else {
				return false;
			}
		}
		else return false;
	}
	
	public boolean apriRaccoltaDisponibilita(String connectionCode) {  //OK
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
			if (!isPrimaPubblicazione() && Time.getActualDateValue(Time.DAY) >= RELEASE_DAY&&
					((visitRep.getUltimoMesePubblicazione() == Time.getActualDateValue(Time.MONTH) 
					&& visitRep.getUltimoAnnoPubblicazione() == Time.getActualDateValue(Time.YEAR)))) { //aggiornato quando pubblicato
				if (!(visitRep.isUltimoPianoPubblicato()) || visitRep.getPossibileDareDisponibilita()) return false; //SE ULTIMO PIANO NON PUBBLICATO O GIA' APERTA RITORNA FALSO
				else return visitRep.apriRaccoltaDisponibilita();
			}
			else {
				return false;
			}
		}
		else return false;
	}
	
	public boolean getPossibilitaDareDisponibilita() { 
		return visitRep.getPossibileDareDisponibilita();
	}

	public List<TipoVisitaDTO> getElencoTipiVisite (String connectionCode) { //OK
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return ambitoRep.getElencoTipiVisite();
		else return null;
 	}
	
	public boolean rimuoviLuogo (String luogo, String connectionCode) {
		if (ambitoRep.checkIfPlaceExists(luogo) && userManager.canAddOrRemove(connectionCode)) return ambitoRep.rimuoviLuogo(luogo);
		else return false;
	}
	
	public boolean rimuoviTipo (String tipo, String connectionCode) {
		if (ambitoRep.checkIfVisitTypeExists(tipo) && userManager.canAddOrRemove(connectionCode)) return ambitoRep.rimuoviTipo(tipo);
		else return false;
	}
	
	
	public boolean checkPrimaConfigurazioneArchivio (String connectionCode) {
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) {
			return ambitoRep.isPrimaConfigurazione();
		}
		else return false;
	}
	
	public void setPossibilitaDareDisponibilitaVolontari(boolean b) {
		visitRep.setPossibilitaDareDisponibilitaVolontari(b);
	}
	
	public boolean isReleaseOrLaterDay(String connectionCode) {
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return RELEASE_DAY >= 16;
		else return false;
	}
	
	public boolean isPrimaPubblicazione () {
		return visitRep.isPrimaPubblicazione();
	}
	
	//Precondizione: pianoPubblicato prima volta
	public boolean indicaDatePrecluse (String connectionCode, String date) { //ok
		if (visitRep.isPrimaPubblicazione() || userRep.getTipoUtente(getUsernameClient(connectionCode)) != CostantiStruttura.CONFIGURATORE) return false;
		if (!Time.isValidDate(date)) return false;
		if (Time.isThisDateInMonthPlus3(date)) return visitRep.indicaDatePrecluse(date);
		else return false;
	}

	private String getUsernameClient(String connectionCode) {
		return accessoManager.getLinkedUsername(connectionCode);
	}

	public List<VisitaDTO> getElencoVisiteProposteCompleteConfermateCancellateEffettuate (String connectionCode) {
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return visitRep.getElencoVisiteProposteCompleteConfermateCancellateEffettuate();
		else return null;
	}
		
	public boolean checkIfPlaceExists (String luogo) {
		return ambitoRep.checkIfPlaceExists(luogo);
	}
	
	public boolean aggiungiLuogo (String connectionCode, LuogoDTO luogo) {
		if (userManager.canAddOrRemove(connectionCode))
			return ambitoRep.aggiungiLuogo(luogo);
		else return false;
	}
	//Usato da impostaAmbito, può essere riusato
	public void setPrimaConfigurazione() {
		ambitoRep.setPrimaConfigurazione();
	}
	
	public boolean aggiungiTipoVisite (TipoVisitaDTO tipoVisita, String connectionCode) {
		if (userManager.canAddOrRemove(connectionCode)) {
			if (checkIfVisitTypeExists(tipoVisita.getTag())) return false;
			List<LuogoDTO> luoghiConTipiAssociati = getElencoTipiVisiteLuogo(connectionCode);
			List<TipoVisitaDTO> listaTipiChePossonoIntersecare = null;
			for (LuogoDTO luogo : luoghiConTipiAssociati) {
				if (luogo.getTag().equals(tipoVisita.getLuogo())) {
					listaTipiChePossonoIntersecare = luogo.getTipiVisitaAssociati();
					break;
				}
			}	
			if (listaTipiChePossonoIntersecare!=null) {
				if (intersectVisitTypeSamePlace(listaTipiChePossonoIntersecare, tipoVisita)) return false;
			}
			else return false;
			
			return ambitoRep.aggiungiTipoVisite(tipoVisita);
		}
		else return false;
	}
	
	public boolean intersectVisitTypeSamePlace (List<TipoVisitaDTO> tipiLuogo, TipoVisitaDTO tipoDaAggiungere) {
		for (TipoVisitaDTO tipo : tipiLuogo) { //tipiVisita del luogo 
			if (Time.comesBefore(tipoDaAggiungere.getDataInizio(), tipo.getDataFine()) 
					&& !Time.comesBefore(tipoDaAggiungere.getDataFine(), tipo.getDataInizio())) {
				for (int day : tipo.getGiorniPrenotabiliVal()) {
					if (tipoDaAggiungere.getGiorniPrenotabiliVal().contains(day)) { //vuol dire che un giorno qualsiasi può intersecare
						String startHourType = tipo.getOraInizio();
						int[] fValue = Time.calculateEndTimeWithStartAndDuration(Integer.parseInt(startHourType.split(":")[0]), Integer.parseInt(startHourType.split(":")[1]), tipo.getDurataVisita());
						String finishHourType = String.format("%02d:%02d", fValue[0], fValue[1]);
						if (Time.isTimeBetween(tipoDaAggiungere.getOraInizio(), startHourType, finishHourType)) return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean checkIfVisitTypeExists (String tipo) {
		return ambitoRep.checkIfVisitTypeExists(tipo);
	}
	
	public void impostaAmbitoTerritoriale (String s, String connectionCode) {
		if (ambitoRep.isPrimaConfigurazione() 
				&& userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			ambitoRep.impostaAmbitoTerritoriale(s);
	}
	
	public boolean modificaMaxPrenotazione (String connectionCode, int max) {
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE) 
			return ambitoRep.impostaMaxPrenotazione(max);
		else return false;
	}
	
	public List<LuogoDTO> getElencoLuoghiVisitabili (String connectionCode) { 
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE)
			return ambitoRep.getElencoLuoghiVisitabili();
		else return null;
	}
	
	public List<LuogoDTO> getElencoTipiVisiteLuogo (String connectionCode) {
		if (userRep.getTipoUtente(getUsernameClient(connectionCode)) == CostantiStruttura.CONFIGURATORE)
		return ambitoRep.getElencoTipiVisiteLuogo();
		else return null;
	}

}
