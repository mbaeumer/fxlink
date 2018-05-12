package se.mbaeumer.fxlink.util;

import org.junit.Test;
import se.mbaeumer.fxlink.models.Link;

import static org.junit.Assert.assertTrue;

public class LinkTitleUtilTest {

    private LinkTitleUtil linkTitleUtil = new LinkTitleUtilImpl();

    @Test
    public void testGenerateTitleWithLongUrlAndDashes(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(linkTitleUtil.generateTitle(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateTitleWithLongUrlAndDashesAndPdfSuffix(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java.pdf";
        String expected = "how to retrieve list string from httpresponse object in java [pdf]";
        Link link  = new Link("", url, "");

        assertTrue(linkTitleUtil.generateTitle(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateTitleWithLongUrlAndDashesAndHtmlSuffix(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java.html";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(linkTitleUtil.generateTitle(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateTitleWithLongUrlAndDashesAndHtmSuffix(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java.htm";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(linkTitleUtil.generateTitle(link).equalsIgnoreCase(expected));
    }


    @Test
    public void testGenerateTitleWithLongUrlAndDashesAndTrailingSlash(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java/";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(linkTitleUtil.generateTitle(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateTitleWithLongUrlAndUnderscores(){
        String url = "https://stackoverflow.com/questions/23151306/how_to_retrieve_list_string_from_httpresponse_object_in_java";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(linkTitleUtil.generateTitle(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testRemoveHtml(){
        String description = "how to retrieve list string from httpresponse object in java.html";
        String expected = "how to retrieve list string from httpresponse object in java";
        String actual = linkTitleUtil.handleSuffix(description);
        assertTrue(actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testHandlePdf(){
        String description = "how to retrieve list string from httpresponse object in java.pdf";
        String expected = "how to retrieve list string from httpresponse object in java [pdf]";
        String actual = linkTitleUtil.handleSuffix(description);
        assertTrue(actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateTitleWithBaseUrlOnly(){
        String url = "www.kicker.de";
        String originalDescription = "Der Kicker";
        Link link  = new Link("", url, originalDescription);
        assertTrue(linkTitleUtil.generateTitle(link).equalsIgnoreCase(originalDescription));
    }
}
