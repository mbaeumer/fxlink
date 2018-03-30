package se.mbaeumer.fxlink.util;

import se.mbaeumer.fxlink.models.Link;

public class DescriptionUtilImpl implements DescriptionUtil {

    @Override
    public String generateDescription(Link link) {
        String url = "";
        String description = null;
        url = removeTheProtocol(link);

        String[] urlParts = url.split("/");
        if (urlParts.length == 1){
            description = link.getDescription();
        }else if (urlParts.length > 1){
            description = urlParts[urlParts.length-1];
            description = description.replaceAll("-", " ");
            description = description.replaceAll("_", " ");
            description = handleSuffix(description);
        }
        return description;
    }

    @Override
    public String removeTheProtocol(Link link) {
        int startIndex = 7;
        if (link.getURL().startsWith("https://")){
            startIndex = 8;
        }
        return link.getURL().substring(startIndex);
    }

    @Override
    public String handleSuffix(String description) {
        description = description.replace(".html", "");
        description = description.replace(".pdf", " [pdf]");
        return description;
    }
}
