package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class CategoryHandler {
	
	public static List<Category> getCategories(){
		return CategoryReadDBHandler.getAllCategories(GenericDBHandler.getInstance());
	}
	
	public static Category createPseudoCategory(String categoryName){
		Category category = new Category();
		category.setId(-1);
		category.setName(categoryName);
		category.setDescription(categoryName);
		category.setCreated(new Date());
		category.setLastUpdated(new Date());
		return category;
	}
	
	public static void createCategory(Category category) throws SQLException{
		String sql = CategoryCreationDBHandler.constructSqlString(category);
		CategoryCreationDBHandler.createCategory(sql, GenericDBHandler.getInstance());
	}
	
	public static void updateCategory(Category category) throws ParseException, SQLException{
		CategoryUpdateDBHandler.updateCategory(category, GenericDBHandler.getInstance());
	}
	
	public static void deleteCategory(Category category) throws SQLException{
		CategoryDeletionDBHandler.deleteCategory(category, GenericDBHandler.getInstance());
	}

	public static void moveCategory(Category source, Category target) throws SQLException{
		LinkUpdateDBHandler.moveLinks(source, target, GenericDBHandler.getInstance());
	}
	
	public static void deleteAllCateories() throws SQLException{
		CategoryDeletionDBHandler.deleteAllCategories(GenericDBHandler.getInstance());
	}
}
