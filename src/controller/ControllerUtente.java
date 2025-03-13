package controller;

public interface ControllerUtente {
	
	public boolean checkPrimoAccesso();
	
	public boolean checkCredenzialiCorrette (Credenziali c);
	
	public boolean cambiaCredenziali (Credenziali c);
	
	public void setUsername(String username);
	
	public String getUsername();
	
	public String comunicaAzioniDisponibili();
	
}
