package se.mbaeumer.fxlink.handlers;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;

/**
 * Created by martinbaumer on 30/12/16.
 */
public class CategoryCreationDBHandlerTest extends TestCase {

    private Category nullCategory = null;

    private CategoryCreationDBHandler categoryCreationDBHandler;

    @Before
    public void setUp(){
        categoryCreationDBHandler = new CategoryCreationDBHandler();
    }

    @Test
    public void testReturnNullIfCategoryIsNull(){
        assertNull(categoryCreationDBHandler.constructSqlString(nullCategory));
    }

    @Test
    public void testConstructSqlString(){
        String expected = "INSERT INTO Category VALUES(DEFAULT, 'Programming', 'Some links related to programming', DEFAULT, DEFAULT)";
        String actual = categoryCreationDBHandler.constructSqlString(createCategory());
        assertTrue("actual result: " + actual, expected.equalsIgnoreCase(actual));
    }

    private Category createCategory(){
        Category category = new Category();
        category.setName("Programming");
        category.setDescription("Some links related to programming");
        return category;
    }
}
