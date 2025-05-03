package client.controller_utente;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import archivio.ArchivioFacade;
import client.app.App;
import client.log_events.AppEvent;
import dto.TipoVisitaDTO;
import utility.CostantiStruttura;
import utility.Credenziali;
import utility.MethodName;
import utility.Time;


public class HandlerConfiguratore extends ControllerUtente{	
	//Precondizione tutti i metodi: param != null
	
	public HandlerConfiguratore(ArchivioFacade archivio, App a, String connectionCode) {
		this.archivio = archivio;
		this.a = a;
		this.connectionCode = connectionCode;
	}
	
	public void checkPrimoAccesso () {
		super.checkPrimoAccesso();
		if (checkPrimaConfigurazioneArchivio()) configuraArchivio();
	}
	
	private boolean checkPrimaConfigurazioneArchivio () {
		return archivio.checkPrimaConfigurazioneArchivio(connectionCode);
	}
	//Postcondizione: nome ambito impostato, max prenotazione impostato, luoghi aggiunti, tipi visita aggiunti, volontari nuovi o esistenti associati
	private void configuraArchivio() {
		String ambito = null;
		do {
			ambito = a.richiediInput("nome ambito territoriale");
		} while (!a.chiediSioNo("Confermi di voler chiamare l'ambito " + ambito + " ?"));
		impostaAmbitoTerritoriale(ambito);
		modificaMaxPrenotazione(a);
		a.catchEvent(AppEvent.STARTING_ADDING_PLACES_NEW_TERRITORY);
		aggiungiLuogo();
	}
	//Postcondizione: volontario creato con tipo visita associato
	private String impostaNuovoVolontarioConUnTipoVisitaScelto (String tipo) {
		a.catchEvent(AppEvent.STARTING_CREATING_VOLUNTEER);
		Credenziali c = a.richiediCredenziali();
		Set<String> tipi_visiteVal = new HashSet<>();
		tipi_visiteVal.add(tipo);
		if (archivio.impostaCredenzialiNuovoVolontario(connectionCode, c.getUsername(), c.getPassword(), tipi_visiteVal, false)) {
			a.catchEvent(AppEvent.NEW_VOLUNTEER_INSERTED);
			return c.getUsername();
		}
		else {
			a.catchEvent(AppEvent.NOT_INSERTED_VOLUNTEER);
			return "";
		}
	}
	
	//Postcondizione: volontario creato con tipo visita associato
	private boolean impostaNuovoVolontarioConTipoVisitaScelto (App a, Set<String> tipi_visiteVal, List<String> volontari) {
		a.catchEvent(AppEvent.STARTING_CREATING_VOLUNTEER);
		Credenziali c = a.richiediCredenziali();
		if (archivio.impostaCredenzialiNuovoVolontario(connectionCode, c.getUsername(), c.getPassword(), tipi_visiteVal, false)) {
			volontari.add(c.getUsername());
			return true;
		}
		else {
			return false;
		}
	}
	
	//Postcondizione: luogo rimosso da Archivio, associazioni rimosse
	@MethodName("Rimuovi luogo")
	public void rimuoviLuogo () {
		if (canAddOrRemove()) {
			if (archivio.rimuoviLuogo(a.richiediInput("luogo da rimuovere"), connectionCode)) {
				a.catchEvent(AppEvent.PLACE_REMOVED_SUCCESFULLY);
			}
			else {
				a.catchEvent(AppEvent.PLACE_NOT_REMOVED);
			}
		}
	}
	
	//Postcondizione: volontario rimosso da Archivio, associazioni rimosse
	@MethodName("Rimuovi volontario")
	public void rimuoviVolontario () {
		if (canAddOrRemove()) {
			if (archivio.rimuoviVolontario(a.richiediInput("username del volontario da rimuovere"), connectionCode)) {
				a.catchEvent(AppEvent.VOLUNTEER_REMOVED);
			}
			else {
				a.catchEvent(AppEvent.VOLUNTEER_NOT_REMOVED);

			}
		}
	}
	
	//Postcondizione: tipo visita rimosso da Archivio, associazioni rimosse
	@MethodName("Rimuovi tipo di visita")
	public void rimuoviTipo () {
		if (canAddOrRemove()) {
			if (archivio.rimuoviTipo(a.richiediInput("tipo da rimuovere"), connectionCode)) {
				a.catchEvent(AppEvent.VISIT_TYPE_REMOVED);
			}
			else {
				a.catchEvent(AppEvent.VISIT_TYPE_NOT_REMOVED);

			}
		}
	}
	
