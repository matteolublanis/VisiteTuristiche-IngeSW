package utility;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.*;

public class JSONUtility {

    public static JSONObject readJsonFile(String filePath) {
        JSONObject jsonObject = null;

        try {
            FileReader reader = new FileReader(filePath);
            JSONTokener  parser = new JSONTokener (reader);
            jsonObject =  new JSONObject(parser);

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
    
    public static boolean containsValue(JSONArray jsonArray, String value) {
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.get(i).equals(value)) {
                return true;
            }
        }
        return false;
    }
    
	//TODO gestire eccezione
    public static HashSet<String> allObjectsSameIntValue (JSONObject jsonObject, int tipo, String keyValue) {
    	HashSet<String> result = new HashSet<>();
    	Iterator<String> keys = jsonObject.keys();
    	while(keys.hasNext()) {
    	    String key = keys.next();
    	    if (jsonObject.get(key) instanceof JSONObject) {
    	        if ((int) ((JSONObject) jsonObject.get(key)).get(keyValue) == tipo) {
    	        	result.add(key);
    	        }
    	    }
    	}
    	return result;
    }
    
	//TODO gestire eccezione
    public static HashMap<String, String> getAllSameValsFromObjects (JSONObject jsonObject, String keyValue) {
    	HashMap<String, String> result = new HashMap<>();
    	Iterator<String> keys = jsonObject.keys();
    	while(keys.hasNext()) {
    	    String key = keys.next();
    	    if (jsonObject.get(key) instanceof JSONObject) {
    	        result.put(key, (String) ((JSONObject) jsonObject.get(key)).get(keyValue).toString());
    	    }
    	}
    	return result;

    }
    
    public static void aggiornaJsonFile (JSONObject jsonObject, String path, int righe) {
    	try (FileWriter file = new FileWriter(path)) {
            file.write(jsonObject.toString(righe)); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}