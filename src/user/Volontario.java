package user;

import controller.ControllerUtente;

public class Volontario extends Utente {

	public Volontario (String username, int tipo, ControllerUtente gu) {
		this.username = username;
		this.tipo = tipo;
		this.gu = gu;
		actionMethods((i, map, method) -> map.put(i, method));

	}

}
