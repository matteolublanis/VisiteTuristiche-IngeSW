package user;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import controller.ControllerUtente;

public abstract class Utente {
	protected String username; 
	protected int tipo;
	private final LinkedHashMap<String, Method> mappaMetodi = new LinkedHashMap<>();
	protected ControllerUtente gu;
	
	public int getTipo() {
		return tipo;
	}
	
	public String getUsername () {
		return this.username;
	}
	
	public void setUsername (String username) {
		this.username = username;
	}
		
	@SuppressWarnings("rawtypes")
	public void actionMethods(ActionMethods action) {
		Class g = this.getClass();
		Method[] m = g.getMethods(); 
		for (Method method : m) { 
            String methodName = method.getName(); 
            if (method.getDeclaringClass().equals(g)) 
            	action.execute(methodName, mappaMetodi, method); 
        } 
	}
	
	public void eseguiMetodo (String i)  {
		Method m = mappaMetodi.get(i);
		try {
			System.out.println(m.toString());
			m.invoke(this); //
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public String metodiEseguibili () {
		String s = "";
		for (String k : mappaMetodi.keySet()) s += k + "\n"; //TODO confrontarsi
		return s;
	}
	
}
