package se.mbaeumer.fxlink.api;

import org.junit.Assert;
import org.junit.Test;

public class WordCountHandlerTest {

    @Test
    public void matchString() {
        WordCountHandler wch = new WordCountHandler(null, null);
        Assert.assertFalse(wch.matchString("upload"));
        Assert.assertTrue(wch.matchString("1235"));
        Assert.assertTrue(wch.matchString("73dttfyg"));
        Assert.assertTrue(wch.matchString("dtt88fyg"));
    }

    @Test
    public void getWordCountB() {
    }
}