package se.mbaeumer.fxlink.util;

import org.junit.Test;
import se.mbaeumer.fxlink.models.Link;

import static org.junit.Assert.assertTrue;

public class DescriptionUtilTest {

    private DescriptionUtil descriptionUtil = new DescriptionUtilImpl();

    @Test
    public void testGenerateDescriptionWithLongUrlAndDashes(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(descriptionUtil.generateDescription(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateDescriptionWithLongUrlAndDashesAndPdfSuffix(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java.pdf";
        String expected = "how to retrieve list string from httpresponse object in java [pdf]";
        Link link  = new Link("", url, "");

        assertTrue(descriptionUtil.generateDescription(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateDescriptionWithLongUrlAndDashesAndHtmlSuffix(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java.html";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(descriptionUtil.generateDescription(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateDescriptionWithLongUrlAndDashesAndHtmSuffix(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java.htm";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(descriptionUtil.generateDescription(link).equalsIgnoreCase(expected));
    }


    @Test
    public void testGenerateDescriptionWithLongUrlAndDashesAndTrailingSlash(){
        String url = "https://stackoverflow.com/questions/23151306/how-to-retrieve-list-string-from-httpresponse-object-in-java/";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(descriptionUtil.generateDescription(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateDescriptionWithLongUrlAndUnderscores(){
        String url = "https://stackoverflow.com/questions/23151306/how_to_retrieve_list_string_from_httpresponse_object_in_java";
        String expected = "how to retrieve list string from httpresponse object in java";
        Link link  = new Link("", url, "");

        assertTrue(descriptionUtil.generateDescription(link).equalsIgnoreCase(expected));
    }

    @Test
    public void testRemoveHtml(){
        String description = "how to retrieve list string from httpresponse object in java.html";
        String expected = "how to retrieve list string from httpresponse object in java";
        String actual = descriptionUtil.handleSuffix(description);
        assertTrue(actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testHandlePdf(){
        String description = "how to retrieve list string from httpresponse object in java.pdf";
        String expected = "how to retrieve list string from httpresponse object in java [pdf]";
        String actual = descriptionUtil.handleSuffix(description);
        assertTrue(actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testGenerateDescriptionWithBaseUrlOnly(){
        String url = "www.kicker.de";
        String originalDescription = "Der Kicker";
        Link link  = new Link("", url, originalDescription);
        assertTrue(descriptionUtil.generateDescription(link).equalsIgnoreCase(originalDescription));
    }
}
