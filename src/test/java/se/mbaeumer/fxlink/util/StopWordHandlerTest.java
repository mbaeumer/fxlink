package se.mbaeumer.fxlink.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class StopWordHandlerTest {

    @Test
    public void removeStopWordsFromList() {
        List<String> originalList = new ArrayList<>(List.of("bild", "stackoverflow", "java", "of", "the", "html", "html"));
        StopWordHandler stopWordHandler = new StopWordHandler();
        List<String> cleanedList = stopWordHandler.removeStopWordsFromList(originalList);
        Assert.assertTrue(cleanedList.contains("java"));
        Assert.assertFalse(cleanedList.contains("of"));
        Assert.assertFalse(cleanedList.contains("the"));
    }

    @Test
    public void removeNumericStrings(){
        List<String> originalList = new ArrayList<>(List.of("bild", "stackoverflow", "java", "of", "the", "html", "html", "123"));
        StopWordHandler stopWordHandler = new StopWordHandler();
        List<String> cleanedList = stopWordHandler.removeStopWordsFromList(originalList);
        Assert.assertTrue(cleanedList.contains("java"));
        Assert.assertFalse(cleanedList.contains("the"));
        Assert.assertFalse(cleanedList.contains("123"));
    }

    @Test
    public void removeAlphaNumericStrings(){
        List<String> originalList = new ArrayList<>(List.of("bild", "stackoverflow", "java", "of", "the", "html", "html", "123abc"));
        StopWordHandler stopWordHandler = new StopWordHandler();
        List<String> cleanedList = stopWordHandler.removeStopWordsFromList(originalList);
        Assert.assertTrue(cleanedList.contains("java"));
        Assert.assertFalse(cleanedList.contains("the"));
        Assert.assertFalse(cleanedList.contains("123abc"));
    }

    @Test
    public void removeStopWordsFromMap() {
        Map map = new HashMap(Map.ofEntries(
                Map.entry("of", 2),
                Map.entry("java", 5),
                Map.entry("stackoverflow", 7),
                Map.entry("the", 4)));

        StopWordHandler stopWordHandler = new StopWordHandler();
        Map actualMap = stopWordHandler.removeStopWordsFromMap(map);
        Assert.assertNull(actualMap.get("of"));
        Assert.assertNotNull(actualMap.get("java"));
    }
}