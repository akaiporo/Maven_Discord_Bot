package Tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Member;

public class JsonFunctions {

	private JSONObject readenFile;
	private FileWriter writtenFile;
	private String authorName;
	private JSONArray authorLinks;
	private static final String LINKSBYUSER_FILENAME = "linksByUser.json";
	
	public JsonFunctions(String name) {
		try {
			FileReader fr = new FileReader(LINKSBYUSER_FILENAME);
			/**
			 * Building the json object
			 */
			StringBuilder sb = new StringBuilder();
		    String line;
		    BufferedReader br = new BufferedReader(fr);
		    while ((line = br.readLine()) != null) {
		        sb.append(line);
		    }
		    br.close();
		    readenFile = new JSONObject(sb.toString());
		    /**
		     * Setting the parameters
		     */
			authorName = name;
			this.setAuthorLinks(authorName);
		} catch (FileNotFoundException e) {
			System.err.println(String.format("File named %s could not be found.", JsonFunctions.LINKSBYUSER_FILENAME));
		} catch (IOException e) {
			System.err.println(String.format("File named %s could not be read.", JsonFunctions.LINKSBYUSER_FILENAME));
		}
	}
	
	public JSONArray getAuthorLinks() {
		System.out.println(authorLinks);
		return authorLinks;
	}
	private void setAuthorLinks(String name) throws IOException{
		if(readenFile.has(name)){
			//Contains only the links posted by the requested user
			authorLinks = readenFile.getJSONArray(authorName); 
		}
		else{
			readenFile.put(name, new JSONArray());
			try {
				this.writtenFile = new FileWriter(LINKSBYUSER_FILENAME);
				writtenFile.write(readenFile.toString());
			} catch (IOException e) {
				System.out.println("Couldn't write in the file");
			}
			finally {
				writtenFile.close();
			}
		}
	}
	public JSONObject getReadenFile() {
		return readenFile;
	}
	public String getAuthorName() {
		return authorName;
	}
	public void addLink(String s) throws IOException{
		authorLinks.put(s);
		try {
			this.writtenFile = new FileWriter(LINKSBYUSER_FILENAME);
			writtenFile.write(readenFile.toString());
		} catch (IOException e) {
			System.out.println("Couldn't write in the file");
		}
		finally {
			writtenFile.close();
		}
	}
	
	public void addAllGuildMembers(JDA jda){
		for(Member m : jda.getGuildById("234661550282113026").getMembers()){
			try {
				this.setAuthorLinks(m.getUser().getName());
			} catch (IOException e) {
				System.out.println("Couldn't write in the file for user "+m.getUser().getName());
			}
		}
	}

}
