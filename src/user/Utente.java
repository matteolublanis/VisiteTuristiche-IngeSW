package user;

public abstract class Utente {
	protected String username; 
	protected long tipo;
	
	public String getUsername () {
		return this.username;
	}
	
	protected void setUsername (String username) {
		this.username = username;
	}
	
}
