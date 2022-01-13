package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.LinkTitleUtil;

import java.io.IOException;

public class TitleHandler {
    private LinkTitleUtil linkTitleUtil;
    private YoutubeCrawler youtubeCrawler;

    public TitleHandler(LinkTitleUtil linkTitleUtil, YoutubeCrawler youtubeCrawler) {
        this.linkTitleUtil = linkTitleUtil;
        this.youtubeCrawler = youtubeCrawler;
    }

    public String generateTitle(Link link){
        boolean youtubeSuccess = false;
        String title = null;
        // if it is youtube then try to generate the youtube title
        if (isYoutubeLink(link)){
            try {
                title = youtubeCrawler.getTitle(link.getURL());
                youtubeSuccess = true;
            } catch (IOException e) {

            }
        }

        if (!youtubeSuccess){
            title = linkTitleUtil.generateTitle(link);
        }

        return title;
    }

    private boolean isYoutubeLink(Link link){
        return link.getURL().startsWith("http://www.youtube.com") || link.getURL().startsWith("https://www.youtube.com");
    }
}
