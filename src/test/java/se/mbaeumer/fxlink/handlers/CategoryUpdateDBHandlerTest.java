package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martinbaumer on 02/01/17.
 */
public class CategoryUpdateDBHandlerTest{

    @Test
    public void testReturnNullIfCategoryIsNull(){
        CategoryUpdateDBHandler categoryUpdateDBHandler = new CategoryUpdateDBHandler();
        Assert.assertNull("should be null", categoryUpdateDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlString(){
        CategoryUpdateDBHandler categoryUpdateDBHandler = new CategoryUpdateDBHandler();
        Category category = createCategory();
        String expected = "UPDATE Category SET name='Programming', description='Some links related to programming', lastUpdated='"
        + category.getLastUpdated() + "' WHERE id=1";
        String actual = categoryUpdateDBHandler.constructSqlString(category);
        System.out.println("actual: " + actual);
        System.out.println("expected: " + expected);

        Assert.assertEquals(actual,expected);
    }

    private Category createCategory(){
        Category category = new Category();
        category.setId(1);
        category.setName("Programming");
        category.setDescription("Some links related to programming");
        category.setLastUpdated(createTimestamp());

        return category;
    }

    private Timestamp createTimestamp(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return Timestamp.valueOf(df.format(new Date()));
    }
}
