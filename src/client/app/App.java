package client.app;

import client.controller_utente.ControllerUtente;

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
}
