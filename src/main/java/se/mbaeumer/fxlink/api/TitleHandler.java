package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.LinkTitleUtil;

import java.io.IOException;

public class TitleHandler {
    private final LinkTitleUtil linkTitleUtil;
    private final YoutubeCrawler youtubeCrawler;

    public TitleHandler(LinkTitleUtil linkTitleUtil, YoutubeCrawler youtubeCrawler) {
        this.linkTitleUtil = linkTitleUtil;
        this.youtubeCrawler = youtubeCrawler;
    }

    public String generateTitle(Link link){
        boolean youtubeSuccess = true;
        String title = null;
        if (isYoutubeLink(link)){
            try {
                title = youtubeCrawler.getTitle(link.getURL());
            } catch (IOException e) {
                youtubeSuccess = false;
            }
        }else{
            title = linkTitleUtil.generateTitle(link);
        }

        if (!youtubeSuccess){
            title = linkTitleUtil.generateTitle(link);
        }

        return title;
    }

    private boolean isYoutubeLink(Link link){
        return (link.getURL().startsWith("http://www.youtube.com") || link.getURL().startsWith("https://www.youtube.com"))
                && link.getURL().contains("watch");
    }
}
