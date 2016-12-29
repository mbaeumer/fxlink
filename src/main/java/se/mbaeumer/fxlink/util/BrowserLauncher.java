package se.mbaeumer.fxlink.util;

import java.awt.*;
import java.net.URI;
import java.util.Arrays;

/**
 * A utility class to enable platform-independent browser handling
 * @author Martin B�umer (martin.baeumer@gmail.com)
 *
 */
public class BrowserLauncher {

	static final String[] browsers = { "google-chrome", "firefox", "opera", "epiphany", "konqueror", "conkeror", "midori", "kazehakase", "mozilla" }; 
	static final String errMsg = "Error attempting to launch web browser"; 
	
	/**
	 * Opens as given URL
	 * @param url the URL as string
	 * @throws Exception
	 */
	public static void openURL(String url) throws Exception {
		String osName = System.getProperty("os.name"); 
		if (osName.startsWith("Mac OS")) { 
			Class.forName("com.apple.eio.FileManager").getDeclaredMethod( "openURL", new Class[] {String.class}).invoke(null, new Object[] {url}); 
		}else if (osName.startsWith("Windows")) { 
			Desktop.getDesktop().browse(new URI(url)); 
		}else { //assume Unix or Linux 
			String browser = null; 
			for (String b : browsers) 
				if (browser == null && Runtime.getRuntime().exec(new String[] {"which", b}).getInputStream().read() != -1) {
					Runtime.getRuntime().exec(new String[] {browser = b, url}); 
				}
			if (browser == null){
				throw new Exception(Arrays.toString(browsers));
			}
		}
	}
}

