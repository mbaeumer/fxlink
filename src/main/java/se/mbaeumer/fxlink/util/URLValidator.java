package se.mbaeumer.fxlink.util;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLValidator {
	public static final String HTTP = "http://";
	public static final String HTTPS = "https://";
	public static final String WWW = "www.";

	public static boolean isValidURL(String url){
		if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)){
			url = HTTP + url;
		}

		String temporaryUrl = url;
		if (temporaryUrl.contains(WWW)){
			temporaryUrl = temporaryUrl.replace(WWW, "");
		}

		if (!temporaryUrl.contains(".")){
			 return false;
		}else{
			String domain = temporaryUrl.substring(temporaryUrl.indexOf("."));
			if (domain.length() <= 2){
				return false;
			}
		}
		UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		return urlValidator.isValid(url);
	}
}
