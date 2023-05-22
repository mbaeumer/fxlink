package se.mbaeumer.fxlink.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.FollowUpStatusReadDBHandler;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.CategoryCount;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Suggestion;
import se.mbaeumer.fxlink.util.LinkSplitter;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class SuggestionHandlerTest {

    private SuggestionHandler suggestionHandler;

    @Mock
    private SuggestionDataHandler suggestionDataHandler;

    @Mock
    private FollowUpStatusReadDBHandler followUpStatusReadDBHandler;

    private URLHelper urlHelper = new URLHelper();

    @Mock
    private LinkReadDBHandler linkReadDBHandler;

    @Test
    public void getSuggestions() {
        Link link = new Link("", "https://www.baeldung.com/jackson-kotlin", "");
        LinkSplitter linkSplitter = new LinkSplitter(this.urlHelper);
        suggestionHandler = new SuggestionHandler(suggestionDataHandler, linkSplitter, linkReadDBHandler, followUpStatusReadDBHandler);

        Map<String, List<CategoryCount>> originalSuggestionMap = createMap();
        List<Link> linksWithCategories = createLinkList();
        Mockito.when(linkReadDBHandler.getAllLinksWithCategories(any(), any())).thenReturn(linksWithCategories);

        Mockito.when(suggestionDataHandler.prepareData(linksWithCategories)).thenReturn(originalSuggestionMap);
        Mockito.when(suggestionDataHandler.removeStopWords(originalSuggestionMap)).thenReturn(removeStopWords(originalSuggestionMap));
        List<Suggestion> suggestions = suggestionHandler.getSuggestions(link);
        Suggestion expected = suggestions.stream().filter(suggestion -> "Kotlin".equals(suggestion.getCategory()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        assertEquals(1, expected.getCount());
    }

    @Test
    public void testCaseInsensitive(){
        Link link = new Link("", "http://www.java2s.com/Code/Java/JavaFX/GridPanewherecolumnstake255025ofitswidth.htm", "");
        LinkSplitter linkSplitter = new LinkSplitter(this.urlHelper);
        suggestionHandler = new SuggestionHandler(suggestionDataHandler, linkSplitter, linkReadDBHandler, followUpStatusReadDBHandler);

        Map<String, List<CategoryCount>> originalSuggestionMap = createMap();
        List<Link> linksWithCategories = createLinkList();
        Mockito.when(linkReadDBHandler.getAllLinksWithCategories(any(), any())).thenReturn(linksWithCategories);

        Mockito.when(suggestionDataHandler.prepareData(linksWithCategories)).thenReturn(originalSuggestionMap);
        Mockito.when(suggestionDataHandler.removeStopWords(originalSuggestionMap)).thenReturn(removeStopWords(originalSuggestionMap));
        List<Suggestion> suggestions = suggestionHandler.getSuggestions(link);
        Suggestion expected = suggestions.stream().filter(suggestion -> "JavaFX".equals(suggestion.getCategory()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        assertEquals(1, expected.getCount());
    }

    private Map<String, List<CategoryCount>> createMap(){
        Map<String, List<CategoryCount>> originalSuggestionMap = new HashMap<>();
        originalSuggestionMap.put("algorithms", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("part", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("learning", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("cold", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("medium", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("recombee", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("evaluation", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("systems", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("recommender", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("machine", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("start", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("1", List.of(createCategoryCount("machine learning")));
        originalSuggestionMap.put("extension", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("functions", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("own", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("classes", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("csv", List.of(createCategoryCount("Java")));
        originalSuggestionMap.put("collections", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("write", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("combination", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("ch", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("kotlin", List.of(createCategoryCount("Kotlin")));
        originalSuggestionMap.put("videos", List.of(createCategoryCount("Recipes")));
        originalSuggestionMap.put("sleep", List.of(createCategoryCount("Recipes")));
        originalSuggestionMap.put("terripous", List.of(createCategoryCount("Recipes")));
        originalSuggestionMap.put("buzzfeedtasty", List.of(createCategoryCount("Recipes")));
        originalSuggestionMap.put("facebook", List.of(createCategoryCount("Recipes")));
        originalSuggestionMap.put("natural", List.of(createCategoryCount("Recipes")));
        originalSuggestionMap.put("remedies", List.of(createCategoryCount("Recipes")));
        originalSuggestionMap.put("buzzfeed", List.of(createCategoryCount("Recipes")));
        originalSuggestionMap.put("se", List.of(createCategoryCount("product tips")));
        originalSuggestionMap.put("indusiadesign", List.of(createCategoryCount("product tips")));
        originalSuggestionMap.put("java", List.of(createCategoryCount("Java")));
        originalSuggestionMap.put("javafx", List.of(createCategoryCount("JavaFX")));
        originalSuggestionMap.put("bealdung", List.of(createCategoryCount("Java"), createCategoryCount("Kotlin")));
        originalSuggestionMap.put("frankel", List.of(createCategoryCount("Kotlin"), createCategoryCount("Kotlin")));
        originalSuggestionMap.put("elements", List.of(createCategoryCount("Kotlin"), createCategoryCount("Kotlin")));

        return originalSuggestionMap;
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

    private CategoryCount createCategoryCount(final String category){
        CategoryCount categoryCount = new CategoryCount(category, 1);
        return categoryCount;
    }

    public Map<String, List<CategoryCount>> removeStopWords(final Map<String, List<CategoryCount>> map){

        Set<String> toExclude = map.keySet().stream()
                .filter(key -> "for".equals(key) || key.length() <=1 || "of".equals(key) || "with".equals(key)
                        || "com".equals(key) || "de".equals(key) || key.matches("\\d+")
                        || "html".equals(key) || "htm".equals(key))
                .collect(Collectors.toSet());
        map.keySet().removeAll(toExclude);

        return map;
    }
}