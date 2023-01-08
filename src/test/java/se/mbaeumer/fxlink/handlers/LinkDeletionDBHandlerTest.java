package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Link;

/**
 * Created by martinbaumer on 07/01/17.
 */
public class LinkDeletionDBHandlerTest{


    private LinkDeletionDBHandler linkDeletionDBHandler;

    @Before
    public void setUp(){
        linkDeletionDBHandler = new LinkDeletionDBHandler();
    }

    @Test
    public void testReturnNullIfLinkIsNull(){
        Assert.assertNull("Should be null", linkDeletionDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlString(){
        String expected = "DELETE FROM Link WHERE id=1";
        String actual = linkDeletionDBHandler.constructSqlString(createLink());
        Assert.assertEquals(expected, actual);
    }

    private Link createLink(){
        Link link = new Link("Der Kicker", "www.kicker.de", "German sports magazine");
        link.setId(1);
        return link;
    }
}