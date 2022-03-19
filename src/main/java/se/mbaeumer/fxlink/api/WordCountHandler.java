package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.LinkSplitter;

import java.util.*;
import java.util.stream.Collectors;

public class WordCountHandler {

    private final LinkReadDBHandler linkReadDBHandler;
    private final LinkSplitter linkSplitter;

    public WordCountHandler(LinkReadDBHandler linkReadDBHandler, LinkSplitter linkSplitter) {
        this.linkReadDBHandler = linkReadDBHandler;
        this.linkSplitter = linkSplitter;
    }

    public Map<String, Integer> getWordCount(){
        // read all links
        List<Link> allLinks = linkReadDBHandler.getAllLinks(GenericDBHandler.getInstance());
        List<String> wordList = new ArrayList<>();
        for (Link link : allLinks){
            String[] urlParts = linkSplitter.splitToData(link);
            wordList.addAll(Arrays.asList(urlParts));
        }
        // create word list
        // create word count map
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : wordList){
            Integer value = wordCount.get(word);
            int newValue = value == null ? 1 : value + 1;
            wordCount.put(word, newValue);
        }

        Set<String> toExclude = wordCount.keySet().stream()
                .filter(key -> "for".equals(key) || key.length() <=1 || "of".equals(key) || "with".equals(key)
                        || "the".equals(key)
                        || "com".equals(key) || "de".equals(key) || key.matches(".*\\d.*")
                        || "html".equals(key) || "htm".equals(key))
                .collect(Collectors.toSet());

        wordCount.keySet().removeAll(toExclude);

        Map<String, Integer> set = wordCount.entrySet()
                .stream()
                .filter(e -> e.getValue() > 2)
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> e.getValue()
                ));
        return set;

    }

    public boolean matchString(final String s){
        return s.matches(".*\\d.*");
    }

}
