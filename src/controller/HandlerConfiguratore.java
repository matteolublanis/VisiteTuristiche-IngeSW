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
	
	public void impostaAmbitoTerritoriale(@ParamName("Nome del ambito territoriale")String s) {
		gdb.impostaAmbitoTerritoriale(s);
	}
	
	@MethodName("Modifica numero max prenotazione per fruitore")
	public boolean modificaMaxPrenotazione(@ParamName("N max per prenotazione di un fruitore")int maxPrenotazione) {
		return gdb.modificaMaxPrenotazione(maxPrenotazione);
	}
	
	@MethodName("Visualizza lista volontari.")
	public String getListaVolontari() {
		 return gdb.getListaUser(CostantiStruttura.VOLONTARIO);
	}
	
	@MethodName("Visualizza elenco luoghi visitabili.")
	public String getElencoLuoghiVisitabili() {
		return gdb.getElencoLuoghiVisitabili();
	}
	
	@MethodName("Visualizza elenco tipi visite per luogo.")
	public String getElencoTipiVisiteLuogo() {
		return gdb.getElencoTipiVisiteLuogo();
	}
	
	@MethodName("Pubbica il piano delle visite.")
	public void pubblicaPianoVisite() {
		
	}
	
	@MethodName("Indica date precluse del prossimo piano a quello successivo a questo.")
	public boolean indicaDatePrecluse(@ParamName("Giorno precluso del mese successivo al prossimo")int giorno) {
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
		return gdb.indicaDatePrecluse(date);
	}
	
	@MethodName("Aggiungi tipo visite.")
	public void aggiungiTipoVisite() {
		
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
