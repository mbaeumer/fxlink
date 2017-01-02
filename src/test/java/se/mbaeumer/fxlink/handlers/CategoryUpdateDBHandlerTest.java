package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Assert;
import se.mbaeumer.fxlink.models.Category;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martinbaumer on 02/01/17.
 */
public class CategoryUpdateDBHandlerTest extends TestCase {

    public void testReturnNullIfCategoryIsNull(){
        Assert.assertNull("should be null", CategoryUpdateDBHandler.constructSqlString(null));
    }

    public void testConstructSqlString(){
        Category category = createCategory();
        String expected = "UPDATE Category SET name='Programming', description='Some links related to programming', lastUpdated='"
        + category.getLastUpdated() + "' WHERE id=1";
        String actual = CategoryUpdateDBHandler.constructSqlString(category);
        System.out.println("actual: " + actual);
        System.out.println("expected: " + expected);

        assertTrue("The strings do not match", actual.equalsIgnoreCase(expected));
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
