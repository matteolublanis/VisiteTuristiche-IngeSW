package user;

import controller.ControllerUtente;

public abstract class Utente {
	protected String username; 
	protected int tipo;
	protected ControllerUtente gu;
	
	public int getTipo() {
		return tipo;
	}
	
	public String getUsername () {
		return this.username;
	}
	
	public void setUsername (String username) {
		this.username = username;
	}
	
	public boolean checkPrimoAccesso () {
		return gu.checkPrimoAccesso();
	}
	
	
}
