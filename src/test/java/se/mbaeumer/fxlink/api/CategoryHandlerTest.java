package se.mbaeumer.fxlink.api;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;

import java.sql.SQLException;
import java.text.ParseException;
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

    @Mock
    private CategoryUpdateDBHandler categoryUpdateDBHandler;

    @Mock
    private CategoryDeletionDBHandler categoryDeletionDBHandler;

    @Mock
    private LinkUpdateDBHandler linkUpdateDBHandler;

    @Before
    public void setUp(){
        categoryHandler = new CategoryHandler(categoryReadDBHandler, categoryCreationDBHandler,
                categoryUpdateDBHandler, categoryDeletionDBHandler,
                linkUpdateDBHandler);
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

    @Test
    public void testUpdateCategory(){
        Category category = new Category();
        category.setId(1);
        category.setName("test2");
        Mockito.when(categoryUpdateDBHandler.constructSqlString(category)).thenReturn("blabla");

        try {
            Mockito.doNothing().when(categoryUpdateDBHandler).updateCategory(any(), any());
            categoryHandler.updateCategory(category);
        } catch (ParseException | SQLException e) {
            fail("Exception occurred");
        }
    }

    @Test
    public void testDeleteCategory(){
        Category category = new Category();
        category.setId(1);
        category.setName("test2");

        Mockito.when(categoryDeletionDBHandler.constructSqlString(category)).thenReturn("blabla");

        try {
            Mockito.doNothing().when(categoryDeletionDBHandler).deleteCategory(any(), any());
            categoryHandler.deleteCategory(category);
        } catch (SQLException throwables) {
            fail("Exception occurred");
        }
    }

    @Test
    public void testDeleteAllCategories(){

        Category category = new Category();
        category.setId(1);
        category.setName("test2");

        try {
            Mockito.doNothing().when(categoryDeletionDBHandler).deleteAllCategories(any());
            categoryHandler.deleteAllCategories();
        } catch (SQLException throwables) {
            fail("Exception occurred");
        }
    }

    @Test
    public void testMoveCategory(){
        Category source = new Category();
        source.setId(1);
        source.setName("Source");

        Category target = new Category();
        target.setId(2);
        target.setName("Target");

        Mockito.when(linkUpdateDBHandler.constructSqlStringMoveLink(source, target)).thenReturn("blabla");

        try {
            Mockito.doNothing().when(linkUpdateDBHandler).moveLinks(any(), any());
            categoryHandler.moveCategory(source, target);
        } catch (SQLException throwables) {
            fail("Exception occurred");
        }
    }
}
