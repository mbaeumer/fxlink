package se.mbaeumer.fxlink.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.CategoryReadDBHandler;
import se.mbaeumer.fxlink.models.Category;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class CategoryHandlerTest {
    private CategoryHandler categoryHandler;

    @Mock
    private CategoryReadDBHandler categoryReadDBHandler;

    @Before
    public void setUp(){
        categoryHandler = new CategoryHandler(categoryReadDBHandler);
    }

    @Test
    public void testGetCategories(){
        Mockito.when(categoryReadDBHandler.getAllCategories(any())).thenReturn(new ArrayList<>());
        List<Category> categories = categoryHandler.getCategories();
        assertEquals(categories.size(), 0);
    }

    @Test
    public void testGetCategoriesByName() throws SQLException {
        String categoryName = "test";
        Mockito.when(categoryReadDBHandler.constructSqlString(categoryName)).thenReturn("blabla");
        Mockito.when(categoryReadDBHandler.getCategoryByName(any(), any())).thenReturn(new Category());
        Category category = categoryHandler.getCategoryByName(categoryName);
        Assert.assertNotNull(category);
    }




}
