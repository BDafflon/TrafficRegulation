package eu.fr.ucbl.disp.trafficregulation.util;

/**
 * Store and provide display status for debug lines and GUI in the instance
 * 
 * @author Etienne François
 */
public class DebugUtil {
	private static boolean debugMode = false;
	private static boolean displayMode = false;
	
	private DebugUtil(){}
	
	public static void setMode(boolean debug, boolean display){
		debugMode = debug;
		displayMode = display;
	}
	
	public static boolean isDebugMode(){
		return debugMode;
	}
	
	public static boolean isDisplayMode(){
		return displayMode;
	}
}
