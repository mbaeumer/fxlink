package se.mbaeumer.fxlink.util;

/**
 * Created by martinbaumer on 28/03/16.
 */
public class URLHelper {
    public static String getBaseURL(String url){
        String result = null;

        if (url != null){
            int index = 0;
            if (url.startsWith("http://") || url.startsWith("https://")){
                result += url;
                index = result.length();
            }
        }
        return result;
    }
}
