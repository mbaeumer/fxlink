package se.mbaeumer.fxlink.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.LinkSplitter;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class WordCountHandlerTest {

    private WordCountHandler wordCountHandler;

    @Mock
    private LinkReadDBHandler linkReadDBHandler;

    private LinkSplitter linkSplitter;

    @Test
    public void matchString() {
        WordCountHandler wch = new WordCountHandler(null, null);
        Assert.assertFalse(wch.matchString("upload"));
        Assert.assertTrue(wch.matchString("1235"));
        Assert.assertTrue(wch.matchString("73dttfyg"));
        Assert.assertTrue(wch.matchString("dtt88fyg"));
    }

    @Test
    public void getWordCount() {
        linkSplitter = new LinkSplitter(new URLHelper());
        wordCountHandler = new WordCountHandler(linkReadDBHandler, linkSplitter);
        Mockito.when(linkReadDBHandler.getAllLinks(any(), any())).thenReturn(createLinkList());
        Map<String, Integer> wordCount = wordCountHandler.getWordCount();
        Optional<String> keyFacebook = wordCount.keySet().stream()
                .filter( k -> "facebook".equals(k))
                .findFirst();
        Assert.assertTrue(keyFacebook.isPresent());
        int count = wordCount.get("facebook");
        Assert.assertEquals(2, count);
    }

    private List<Link> createLinkList(){
        List<Link> links = new ArrayList<>();

        links.add(createLink("https://www.facebook.com/buzzfeedtasty/videos/276402623429801/", 1, "Recipes"));
        links.add(createLink("https://www.facebook.com/buzzfeedtasty/videos/276402623429834/", 1, "Recipes"));
        links.add(createLink("https://www.buzzfeed.com/terripous/natural-sleep-remedies", 1, "Recipes"));
        links.add(createLink("https://www.indusiadesign.se/", 2, "product tips"));
        links.add(createLink("https://blog.frankel.ch/write-extension-functions-own-classes-kotlin/", 3, "Kotlin"));
        links.add(createLink("https://medium.com/recombee-blog/machine-learning-for-recommender-systems-part-1-algorithms-evaluation-and-cold-start-6f696683d0ed", 4,
                "machine learning"));
        links.add(createLink("https://www.baeldung.com/elements-combination-kotlin-collections/", 5, "Kotlin"));
        links.add(createLink("https://www.baeldung.com/java-csv", 6, "Java"));
        links.add(createLink("http://tutorials.jenkov.com/javafx/datepicker.html", 9, "JavaFX"));
        return links;
    }

    private Link createLink(final String url, final int id, final String categoryName){
        Link link = new Link("", url, "");
        link.setCategory(createCategory(id, categoryName));
        return link;
    }

    private Category createCategory(final int id, final String name){
        Category category = new Category();
        category.setId(id);
        category.setName(name);
        return category;
    }
}