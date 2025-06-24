package client.app;

import java.util.ArrayList;
import java.util.List;

import client.controller_utente.ControllerUtente;
import client.log_events.AppEvent;
import client.login.Login;
import dto.*;
import dto.LuogoDTO;
import dto.TipoVisitaDTO;
import utility.Credenziali;

public interface ViewInterface {
    void mostraMessaggio(String messaggio);
    void mostraElencoAzioni(List<String> azioni);
    void mostraListaLuoghi(List<LuogoDTO> luoghi);
    void mostraListaVisite(List<VisitaDTO> visite);
    void mostraListaTipiVisita(List<TipoVisitaDTO> tipi);
    void mostraListaUtenti(List<UserDTO> utenti);
    void mostraListaPrenotazioni(List<PrenotazioneDTO> prenotazioni);
    void mostraListaDateDisponibilita(List<DataDisponibilitaDTO> date);
    void richiediInput(UiRequest richiesta);
    void richiestaCompletata(String richiedenteId, Object risultato);
	void stop();
	void start();
}
