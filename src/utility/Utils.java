package utility;

import java.io.File;

public class Utils {
	private static boolean isDebugMode = true;
	
	public static String testFolder = "toIgnore/test/";
	public static String trainFolder = "toIgnore/train/";
	public static String tempFolder= "toIgnore/temp";
	public static String recordingFilePath= "toIgnore/temp/recording.wav";

	
	public final static String mp3 = "mp3";
	public final static String wav = "wav";
	
	
	public static void debug(String info) {
		if(isDebugMode) {
			System.out.println(info);
		}
	}
	public static void debug(byte[] array) {
		for(byte b: array) {
			if(isDebugMode) {
				System.out.print((int)b+" ");
			}
		}
		System.out.println("");
	}
	
	 public static String getExtension(File f) {
	        String ext = null;
	        String s = f.getName();
	        int i = s.lastIndexOf('.');

	        if (i > 0 &&  i < s.length() - 1) {
	            ext = s.substring(i+1).toLowerCase();
	        }
	        return ext;
	    }
}
