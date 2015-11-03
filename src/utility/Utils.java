package utility;

public class Utils {
	private static boolean isDebugMode = true;
	
	public static String testFolder = "toIgnore/test";
	public static String trainFolder = "toIgnore/train";
	
	public static void debug(String info) {
		if(isDebugMode) {
			System.out.println(info);
		}
	}
}
