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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkCredenzialiCorrette(Credenziali c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cambiaCredenziali(Credenziali c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setUsername(String username) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String comunicaAzioniDisponibili() {
		// TODO Auto-generated method stub
		return null;
	}


}
