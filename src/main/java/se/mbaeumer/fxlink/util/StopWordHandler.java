package se.mbaeumer.fxlink.util;

import se.mbaeumer.fxlink.models.CategoryCount;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StopWordHandler {
    /*
    words.stream().filter(key -> "for".equals(key)
                || key.length() <=1 || "of".equals(key) || "with".equals(key)
                || "the".equals(key) || "to".equals(key)
                || "com".equals(key) || "de".equals(key) || key.matches(".*\\d.*")
                || "html".equals(key) || "htm".equals(key)).collect(Collectors.toList());
     */
    private static final List<String> STOP_WORDS_LIST = List.of("of", "with", "the", "to", "com", "de", "html", "htm");
    private static final Set<String> STOP_WORDS_SET = Set.of("of", "with", "the", "to", "com", "de", "html", "htm");

    public List<String> removeStopWordsFromList(List<String> words){
        words.removeAll(STOP_WORDS_LIST);
        List<String> toExclude = words.stream().filter(word -> word.matches(".*\\d.*")).collect(Collectors.toList());
        words.removeAll(toExclude);
        return words;
    }

    public Map removeStopWordsFromMap(Map<String, List<CategoryCount>> aMap){
        aMap.keySet().removeAll(STOP_WORDS_SET);
        Set<String> toExclude = aMap.keySet().stream()
                .filter(word -> word.matches(".*\\d.*"))
                .collect(Collectors.toSet());
        aMap.keySet().removeAll(toExclude);
        return aMap;
    }
}
