package user;

public class Credenziali {
	
	private String username, password;
	
	public Credenziali (String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

}
