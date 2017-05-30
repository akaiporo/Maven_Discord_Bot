package Tools;

import java.util.ArrayList;

import org.json.JSONArray;

public final class Tools {
	
	public static ArrayList<Object> jsonArrayToArrayList(JSONArray json){
		ArrayList<Object> list = new ArrayList<Object>();
		int len = json.length();
		for (int i=0;i<len;i++){ 
		   list.add(json.get(i).toString());
		} 
		return list;
	}

}
