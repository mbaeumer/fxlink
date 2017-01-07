package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martinbaumer on 31/12/16.
 */
public class LinkUpdateDBHandlerTest extends TestCase {

    @Test
    public void testReturnNullIfLinkIsNull(){
        Assert.assertNull("The method did not return null", LinkUpdateDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlStringWithCategory(){
        Link link = createLink();
        String expected = "UPDATE Link SET title='Der Kicker', url='www.kicker.de', description='German sports magazine',";
        expected += "categoryId=5,lastUpdated='" + link.getLastUpdated() + "' WHERE id=1";
        String actual = LinkUpdateDBHandler.constructSqlString(link);
        assertTrue("The string values do not match", actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testReturnNullIfSourceIsNull(){
        assertNull("", LinkUpdateDBHandler.constructSqlStringMoveLink(null, createCategory()));
    }

    @Test
    public void testReturnNullIfTargetIsNull(){
        assertNull("", LinkUpdateDBHandler.constructSqlStringMoveLink(null, createCategory()));
    }

    @Test
    public void testConstructSqlStringforMove(){
        Category source = createCategory();
        Category target = createCategory();
        target.setId(6);
        String expected = "UPDATE Link SET categoryId=6 WHERE categoryId=5";
        String actual = LinkUpdateDBHandler.constructSqlStringMoveLink(source, target);
        assertTrue("The strings do not match", expected.equalsIgnoreCase(actual));

    }

    @Test
    public void testConstructSqlStringWithoutCategory(){
        Link link = createLink();
        link.setCategory(null);
        String expected = "UPDATE Link SET title='Der Kicker', url='www.kicker.de', description='German sports magazine',";
        expected += "lastUpdated='" + link.getLastUpdated() + "' WHERE id=1";
        String actual = LinkUpdateDBHandler.constructSqlString(link);
        assertTrue("The string values do not match", actual.equalsIgnoreCase(expected));
    }

    private Link createLink(){
        Link link = new Link("Der Kicker", "www.kicker.de", "German sports magazine");
        link.setId(1);
        link.setCategory(createCategory());
        link.setLastUpdated(createTimestamp());

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
