package logic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import utility.Utils;


// in charge of train database music files
public class FileSystem {
	private static HashMap<String, Integer> SongNameIdMap;
	private static ArrayList<String> SongNameList;
	
	private static AudioFilter filter = new AudioFilter();
	private static String infoFilePath = "musicalFileList.txt";
	
	private static boolean isInit = false;
	
	
	public static int getSongId(String songName) {
		
		if(!isInit) {
			init();
		}
		if(SongNameIdMap.containsKey(songName)) {
			return SongNameIdMap.get(songName);
		}
		return -1;
	}
	
	public static String getSongById(int id) {
		if(!isInit) {
			init();
		}
		if(id <0) {
			return "Fail to find any song";
		}
		if(SongNameList.size() <= id) {
			return " empty sring";
		}
		return SongNameList.get(id);
	}
	
	public static ArrayList<File> getSongList() {
		if(!isInit) {
			init();
		} 
		ArrayList<File> songList = new ArrayList<File>();
		for (String songName : SongNameList) {
			File song = new File(Utils.trainFolder+songName);
			if(song.exists() && filter.accept(song)) {
				songList.add(song);
			}
		}
		Utils.debug("songlist size " + songList.size());
		return songList;
	}
	
	private static void reconstructFileSystem() {
		SongNameIdMap = new HashMap<String, Integer>();
		SongNameList = new ArrayList<String>();
		Utils.debug("-----Start walk through train file ------");
		walk(new File(Utils.trainFolder));
		Utils.debug("-----end walk through train file ------");
		try {
			File file = new File(Utils.tempFolder + infoFilePath);

			// if file doesnt exists, then create it
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			for(String fileName: SongNameList) {
				bw.write(fileName);
				bw.newLine();
			}
			
			
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void init() {
		Utils.debug("init fileSystem");
		SongNameIdMap = new HashMap<String, Integer>();
		SongNameList = new ArrayList<String>();
		
		File file = new File(Utils.tempFolder + infoFilePath);
		// if file doesnt exists, then create it
		if (!file.exists()) {
			Utils.debug("reconstruct FileSystem");
			reconstructFileSystem();
		} else {
			try {
				

				FileReader fr = new FileReader(file.getAbsoluteFile());
				BufferedReader br = new BufferedReader(fr);
				
				String fileName;
				int id = 0;
				while( (fileName=br.readLine()) != null) {
					SongNameList.add(fileName);
					SongNameIdMap.put(fileName, id);
					id++;
				}
				
				
				isInit = true;
				Utils.debug("init fileSystem completed");
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	// only walk when file system need to be constructed
	private static void walk(File root) {
        File[] list = root.listFiles();

        if (list == null) return;

        for ( File f : list ) {
            if (f.isDirectory()) {
                walk(f);
                
            }
            else {
            	if(filter.accept(f)) {
            		String fileName = f.getName();
            		int id = SongNameList.size();
    				SongNameList.add(fileName);
            		SongNameIdMap.put(fileName, id);
            		Utils.debug(" walk through file: " + fileName + " with id " + id);
            	}
            	
            }
        }
	}
}
