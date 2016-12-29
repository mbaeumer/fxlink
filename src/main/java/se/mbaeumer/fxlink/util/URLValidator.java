package se.mbaeumer.fxlink.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLValidator {
	public static boolean isValidURL(String url){
		String oldRegex = "^(http://|https://)?(www.)?([a-zA-Z0-9]+).([a-zA-Z0-9]+)*.[a-z]{2,3}.?([a-zA-Z0-9]+)?$";
		String regex = "^(http://|https://)?(www.)?([a-zA-Z0-9]+).([a-zA-Z0-9]+)*.[a-z]{2,3}.?([a-zA-Z0-9]+).*$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(url);
		return m.matches();
	}
}
