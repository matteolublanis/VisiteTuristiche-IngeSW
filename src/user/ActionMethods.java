package user;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;

public interface ActionMethods {
	void execute(String i, LinkedHashMap m, Method method);
}
