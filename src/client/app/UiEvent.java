package client.app;

public class UiEvent {
	private UiEventType tipo;
    private Object payload;

    public UiEvent(UiEventType tipo, Object payload) {
        this.tipo = tipo;
        this.payload = payload;
    }

    public UiEventType getTipo() {
        return tipo;
    }

    public Object getPayload() {
        return payload;
    }
}