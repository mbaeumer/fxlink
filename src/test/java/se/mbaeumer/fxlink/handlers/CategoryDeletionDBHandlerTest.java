package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;

/**
 * Created by martinbaumer on 11/01/17.
 */
public class CategoryDeletionDBHandlerTest extends TestCase {
    @Test
    public void testReturnNullIfCategoryIsNull(){
        CategoryDeletionDBHandler categoryDeletionDBHandler = new CategoryDeletionDBHandler();
        Assert.assertNull("Should return null", categoryDeletionDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlString(){
        CategoryDeletionDBHandler categoryDeletionDBHandler = new CategoryDeletionDBHandler();
        String expected = "DELETE FROM Category WHERE id=1";
        String actual = categoryDeletionDBHandler.constructSqlString(createCategory());
        System.out.println("actual: " + actual);
        System.out.println("expected: " + expected);
        assertTrue("The strings do not match", actual.equalsIgnoreCase(expected));
    }

    private Category createCategory(){
        Category category = new Category();
        category.setId(1);
        category.setName("Programming");
        category.setDescription("Some links related to programming");
        return category;
    }
}
