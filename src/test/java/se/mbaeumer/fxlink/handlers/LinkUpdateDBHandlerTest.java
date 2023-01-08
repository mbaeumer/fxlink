package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martinbaumer on 31/12/16.
 */
public class LinkUpdateDBHandlerTest{

    private LinkUpdateDBHandler linkUpdateDBHandler;

    @Before
    public void setUp(){
        linkUpdateDBHandler = new LinkUpdateDBHandler();
    }

    @Test
    public void testReturnNullIfLinkIsNull(){
        Assert.assertNull("The method did not return null", linkUpdateDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlStringWithCategory(){
        Link link = createLink();
        String expected = "UPDATE Link SET title='Der Kicker', url='www.kicker.de', description='German sports magazine', followuprank=1,";
        expected += "categoryId=5,lastUpdated='" + link.getLastUpdated() + "' WHERE id=1";
        String actual = linkUpdateDBHandler.constructSqlString(link);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testReturnNullIfSourceIsNull(){
        Assert.assertNull("", linkUpdateDBHandler.constructSqlStringMoveLink(null, createCategory()));
    }

    @Test
    public void testReturnNullIfTargetIsNull(){
        Assert.assertNull("", linkUpdateDBHandler.constructSqlStringMoveLink(null, createCategory()));
    }

    @Test
    public void testConstructSqlStringforMove(){
        Category source = createCategory();
        Category target = createCategory();
        target.setId(6);
        String expected = "UPDATE Link SET categoryId=6 WHERE categoryId=5";
        String actual = linkUpdateDBHandler.constructSqlStringMoveLink(source, target);
        Assert.assertEquals(expected, actual);

    }

    @Test
    public void testConstructSqlStringWithoutCategory(){
        Link link = createLink();
        link.setCategory(null);
        String expected = "UPDATE Link SET title='Der Kicker', url='www.kicker.de', description='German sports magazine', followuprank=1,categoryId=null,";
        expected += "lastUpdated='" + link.getLastUpdated() + "' WHERE id=1";
        String actual = linkUpdateDBHandler.constructSqlString(link);
        System.out.println(expected);
        System.out.println(actual);
        Assert.assertEquals(actual, expected);
    }

    private Link createLink(){
        Link link = new Link("Der Kicker", "www.kicker.de", "German sports magazine");
        link.setId(1);
        link.setCategory(createCategory());
        link.setLastUpdated(createTimestamp());
        link.setFollowUpRank(1);

        return link;
    }

    private Category createCategory(){
        Category category = new Category();
        category.setId(5);
        return category;
    }

    private Timestamp createTimestamp(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return Timestamp.valueOf(df.format(new Date()));
    }
}
