package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Link;

/**
 * Created by martinbaumer on 07/01/17.
 */
public class LinkDeletionDBHandlerTest extends TestCase{

    @Test
    public void testReturnNullIfLinkIsNull(){
        Assert.assertNull("Should be null", LinkDeletionDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlString(){
        String expected = "DELETE FROM Link WHERE id=1";
        String actual = LinkDeletionDBHandler.constructSqlString(createLink());
        assertTrue("The strings do not match", expected.equalsIgnoreCase(actual));
    }

    private Link createLink(){
        Link link = new Link("Der Kicker", "www.kicker.de", "German sports magazine");
        link.setId(1);
        return link;
    }
}
