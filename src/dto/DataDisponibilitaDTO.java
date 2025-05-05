package dto;

import java.util.List;
import java.util.Map;

public class DataDisponibilitaDTO implements DTO{
	
	private String tag;
	private List<String> giorni;
	
	public DataDisponibilitaDTO (String tag, List<String> giorni) {
		this.tag = tag;
		this.giorni = giorni;
	}
	
	@Override
	public Map<String, List<String>> infoDTO() {
		return null;
	}
	
	public String getTag() { return tag; }
	public List<String> getGiorni() { return giorni; }

}
