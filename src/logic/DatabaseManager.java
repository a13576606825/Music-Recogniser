package logic;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import utility.Utils;

import com.google.gson.Gson;



public class DatabaseManager {
	
	
	
	private static Gson gson = new Gson();
	private final static String databasePath = Utils.tempFolder + "dataHashMap.json";
	
	public static void storeDataHashMap(Map<Long, List<DataPoint>> dataHashMap) {
		DataHashMap map = new DataHashMap(dataHashMap); 
		String str = gson.toJson(map);
		try {
			//write converted json data to a file named "file.json"
			File jsonFile = new File(databasePath);
			if(jsonFile.exists()) {
				jsonFile.delete();
			}
			jsonFile.createNewFile();
			FileWriter writer = new FileWriter(jsonFile);
			writer.write(str);
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public static Map<Long, List<DataPoint>> readDataHashMap() {
		try {
			File f = new File(databasePath);
			if(f.isFile() && f.exists()){
				BufferedReader br = new BufferedReader(
					new FileReader(f));

				//convert the json string back to object
				DataHashMap obj = gson.fromJson(br, DataHashMap.class);
				return obj.getDataHashMap();
			}
		

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
   

}
