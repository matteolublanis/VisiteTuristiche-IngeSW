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

public interface App {
	public void start();
	public void stop();
	public boolean scegliAzione(String azione); //TODO da cambiare con ENUM azioni
	public Login getGl();
	public void setGu(ControllerUtente gu);
	public ControllerUtente getGu();
	public String richiediInput(String msg);
	public boolean chiediSioNo(String msg);
	public String richiediDataValida(String msg); //TODO da cambiare con LocalDate o Calendar?
	public int richiediInt(String msg);
	public String richiediOraValida(String msg);
	public int richiediNumeroConLimiteInferiore(String msg, int limite);
	public void viewLogin(Credenziali primeCredenziali);
	public Credenziali richiediCredenziali();
	void catchEvent(AppEvent e);
	void catchEvent(AppEvent e, String attachment);
	public void log(String msg);
	public void viewPrimoAccesso();
	public void viewListDTO(List<DTO> list);
	public void viewListDataDisponibilitaDTO(List<DataDisponibilitaDTO> list);
	public void viewListLuogoDTO(List<LuogoDTO> list);
	public void viewListPrenotazioneDTO(List<PrenotazioneDTO> list);
	public void viewListTipoVisitaDTO(List<TipoVisitaDTO> list);
	public void viewListUserDTO(List<UserDTO> list);
	public void viewListVisitaDTO(List<VisitaDTO> list);
	public TipoVisitaDTO richiediTipoVisita(String luogo);
	public String richiediTipoVisitaEsistente();
	public ArrayList<Credenziali> richiediVolontari();
	public String richiediVisitaEsistente();
	public LuogoDTO richiediLuogo();
}
