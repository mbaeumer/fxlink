package se.mbaeumer.fxlink.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class URLHelperTest {

    private URLHelper urlHelper;

    @Test
    public void withoutProtocol() {
        urlHelper = new URLHelper();
        String expected = "www.dadada.se";
        String actual = urlHelper.withoutProtocol("http://www.dadada.se");
        assertEquals(expected,actual);
    }

    @Test
    public void withoutPrefix() {
        urlHelper = new URLHelper();
        String expected = "dadada.se";
        String actual = urlHelper.withoutPrefix("www.dadada.se");
        assertEquals(expected,actual);
    }
}