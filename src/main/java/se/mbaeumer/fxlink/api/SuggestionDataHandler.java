package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.models.CategoryCount;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.LinkSplitter;
import se.mbaeumer.fxlink.util.StopWordHandler;

import java.util.*;

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
        return stopWordHandler.removeStopWordsFromMap(map);
    }

    private List<CategoryCount> initList(final String categoryName){
        List<CategoryCount> categoryCounts = new ArrayList<>();
        categoryCounts.add(new CategoryCount(categoryName, 1));
        return categoryCounts;
    }
}