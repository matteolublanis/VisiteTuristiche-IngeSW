package dto;

import java.util.HashMap;
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
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		result.put(tag, giorni);
		return result;
	}
	
	public String getTag() { return tag; }
	public List<String> getGiorni() { return giorni; }

}
