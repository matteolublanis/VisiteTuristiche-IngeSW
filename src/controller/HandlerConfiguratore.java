package controller;

import java.lang.reflect.Method;
import java.util.LinkedList;

import utility.CostantiStruttura;
import utility.MethodName;
import utility.ParamName;
import utility.Time;


public class HandlerConfiguratore extends ControllerUtente{	
	
	public HandlerConfiguratore () {
		
	}
	
	public HandlerConfiguratore (ControllerArchivio gdb, String username) {
		super(gdb, username);
	}
	
	@MethodName("Imposta ambito territoriale")
	public void impostaAmbitoTerritoriale(@ParamName("Nome del ambito territoriale")String s) {
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public String modificaMaxPrenotazione(@ParamName("N max per prenotazione di un fruitore")int maxPrenotazione) {
		if (gdb.modificaMaxPrenotazione(maxPrenotazione)) return "Il valore max prenotazione è stato modificato.";
		else return "Il valore max prenotazione non è stato modificato.";
	}
	
	@MethodName("Visualizza lista volontari")
	public String getListaVolontari() {
		 return gdb.getListaUser(CostantiStruttura.VOLONTARIO);
	}
	
	@MethodName("Visualizza elenco luoghi visitabili")
	public String getElencoLuoghiVisitabili() {
		return gdb.getElencoLuoghiVisitabili();
	}
	
	@MethodName("Visualizza elenco tipi visite per luogo")
	public String getElencoTipiVisiteLuogo() {
		return gdb.getElencoTipiVisiteLuogo();
	}
	
	@MethodName("Pubbica il piano delle visite")
	public void pubblicaPianoVisite() {
		
	}
	
	@MethodName("Indica date precluse del prossimo piano a quello successivo a questo")
	public String indicaDatePrecluse(@ParamName("Giorno precluso del mese successivo al prossimo")int giorno) {
		int mese, anno;
		if (Time.getActualMonth() + 3 > 12) {
			mese = Time.getActualMonth() + 3 - 12;
			anno = Time.getActualYear() + 1;
		}
		else {
			mese = Time.getActualMonth() + 2;
			anno = Time.getActualYear();
		}
		String date = String.format("%02d-%02d-%04d", giorno, mese, anno);
		if (gdb.indicaDatePrecluse(date)) return "Inserita data preclusa.";
		else return "La data preclusa non è stata inserita";
	}
	
	@MethodName("Aggiungi tipo visite")
	public void aggiungiTipoVisite() {
		
	}
	@MethodName("Aggiungi credenziali nuovo configuratore")
	public String impostaCredenzialiNuovoConfiguratore (@ParamName("Username")String username, @ParamName("Password")String password) {
		if (gdb.impostaCredenzialiNuovoConfiguratore(username, password)) return "Aggiunte credenziali nuovo configuratore.";
		else return "Non sono state aggiunte nuove credenziali.";
				
	}
	@MethodName("Visualizza visite proposte, complete, confermate, cancellate e effettuate")
	public String getElencoVisiteProposteCompleteConfermateCancellateEffettuate () {
		
		/*
		 * 
		 * Le visite proposte sono nel piano pubblicato del mese i+1
		 * Le visite complete sono nello stesso piano di proposte ma non accettano fruitori (confronto MaxFruitore e prenotazioni)
		 * Le visite confermate sono nello stesso piano di proposte di prima ma non accettano più nulla anche se sotto maxfruitore
		 * Le visite cancellate sono come le confermate ma non vengono successivamente memorizzate nello storico una volta fatte
		 * Le visite effettuate sono quelle confermate che superano la data quando viene effettuata
		 * 
		 */
		
		return "";
	}
	@MethodName("Aggiungi nuova visita nel piano da pubblicare")
	public void aggiungiVisita () {
		
	}
	
	@Override
    public LinkedList<Method> getAzioniDisponibili() {
    	
    	LinkedList<Method> metodiConcreti = new LinkedList<>();
        
         Method[] metodi = this.getClass().getDeclaredMethods();
        
         for (Method metodo : metodi) {
             if (!metodo.getName().equals("getAzioniDisponibili")
            		 && !metodo.getName().equals("impostaAmbitoTerritoriale")
            		 && !metodo.getName().startsWith("lambda$")) { 
             metodiConcreti.add(metodo);
             
         	}
    	}
        metodiConcreti.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));
    	return metodiConcreti;
    	/*
    	Metodo più generico per usare più strutture, serve però ordine quindi usiamo linkedlist
    	return Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(metodo -> !metodo.getName().equals("getAzioniDisponibili")) //aggiungere altri controlli
                .toList();
                
        */
    }
}
