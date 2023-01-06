package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;

/**
 * Created by martinbaumer on 30/12/16.
 */
public class CategoryCreationDBHandlerTest{

    private final Category nullCategory = null;

    private CategoryCreationDBHandler categoryCreationDBHandler;

    @Before
    public void setUp(){
        categoryCreationDBHandler = new CategoryCreationDBHandler();
    }

    @Test
    public void testReturnNullIfCategoryIsNull(){
        Assert.assertNull(categoryCreationDBHandler.constructSqlString(nullCategory));
    }

    @Test
    public void testConstructSqlString(){
        String expected = "INSERT INTO Category VALUES(DEFAULT, 'Programming', 'Some links related to programming', DEFAULT, DEFAULT)";
        String actual = categoryCreationDBHandler.constructSqlString(createCategory());
        Assert.assertEquals(actual, expected);
    }

    private Category createCategory(){
        Category category = new Category();
        category.setName("Programming");
        category.setDescription("Some links related to programming");
        return category;
    }
}
