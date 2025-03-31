package dto;

import java.util.List;

public class UserDTO {
	private String username = null;
	private List<String> tipi_visite = null;
	
	public UserDTO(String username) {
		this.username = username;
	}
	
	public UserDTO(String username, List<String> tipi_visite) {
		this.username = username;
		this.tipi_visite = tipi_visite;
	}

	public String getUsername() {
		return username;
	}

	public List<String> getTipi_visite() {
		return tipi_visite;
	}
}
