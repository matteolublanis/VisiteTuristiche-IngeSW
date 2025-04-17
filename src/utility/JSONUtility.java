package utility;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.*;

public class JSONUtility {
	
	//Precondizione: filePath exists
    public static JSONObject readJsonFile(String filePath) {
        JSONObject jsonObject = null;

        try {
            FileReader reader = new FileReader(filePath);
            JSONTokener  parser = new JSONTokener (reader);
            jsonObject =  new JSONObject(parser);

            try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

        } catch (FileNotFoundException e) {
            return null;
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
    
    //Precondizione: jsonObject != null && keyValue != null 
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
    
    //Precondizione: jsonObject != null && keyValue != null
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
    //Precondizione: jsonObject != null && file exists in path 
    public static void aggiornaJsonFile (JSONObject jsonObject, String path, int righe) {
        String dirName = "json"; // Scrive tutti i JSON in una cartella dedicata
        Path dirPath = Paths.get(dirName);
        if (!Files.exists(dirPath)) {
            try {
				Files.createDirectories(dirPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
        } 
    	try (FileWriter file = new FileWriter(path)) {
            file.write(jsonObject.toString(righe)); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}