package se.mbaeumer.fxlink.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.CategoryCount;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Suggestion;
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

    private URLHelper urlHelper = new URLHelper();

    @Mock
    private LinkReadDBHandler linkReadDBHandler;

    @Test
    public void getSuggestions() {
        Link link = new Link("", "https://www.baeldung.com/jackson-kotlin", "");
        //Link link = new Link("", "www.test.com", "");
        suggestionHandler = new SuggestionHandler(suggestionDataHandler, urlHelper, linkReadDBHandler);

        List<Link> linksWithCategories = createLinkList();
        Mockito.when(linkReadDBHandler.getAllLinksWithCategories(any())).thenReturn(linksWithCategories);
        Map<String, List<CategoryCount>> originalSuggestionMap = new HashMap<>();
        originalSuggestionMap.put("algorithms", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("part", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("learning", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("cold", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("medium", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("recombee", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("evaluation", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("systems", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("recommender", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("machine", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("start", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("1", List.of(createCaegoryCount("machine learning", 1)));
        originalSuggestionMap.put("extension", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("functions", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("own", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("classes", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("csv", List.of(createCaegoryCount("Java", 1)));
        originalSuggestionMap.put("collections", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("write", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("combination", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("ch", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("kotlin", List.of(createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("videos", List.of(createCaegoryCount("Recipes", 1)));
        originalSuggestionMap.put("sleep", List.of(createCaegoryCount("Recipes", 1)));
        originalSuggestionMap.put("terripous", List.of(createCaegoryCount("Recipes", 1)));
        originalSuggestionMap.put("buzzfeedtasty", List.of(createCaegoryCount("Recipes", 1)));
        originalSuggestionMap.put("facebook", List.of(createCaegoryCount("Recipes", 1)));
        originalSuggestionMap.put("natural", List.of(createCaegoryCount("Recipes", 1)));
        originalSuggestionMap.put("remedies", List.of(createCaegoryCount("Recipes", 1)));
        originalSuggestionMap.put("buzzfeed", List.of(createCaegoryCount("Recipes", 1)));
        originalSuggestionMap.put("se", List.of(createCaegoryCount("product tips", 1)));
        originalSuggestionMap.put("indusiadesign", List.of(createCaegoryCount("product tips", 1)));
        originalSuggestionMap.put("java", List.of(createCaegoryCount("Java", 1)));
        originalSuggestionMap.put("bealdung", List.of(createCaegoryCount("Java", 1), createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("frankel", List.of(createCaegoryCount("Kotlin", 1), createCaegoryCount("Kotlin", 1)));
        originalSuggestionMap.put("elements", List.of(createCaegoryCount("Kotlin", 1), createCaegoryCount("Kotlin", 1)));

        Mockito.when(suggestionDataHandler.prepareData(linksWithCategories)).thenReturn(originalSuggestionMap);
        Mockito.when(suggestionDataHandler.removeStopWords(originalSuggestionMap)).thenReturn(removeStopWords(originalSuggestionMap));
        List<Suggestion> suggestions = suggestionHandler.getSuggestions(link);
        Suggestion expected = suggestions.stream().filter(suggestion -> "Kotlin".equals(suggestion.getCategory()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
        assertTrue(expected.getCount() == 1);
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

    private CategoryCount createCaegoryCount(final String category, final int count){
        CategoryCount categoryCount = new CategoryCount(category, count);
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