	//Postcondizione: impostato nome ambito territoriale
	private void impostaAmbitoTerritoriale(String s) {
		archivio.impostaAmbitoTerritoriale(s, connectionCode);
	}
	
	//Precondizione: maxPrenotazione > 0
	//Postcondizione: impostato max prenotazione
	private boolean impostaMaxPrenotazione(int maxPrenotazione) {
		return (archivio.modificaMaxPrenotazione(connectionCode, maxPrenotazione));
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public void modificaMaxPrenotazione (App a) {
		int max = 0;
		do {
			max = a.richiediInt("max prenotazione per fruitore");
			if (max < 1) a.catchEvent(AppEvent.NOT_VALID_VALUE);
			else if (a.chiediSioNo("Confermi?")) {
				break;
			}
			else continue;
		} while (true);
		if (impostaMaxPrenotazione(max)) {
			a.catchEvent(AppEvent.MAX_VALUE_SET);
		}
		else {
			a.catchEvent(AppEvent.MAX_VALUE_NOT_SET);

		}
	}
	
	@MethodName("Visualizza lista volontari")
	public void getListaVolontari(App a) {
		a.viewListDTO(archivio.getListaUser(connectionCode, CostantiStruttura.VOLONTARIO));
	}
	
	@MethodName("Visualizza elenco luoghi visitabili")
	public void getElencoLuoghiVisitabili(App a) {
		a.viewListDTO(archivio.getElencoLuoghiVisitabili(connectionCode));
	}
	
	@MethodName("Visualizza elenco tipi visite per luogo")
	public void getElencoTipiVisiteLuogo(App a) {
		a.viewListDTO(archivio.getElencoTipiVisiteLuogo(connectionCode));
	}
	
	//Precondizione: isTodays(16) o dopo
	//Postcondizione: piano pubblicato
 	@MethodName("Pubblica il piano delle visite")
	public void pubblicaPianoVisite(App a) {
		if (archivio.isReleaseOrLaterDay(connectionCode)) {
			if (archivio.isPrimaPubblicazione()) {
				a.catchEvent(AppEvent.PROJECT_STARTED);
				archivio.pubblicaPiano(connectionCode);
				archivio.apriRaccoltaDisponibilita(connectionCode);
			}
			else {
				if (archivio.pubblicaPiano(connectionCode)) {
					a.catchEvent(AppEvent.SCHEDULE_PUBLISHED);
				}
				else {
					a.catchEvent(AppEvent.SCHEDULE_NOT_PUBLISHED);

				}
					
			}
		}
		else a.catchEvent(AppEvent.CANT_PUBLISH_NOW);
	}
	
	@MethodName("Indica date precluse del prossimo piano a quello successivo a questo")
	public void indicaDatePrecluse() {
		String data = null; 
		do {
			data = a.richiediDataValida("data preclusa"); 
			if (a.chiediSioNo("Confermi di inserire " + data + " come data preclusa?")) {
				break;
			}
			else return;
		} while (true);
		if ((archivio.indicaDatePrecluse(connectionCode, data))) {
			a.catchEvent(AppEvent.PRECLUDED_DATE_INSERTED);
		}
		else {
			a.catchEvent(AppEvent.PRECLUDED_DATE_NOT_INSERTED);
		}
	}
	
	@MethodName("Apri la raccolta delle disponibilità dei volontari")
	public void apriRaccoltaDisponibilita () {
		if (archivio.apriRaccoltaDisponibilita(connectionCode)) {
			a.catchEvent(AppEvent.DISPONIBILITIES_OPENED);
		}
		else {
			a.catchEvent(AppEvent.DISPONIBILITIES_NOT_OPENED);
		}
	}
	
	@MethodName("Chiudi la raccolta delle disponibilità dei volontari")
	public void chiudiRaccoltaDisponibilita () {
		if (archivio.chiudiRaccoltaDisponibilita(connectionCode)) {
			a.catchEvent(AppEvent.DISPONIBILITIES_CLOSED);
		}
		else {
			a.catchEvent(AppEvent.DISPONIBILITIES_NOT_CLOSED);
		}
	}
	
	//Precondizione: luogo diverso da null (presente nell'archivio)
	//Postcondizione: tipo visita inserito e inserito associazione
	private boolean aggiungiTipoVisitePartendoDaLuogo (String luogo) {
		TipoVisitaDTO tipoVisita = a.richiediTipoVisita();
		return (archivio.aggiungiTipoVisite(tipoVisita, connectionCode));
	}
	
	@MethodName("Aggiungi tipo visite")
	public void aggiungiTipoVisite() {
		if (canAddOrRemove()) {
			String luogo = "";
			do {
				luogo = a.richiediInput("luogo della visita");
				if (!archivio.checkIfPlaceExists(luogo)) a.view("Il luogo inserito è inesistente.");
			} while (!archivio.checkIfPlaceExists(luogo));
			
			a.view((aggiungiTipoVisitePartendoDaLuogo(luogo)) ? "Il nuovo tipo di visita è stato aggiunto." : "Il nuovo tipo di visita non è stato aggiunto.");
		}
		
	}
	@MethodName("Aggiungi credenziali nuovo configuratore")
	public void impostaCredenzialiNuovoConfiguratore () {
		//Event Add Configuratore
		Credenziali c = a.richiediCredenziali();
		if (archivio.impostaCredenzialiNuovoConfiguratore(connectionCode, c.getUsername(), c.getPassword())) {
			a.view("Aggiunto nuovo configuratore."); //Event
		}
		else {
			a.view("Non è stato aggiunto un nuovo configuratore, username già in utilizzo."); //Event
		}
				
	}
	@MethodName("Visualizza visite proposte, complete, confermate, cancellate e effettuate")
	public void getElencoVisiteProposteCompleteConfermateCancellateEffettuate (App a) {
		a.viewListDTO(archivio.getElencoVisiteProposteCompleteConfermateCancellateEffettuate(connectionCode));
	}
	
	private void associaVolontarioEsistente(String tipo) {
	    getListaVolontari(a);
	    String volontario;
	    do {
	        volontario = a.richiediInput("volontario da associare (esc per tornare indietro)");
	        if (volontario.equalsIgnoreCase("esc")) return;
	        if (!archivio.checkIfUserExists(volontario)) {
	            a.view("Il volontario inserito non esiste, reinserire.");
	        } else if (!archivio.associaVolontarioEsistenteATipoVisitaEsistente(connectionCode, volontario, tipo)) {
	            a.view("Problema nell'inserimento del volontario, potrebbe esserci un conflitto con i giorni.");
	        } else {
	            break;
	        }
	    } while (true);
	}
	
	@MethodName("Aggiungi volontari ad un tipo di visita esistente")
	public void aggiungiVolontariATipiVisita () {
		if (canAddOrRemove()) {
			a.viewListDTO(archivio.getElencoTipiVisite(connectionCode));
			String tipo = richiediVisitaEsistente("tag del tipo della visita a cui associare i volontari");
			List<String> volontari = a.richiediVolontari();
			
		}
	}
	
	private boolean canAddOrRemove() {
		if (archivio.canAddOrRemove(connectionCode)) {
			return true;
		}
		else {
			a.catchEvent(AppEvent.CANT_ADD_OR_REMOVE);
			return false;
		}
	}
	
	@MethodName("Aggiungi luogo")
	public void aggiungiLuogo () {
		if (canAddOrRemove()) { 
			do {
				String tag = a.richiediInput("tag del luogo");
				String nome = a.richiediInput("nome del luogo");
				String descrizione = a.richiediInput("descrizione del luogo");
				String collocazione = a.richiediInput("collocazione del luogo");
				if (archivio.aggiungiLuogo(connectionCode, tag, nome, descrizione, collocazione, null)) {
					a.view("Aggiunto un nuovo luogo.");
					boolean aggiunto = false;
					do {
						aggiunto = aggiungiTipoVisitePartendoDaLuogo(tag);
						a.view(aggiunto ? "Il nuovo tipo di visita è stato aggiunto." : "Il nuovo tipo di visita non è stato aggiunto.");
						if (aggiunto) {
							aggiunto = !a.chiediSioNo("Vuoi continuare con l'aggiunta di tipi di visite?"); //se non vuole inserire ha finito
						}
					} while (!aggiunto);
				}
				else a.view("Il luogo non è stato aggiunto, controllare il tag inserito.");
			} while (a.chiediSioNo("Vuoi aggiungere un altro luogo?"));
		}
		
	}
	
}
