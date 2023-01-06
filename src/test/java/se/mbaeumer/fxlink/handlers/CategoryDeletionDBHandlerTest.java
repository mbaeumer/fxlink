package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;

import static org.junit.Assert.assertEquals;

/**
 * Created by martinbaumer on 11/01/17.
 */
public class CategoryDeletionDBHandlerTest{
    @Test
    public void testReturnNullIfCategoryIsNull(){
        CategoryDeletionDBHandler categoryDeletionDBHandler = new CategoryDeletionDBHandler();
        Assert.assertNull("Should return null", categoryDeletionDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlString(){
        CategoryDeletionDBHandler categoryDeletionDBHandler = new CategoryDeletionDBHandler();
        String expected = "DELETE FROM Category WHERE ID=1";
        String actual = categoryDeletionDBHandler.constructSqlString(createCategory());
        assertEquals(actual,expected);
    }

    private Category createCategory(){
        Category category = new Category();
        category.setId(1);
        category.setName("Programming");
        category.setDescription("Some links related to programming");
        return category;
    }
}
