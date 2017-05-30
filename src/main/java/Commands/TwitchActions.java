package Commands;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class TwitchActions {

	private static final String PROPERTIES_FILENAME = "/twitch.properties";
	private static String apiKey;
	private String channel = "ogn_lol";
	
	public TwitchActions() {
		Properties properties = new Properties();
        try {
            InputStream in = getClass().getResourceAsStream(PROPERTIES_FILENAME); 
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
        }
        apiKey = properties.getProperty("twitch.apikey");
	}
	
	public Boolean getLiveStream() throws IOException{
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.twitch.tv/kraken/streams/"+channel)
					  .header("accept", "application/json")
					  .header("Client-ID", apiKey)
					  .queryString("stream_type", "live")
					  .asJson();
			JSONObject json = jsonResponse.getBody().getObject();
			Map<String, Object> datas = this.getStreamDatas(json);
			if(datas.get("stream") != null){
				return true;
			}
			else{
				return false;
			}
		} catch (UnirestException e) {
			
		}
		
		return null;
		
	}
	
	private Map<String, Object> getStreamDatas(JSONObject json){
		 HashMap<String, Object> result = new HashMap<String, Object>();
		Iterator<?> keys = json.keys();
		while(keys.hasNext()){
		    String key = (String)keys.next();
		    result.put(key, json.get(key));
		}
		return result;
	}

}
