package se.mbaeumer.fxlink.util;

import org.junit.Test;
import se.mbaeumer.fxlink.models.Link;

import static org.junit.Assert.*;

public class LinkSplitterTest {

    private LinkSplitter linkSplitter;
    @Test
    public void splitToData() {
        linkSplitter = new LinkSplitter(new URLHelper());
        Link link = new Link("some link", "www.somelink.com/some-interesting-url", "");
        String[] actual = linkSplitter.splitToData(link);
        assertEquals(5, actual.length);
        assertTrue(contains(actual, "somelink"));
        assertTrue(contains(actual, "com"));
        assertTrue(contains(actual, "some"));
        assertTrue(contains(actual, "interesting"));
        assertTrue(contains(actual, "url"));
    }

    private boolean contains(String[] words, String word){
        boolean found = false;
        for (String w : words){
            if (w.equalsIgnoreCase(word)){
                found = true;
            }
        }
        return found;
    }
}