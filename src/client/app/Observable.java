package client.app;

public interface Observable {
    void attach(Observer o);
    void detach(Observer o);
    void notifyObservers(UiEvent evento);
}
