package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.models.CategoryCount;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Suggestion;
import se.mbaeumer.fxlink.util.LinkSplitter;

import java.util.*;
import java.util.stream.Collectors;

public class SuggestionHandler {

    private final SuggestionDataHandler suggestionDataHandler;
    private final LinkSplitter linkSplitter;
    private final LinkReadDBHandler linkReadDBHandler;

    public SuggestionHandler(SuggestionDataHandler suggestionDataHandler, LinkSplitter linkSplitter, LinkReadDBHandler linkReadDBHandler) {
        this.suggestionDataHandler = suggestionDataHandler;
        this.linkSplitter = linkSplitter;
        this.linkReadDBHandler = linkReadDBHandler;
    }

    public List<Suggestion> getSuggestions(Link link){
        List<Link> allLinks = linkReadDBHandler.getAllLinksWithCategories(GenericDBHandler.getInstance());
        List<Link> linksWithCategories = allLinks.stream().filter(l -> l.getCategory()!=null)
                .collect(Collectors.toList());
        Map<String, List<CategoryCount>> map = suggestionDataHandler.prepareData(linksWithCategories);
        map = suggestionDataHandler.removeStopWords(map);

        return createSuggestions(map, link);
    }

    public List<Suggestion> createSuggestions(final Map<String, List<CategoryCount>> map, final Link link){
        List<Suggestion> suggestions = new ArrayList<>();
        String[] urlParts = linkSplitter.splitToData(link);

        for (String word : urlParts){
            List<CategoryCount> categoryCounts = map.get(word.toLowerCase());
            if (categoryCounts == null){
                continue;
            }
            categoryCounts.forEach(categoryCount ->{
                Optional<Suggestion> matchingSuggestion = suggestions
                        .stream()
                         .filter(suggestion -> suggestion.getCategory().equalsIgnoreCase(categoryCount.getCategory()))
                        .findFirst();
                if (matchingSuggestion.isPresent()){
                    matchingSuggestion.get().increaseCount();
                }else {
                    suggestions.add(new Suggestion(categoryCount.getCategory(), 1));
                }
            });
        }

        return suggestions.stream().sorted(Comparator.comparing(Suggestion::getCount).reversed()).collect(Collectors.toList());
    }
}