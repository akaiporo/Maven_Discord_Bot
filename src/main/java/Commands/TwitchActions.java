package Commands;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;

import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class TwitchActions extends TimerTask{

	private static final String PROPERTIES_FILENAME = "/twitch.properties";
	private static String apiKey;
	private List<String> channel = new ArrayList<String>();
	protected PropertyChangeSupport propertyChangeSupport;
	private boolean streamIsLive = false;
	
	/**
	 * Set up the twitch apikey, from the twitch.properties file
	 */
	public TwitchActions() {
		
		propertyChangeSupport = new PropertyChangeSupport(this);
		
		channel.add("raze_sora");
		
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
	/**
	 * Same as the run method, but can be called manually, to check if a channel is live or not
	 * @return false or true, depending if the stream is live or not
	 * @throws IOException
	 */
	public Boolean getLiveStream() throws IOException{
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.twitch.tv/kraken/streams/"+channel.get(0))
					  .header("accept", "application/json")
					  .header("Client-ID", apiKey)
					  .queryString("stream_type", "live")
					  .asJson();
			JSONObject json = jsonResponse.getBody().getObject();
			Map<String, Object> datas = this.getStreamDatas(json);
			if(!datas.get("stream").toString().equals("null")){
				return true;
			}
			else{
				return false;
			}
		} catch (UnirestException e) {
			
		}
		
		return null;
		
	}
	
	/**
	 * Return an Object as value because it can be castable as a String, or as a String array
	 * @param json
	 * @return a Map of <String, Object> (representing the Key>Value of the json passed)
	 */
	private Map<String, Object> getStreamDatas(JSONObject json){
		 HashMap<String, Object> result = new HashMap<String, Object>();
		Iterator<?> keys = json.keys();
		while(keys.hasNext()){
		    String key = (String)keys.next();
		    result.put(key, json.get(key));
		}
		return result;
	}
	/**
	 * Check if a Twich stream is live
	 * If it is, check if it was already live at the previous iteration
	 * If not, trigger an event "channelIsLive" to the BOT MessageListenerActions
	 */
	@Override
	public void run() {
		try {
			HttpResponse<JsonNode> jsonResponse = Unirest.get("https://api.twitch.tv/kraken/streams/"+channel.get(0))
					  .header("accept", "application/json")
					  .header("Client-ID", apiKey)
					  .queryString("stream_type", "live")
					  .asJson();
			JSONObject json = jsonResponse.getBody().getObject();
			Map<String, Object> datas = this.getStreamDatas(json);
			if(!datas.get("stream").toString().equals("null")){
				this.triggerChannelIsLiveEvent(channel.get(0));
				this.streamIsLive = true;
			}
			else{
				this.streamIsLive = false;
			}
		} catch (UnirestException e) {
			
		}
	}
	/**
	 * Trigger "channelIsLive" event
	 * @param channel : name of the channel that went live
	 */
	public void triggerChannelIsLiveEvent(String channel) {
		if(!this.streamIsLive){
			propertyChangeSupport.firePropertyChange(channel, true, true);
		}
	}

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

}
