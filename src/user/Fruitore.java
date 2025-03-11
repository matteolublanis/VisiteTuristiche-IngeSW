package user;

import controller.ControllerUtente;
import utility.CostantiStruttura;

public class Fruitore extends Utente {
	
	public Fruitore (String username, ControllerUtente gu) {
		this.username = username;
		this.tipo = CostantiStruttura.FRUITORE;
		this.gu = gu;
		actionMethods((i, map, method) -> map.put(i, method));
	}
	
}
