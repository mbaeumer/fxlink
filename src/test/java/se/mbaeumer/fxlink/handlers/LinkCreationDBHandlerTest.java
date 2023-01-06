package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Before;
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
public class LinkCreationDBHandlerTest{

    private LinkCreationDBHandler linkCreationDBHandler;

    @Before
    public void setUp(){
        linkCreationDBHandler = new LinkCreationDBHandler();
    }

    @Test
    public void testReturnNullIfLinkIsNull(){
        Assert.assertNull(linkCreationDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlStringWithoutCategory(){
        Link link = createLinkWithoutCategory();
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "', DEFAULT)";
        String actual = linkCreationDBHandler.constructSqlString(link);
        Assert.assertEquals(actual,expected);
    }

    @Test
    public void testConstructSqlStringWithLinkThatContainsQuestionMark(){
        Link link = createLinkWithoutCategory();
        link.setURL("https://www.youtube.com/watch?v=HZyRQ8Uhhmk");
        link.setTitle("Some youtube link");
        link.setDescription("Some youtube link");
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Some youtube link', 'https://www.youtube.com/watch?v=HZyRQ8Uhhmk', 'Some youtube link', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "', DEFAULT)";
        String actual = linkCreationDBHandler.constructSqlString(link);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testConstructSqlStringWithCategory(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createSportsCategory());
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', 1, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "', DEFAULT)";
        String actual = linkCreationDBHandler.constructSqlString(link);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testConstructSqlStringWhenCategoryIsNA(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createCategoryWithNA());
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "', DEFAULT)";
        String actual = linkCreationDBHandler.constructSqlString(link);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testConstructSqlStringWhenCategoryIsAll(){
        Link link = createLinkWithoutCategory();
        link.setFollowUpRank(5);
        link.setCategory(createCategoryWithNameAll());
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "', 5)";
        String actual = linkCreationDBHandler.constructSqlString(link);
        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testConstructSqlStringWithDefaultRank(){
        Link link = createLinkWithoutCategory();
        link.setFollowUpRank(-1);
        link.setCategory(createCategoryWithNameAll());
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "', DEFAULT)";
        String actual = linkCreationDBHandler.constructSqlString(link);
        System.out.println("expected: " + expected);
        System.out.println("actual:   " + actual);
        Assert.assertEquals(actual, expected);
    }

    @Test
    public void testReturnFalseIfCategoryIsNull(){
        Link link = createLinkWithoutCategory();
        Assert.assertFalse("result should be false", linkCreationDBHandler.isCategorySet(link));
    }

    @Test
    public void testReturnFalseIfCategoryIsNA(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createCategoryWithNA());
        Assert.assertFalse("result should be false", linkCreationDBHandler.isCategorySet(link));
    }

    @Test
    public void testReturnFalseIfCategoryIsAll(){
        Link link = createLinkWithoutCategory();
        link.setCategory(createCategoryWithNameAll());
        Assert.assertFalse("result should be false", linkCreationDBHandler.isCategorySet(link));
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