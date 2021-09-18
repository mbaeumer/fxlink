package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;

public class CategoryReadDBHandlerTest {

    @Test
    public void testReadCategoryByName(){
        CategoryReadDBHandler categoryReadDBHandler = new CategoryReadDBHandler();
        String expected = "select c.id as categoryId, c.name, c.description from category c where c.name='cat1'";
        String actual = categoryReadDBHandler.constructSqlString("cat1");
        Assert.assertTrue(expected.equalsIgnoreCase(actual));
    }
}
