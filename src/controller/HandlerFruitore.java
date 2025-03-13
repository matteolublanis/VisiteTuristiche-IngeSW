package controller;

public class HandlerFruitore implements ControllerUtente {
	
	private ControllerArchivio gdb;
	private String username;
	
	
	public HandlerFruitore (ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
	}
	
	@Override
	public boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(username);
	}

	@Override
	public boolean checkCredenzialiCorrette(Credenziali c) {
		return gdb.checkCredenzialiCorrette(c);
	}

	@Override
	public boolean cambiaCredenziali(Credenziali c) {
		if (gdb.cambiaCredenziali(username, c)) return true;
		return false;
	}

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String comunicaAzioniDisponibili() {
		// TODO Auto-generated method stub
		return null;
	}


}
