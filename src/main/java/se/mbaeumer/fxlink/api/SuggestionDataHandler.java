package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.models.CategoryCount;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.*;
import java.util.stream.Collectors;

public class SuggestionDataHandler {

    private URLHelper urlHelper;

    public SuggestionDataHandler(URLHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    public Map prepareData(final List<Link> links){
        Map<String, List<CategoryCount>> hash = new HashMap();
        for (Link link : links){
            String url = urlHelper.withoutProtocol(link.getURL());
            url = urlHelper.withoutPrefix(url);
            String[] urlParts = urlHelper.getUrlParts(url);
            for (String word : urlParts){
                List<CategoryCount> categoryCounts = hash.get(word);

                if (categoryCounts == null) {
                    hash.put(word, initList(link.getCategory().getName()));
                }else{
                    Optional<CategoryCount> match = categoryCounts.stream()
                            .filter(categoryCount -> categoryCount.getCategory().equalsIgnoreCase(link.getCategory().getName()))
                            .findFirst();

                    if (match.isPresent()){
                        CategoryCount categoryCount = match.get();
                        categoryCount.increaseCount();

                    }else{
                        categoryCounts.add(new CategoryCount(link.getCategory().getName(), 1));
                    }
                }
            }
        }
        return hash;
    }

    public Map removeStopWords(final Map<String, List<CategoryCount>> map){

        Set<String> toExclude = map.keySet().stream()
                .filter(key -> "for".equals(key) || key.length() <=1 || "of".equals(key) || "with".equals(key)
                        || "com".equals(key) || "de".equals(key) || key.matches("\\d+"))
                .collect(Collectors.toSet());
        map.keySet().removeAll(toExclude);

        return map;
    }

    private List<CategoryCount> initList(final String categoryName){
        List<CategoryCount> categoryCounts = new ArrayList<>();
        categoryCounts.add(new CategoryCount(categoryName, 1));
        return categoryCounts;
    }
}