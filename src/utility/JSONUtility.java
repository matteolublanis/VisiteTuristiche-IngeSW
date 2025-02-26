package utility;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
    
    public static void aggiornaJsonFile (JSONObject jsonObject, String path) {
    	Iterator<String> keys = jsonObject.keys();

    	try (FileWriter file = new FileWriter(path)) {
            file.write(jsonObject.toString(5)); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}