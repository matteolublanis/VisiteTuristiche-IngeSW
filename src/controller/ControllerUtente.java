package controller;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedList;

import main.App;
import utility.CostantiStruttura;
import utility.Credenziali;

public abstract class ControllerUtente {
	
	protected ControllerArchivio gdb;
	protected String username;
	
	public ControllerUtente () {
		//do something
	}
	
	public ControllerUtente(ControllerArchivio gdb, String username) {
		this.gdb = gdb;
		this.username = username;
	}
	
	public int getTipoUtente () {
		return gdb.getTipoUtente(username);
	}
	
	public boolean checkPrimoAccesso() {
		return gdb.checkPrimoAccesso(username);
	}
	public void primoAccesso(App a) {
		if (checkPrimoAccesso()) {
			a.view("Primo accesso eseguito.");
			boolean b = false;
			do {
				a.view("Cambia le tue credenziali:");
				String username = a.richiediVal(CostantiStruttura.STRING, "username");
				String password = a.richiediVal(CostantiStruttura.STRING, "password");
				Credenziali c = new Credenziali(username, password);
				b = cambiaCredenziali(c);
				if (!b) a.view("Credenziali non cambiate, username già presente.");
			} while (!b);
			a.view("Credenziali cambiate.");
		};
	}
	
	public boolean cambiaCredenziali(Credenziali c) {
		return (gdb.cambiaCredenziali(this.username, c, this));
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
	protected boolean chiediSioNo (App a, String val) {
		a.view(val);
		do {
			String answer = (String)a.richiediVal(CostantiStruttura.STRING, "si o no");
			switch (answer.toLowerCase()) {
			case "si":
				return true;
			case "no":
				return false;
			default:
				a.view("Formato non valido, inserire si/no");
				break;
			}
		} while (true);

	}
	
    public LinkedList<Method> getAzioniDisponibili() {
    	
    	LinkedList<Method> metodiConcreti = new LinkedList<>();
        
         Method[] metodi = this.getClass().getDeclaredMethods();
        
         for (Method metodo : metodi) {
             if (!metodo.getName().equals("getAzioniDisponibili")
            		 && !metodo.getName().startsWith("lambda$")
            		 && (Modifier.isPublic(metodo.getModifiers()))) { 
             metodiConcreti.add(metodo);
         		}
    	}
         
        metodiConcreti.sort((m1, m2) -> m1.getName().compareTo(m2.getName()));
    	return metodiConcreti;
    	/*
    	 * 
    	Metodo più generico per usare più strutture, serve però ordine quindi usiamo linkedlist
    	return Arrays.stream(this.getClass().getDeclaredMethods())
                .filter(metodo -> !metodo.getName().equals("getAzioniDisponibili")) //aggiungere altri controlli
                .toList();
                
        */
    }
}
