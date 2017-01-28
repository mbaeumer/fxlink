package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martinbaumer on 30/12/16.
 */
public class LinkCreationDBHandlerTest extends TestCase {

    @Test
    public void testReturnNullIfLinkIsNull(){
        assertNull(LinkCreationDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlStringWithoutCategory(){
        Link link = createLinkWithoutCategory();
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "')";
        String actual = LinkCreationDBHandler.constructSqlString(link);
        assertTrue("actual: " + actual, actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testConstructSqlStringWithCategory(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createSportsCategory());
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', 1, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "')";
        String actual = LinkCreationDBHandler.constructSqlString(link);
        assertTrue("actual: " + actual, actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testConstructSqlStringWhenCategoryIsNA(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createCategoryWithNA());
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "')";
        String actual = LinkCreationDBHandler.constructSqlString(link);
        assertTrue("actual: " + actual, actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testConstructSqlStringWhenCategoryIsAll(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createCategoryWithNameAll());
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "')";
        String actual = LinkCreationDBHandler.constructSqlString(link);
        assertTrue("actual: " + actual, actual.equalsIgnoreCase(expected));
    }

    @Test
    public void testReturnFalseIfCategoryIsNull(){
        Link link = createLinkWithoutCategory();
        assertFalse("result should be false", LinkCreationDBHandler.isCategorySet(link));
    }

    @Test
    public void testReturnFalseIfCategoryIsNA(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createCategoryWithNA());
        assertFalse("result should be false", LinkCreationDBHandler.isCategorySet(link));
    }

    @Test
    public void testReturnFalseIfCategoryIsAll(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createCategoryWithNameAll());
        assertFalse("result should be false", LinkCreationDBHandler.isCategorySet(link));
    }





    private Link createLinkWithoutCategory(){
        Link link = new Link("Der Kicker", "www.kicker.de", "German sports magazin");
        link.setLastUpdated(createTimestamp());
        return link;
    }

    private Timestamp createTimestamp(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return Timestamp.valueOf(df.format(new Date()));
    }

    private Category createSportsCategory(){
        Category category = new Category();
        category.setId(1);
        category.setName("Sports");
        return category;
    }

    private Category createCategoryWithNameAll(){
        Category category = new Category();
        category.setId(1);
        category.setName(ValueConstants.VALUE_ALL);
        return category;
    }

    private Category createCategoryWithNA(){
        Category category = new Category();
        category.setId(1);
        category.setName(ValueConstants.VALUE_N_A);
        return category;
    }




}
