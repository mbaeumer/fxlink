package se.mbaeumer.fxlink.api;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class YoutubeCrawler {

    public String getTitle(final String url) throws IOException {
        Document document = Jsoup.connect(url).get();
        //3. Parse the HTML to extract links to other URLs
        String title = document.title();
        Elements el = document.select("div#watch7-content");
        Elements el2 = el.select("meta[itemprop=name]");
        String tit2 = el2.get(0).attr("content");
        return tit2.replaceAll("\"", "").replaceAll("\'", "");
    }
}
