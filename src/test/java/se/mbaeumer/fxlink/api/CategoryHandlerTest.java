package se.mbaeumer.fxlink.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.CategoryCreationDBHandler;
import se.mbaeumer.fxlink.handlers.CategoryReadDBHandler;
import se.mbaeumer.fxlink.models.Category;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class CategoryHandlerTest {
    private CategoryHandler categoryHandler;

    @Mock
    private CategoryReadDBHandler categoryReadDBHandler;

    @Mock
    private CategoryCreationDBHandler categoryCreationDBHandler;

    @Before
    public void setUp(){
        categoryHandler = new CategoryHandler(categoryReadDBHandler, categoryCreationDBHandler);
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


    @Test
    public void testCreateCategory() {
        Category newCategory = new Category();
        newCategory.setName("test");
        Mockito.when(categoryCreationDBHandler.constructSqlString(newCategory)).thenReturn("blabla");

        try {
            Mockito.when(categoryCreationDBHandler.createCategory(any(), any())).thenReturn(1);
            categoryHandler.createCategory(newCategory);
        } catch (SQLException throwables) {
            fail("Exception occurred");
        }
    }
}
