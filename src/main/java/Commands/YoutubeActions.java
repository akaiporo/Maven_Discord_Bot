package Commands;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Lists;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;


public class YoutubeActions {

	private static YouTube youtube;
	private static final String PROPERTIES_FILENAME = "/youtube.properties";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 25;
	
	List<String> scopes = Lists.newArrayList();

	public YoutubeActions() throws IOException {
		scopes.add("https://www.googleapis.com/auth/youtube");
		//Credential credential = Auth.authorize(scopes, "youtubeactions");
		
        // This object is used to make YouTube Data API requests.
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("Discord bot").build();
       
        	
	}
	
	/**
	 * 
	 * @param textQuery
	 * @param maxResults
	 * @param filter
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public List<String> retrieveVideoUrl(String textQuery, int maxResults, boolean filter, int timeout) 
			throws Exception {
		
		return new ArrayList<String>();
	
	}
	/**
	 * Search the request terms in Youtube
	 * @param s -> Query
	 * @return The query result
	 * @throws IOException
	 */
	public String search(String s) throws IOException {
        // Read the developer key from the properties file.
        Properties properties = new Properties();
        try {
            InputStream in = getClass().getResourceAsStream(PROPERTIES_FILENAME); 
            properties.load(in);

        } catch (IOException e) {
            System.err.println("There was an error reading " + PROPERTIES_FILENAME + ": " + e.getCause()
                    + " : " + e.getMessage());
        }
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("").build();

            // Prompt the user to enter a query term.
            String queryTerm = s;

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = properties.getProperty("youtube.apikey");
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

               return prettyPrint(searchResultList.iterator(), queryTerm);
    }
	/**
	 * Iterate through the results, dans take the last one (So, the first video in the list)
	 * @param iteratorSearchResults
	 * @param query
	 * @return
	 */
	 private String prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

	        if (!iteratorSearchResults.hasNext()) {
	            System.out.println(" There aren't any results for your query.");
	        }

	        while (iteratorSearchResults.hasNext()) {

	            SearchResult singleVideo = iteratorSearchResults.next();
	            ResourceId rId = singleVideo.getId();

	            // Confirm that the result represents a video. Otherwise, the
	            // item will not contain a video ID.
	            if (rId.getKind().equals("youtube#video")) {

	                String vid = "https://www.youtube.com/watch?v=" + rId.getVideoId();
	                return vid;
	            }
	        }
	        return "no results";
	    }
	
}
