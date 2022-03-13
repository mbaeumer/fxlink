package se.mbaeumer.fxlink.util;

import se.mbaeumer.fxlink.models.Link;

public class LinkSplitter {

    private final URLHelper urlHelper;

    public LinkSplitter(URLHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    public String[] splitToData(final Link link){
        String[] data;
        // youtube, but not playlist
        if (link.getURL().contains("youtube.com") && !link.getURL().contains("playlist")){
            data = link.getTitle().split(" ");
        }else{
            String url = urlHelper.withoutProtocol(link.getURL());
            url = urlHelper.withoutPrefix(url);
            data = urlHelper.getUrlParts(url);
        }

        return data;
    }
}
