package user;

import controller.ControllerUtente;
import utility.CostantiStruttura;

public class Volontario extends Utente {

	public Volontario (String username, ControllerUtente gu) {
		this.username = username;
		this.gu = gu;
		actionMethods((i, map, method) -> map.put(i, method));

	}

}
