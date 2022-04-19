package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.models.CategoryCount;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.LinkSplitter;
import se.mbaeumer.fxlink.util.StopWordHandler;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.*;
import java.util.stream.Collectors;

public class SuggestionDataHandler {

    private final LinkSplitter linkSplitter;
    private final StopWordHandler stopWordHandler;

    public SuggestionDataHandler(LinkSplitter linkSplitter, StopWordHandler stopWordHandler) {
        this.linkSplitter = linkSplitter;
        this.stopWordHandler = stopWordHandler;
    }

    public Map<String, List<CategoryCount>> prepareData(final List<Link> links){
        Map<String, List<CategoryCount>> hash = new HashMap();
        for (Link link : links){
            String[] urlParts = linkSplitter.splitToData(link);
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

    public Map<String, List<CategoryCount>> removeStopWords(final Map<String, List<CategoryCount>> map){

        /* TODO: Use StopWordHandler here */
        return stopWordHandler.removeStopWordsFromMap(map);
        /*
        Set<String> toExclude = map.keySet().stream()
                .filter(key -> "for".equals(key) || key.length() <=1 || "of".equals(key) || "with".equals(key)
                        || "com".equals(key) || "de".equals(key) || key.matches("\\d+")
                        || "html".equals(key) || "htm".equals(key))
                .collect(Collectors.toSet());
        map.keySet().removeAll(toExclude);

        return map;

         */
    }

    private List<CategoryCount> initList(final String categoryName){
        List<CategoryCount> categoryCounts = new ArrayList<>();
        categoryCounts.add(new CategoryCount(categoryName, 1));
        return categoryCounts;
    }
}