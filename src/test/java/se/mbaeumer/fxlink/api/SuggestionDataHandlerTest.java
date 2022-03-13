package se.mbaeumer.fxlink.api;

import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.CategoryCount;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.LinkSplitter;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SuggestionDataHandlerTest {

    private SuggestionDataHandler suggestionDataHandler;

    @Test
    public void prepareData() {
        suggestionDataHandler = new SuggestionDataHandler(new LinkSplitter(new URLHelper()));
        Map<String, List<CategoryCount>> suggestionMap = suggestionDataHandler.prepareData(createLinkList());
        assertEquals(suggestionMap.keySet().size(), 43);
        System.out.println();
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