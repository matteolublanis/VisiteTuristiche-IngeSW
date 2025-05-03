package client.app;

import java.util.ArrayList;
import java.util.List;

import client.controller_utente.ControllerUtente;
import client.log_events.AppEvent;
import dto.DTO;
import dto.TipoVisitaDTO;
import utility.Credenziali;

public interface App {
	public void start();
	public void stop();
	public void setGu(ControllerUtente gu);
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
	public TipoVisitaDTO richiediTipoVisita(String luogo);
	public String richiediTipoVisitaEsistente();
	public ArrayList<String> richiediVolontari();
}
