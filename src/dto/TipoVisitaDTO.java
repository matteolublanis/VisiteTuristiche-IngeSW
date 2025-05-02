package dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TipoVisitaDTO implements DTO{
	private String tag = null;
    private String titolo = null;
    
    
    public TipoVisitaDTO(String tag, String titolo) {
    	this.tag = tag;
    	this.titolo = titolo;
    }
    
	@Override
	public Map<String, List<String>> infoDTO() {
		Map<String, List<String>> info = new HashMap<String, List<String>>();
		List<String> code = new ArrayList<String>();
		code.add(tag);
		info.put(titolo, code);
		return info;
	}

	public String getTag() {
		return tag;
	}

	public String getTitolo() {
		return titolo;
	}
	
}
