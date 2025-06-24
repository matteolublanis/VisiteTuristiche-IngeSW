package client.app;

public class UiRequest {
    private UiEventType tipo;
    private String messaggio;
    private Object parametriAggiuntivi;
    private String richiedenteId;

    public UiRequest(UiEventType tipo, String messaggio, String richiedenteId) {
        this.tipo = tipo;
        this.messaggio = messaggio;
        this.richiedenteId = richiedenteId;
    }

    public UiRequest(UiEventType tipo, String messaggio, Object parametriAggiuntivi, String richiedenteId) {
        this.tipo = tipo;
        this.messaggio = messaggio;
        this.parametriAggiuntivi = parametriAggiuntivi;
        this.richiedenteId = richiedenteId;
    }

    public UiEventType getTipo() { 
    	return tipo; 
    }
    
    public String getMessaggio() { 
    	return messaggio; 
    }
    
    public Object getParametriAggiuntivi() { 
    	return parametriAggiuntivi; 
    }
    
    public String getRichiedenteId() { 
    	return richiedenteId; 
    }
}
