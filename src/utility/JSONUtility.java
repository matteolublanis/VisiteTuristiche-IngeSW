package utility;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.parser.ParseException;

public class JSONUtility {

    public static JSONObject readJsonFile(String filePath) {
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = null;

        try {
            FileReader reader = new FileReader(filePath);

            Object obj = parser.parse(reader);
            jsonObject = (JSONObject) obj;

            reader.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
    
    public static void aggiornaJsonFile (JSONObject jsonObject) {
    	try (FileWriter file = new FileWriter("src/memoria.json")) {
            file.write(jsonObject.toJSONString()); // Scrivi l'oggetto JSON aggiornato nel file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}