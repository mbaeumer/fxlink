package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class CategoryHandler {

	private final CategoryReadDBHandler categoryReadDBHandler;
	private final CategoryCreationDBHandler categoryCreationDBHandler;
	private final CategoryUpdateDBHandler categoryUpdateDBHandler;

	public CategoryHandler(CategoryReadDBHandler categoryReadDBHandler, CategoryCreationDBHandler categoryCreationDBHandler, CategoryUpdateDBHandler categoryUpdateDBHandler) {
		this.categoryReadDBHandler = categoryReadDBHandler;
		this.categoryCreationDBHandler = categoryCreationDBHandler;
		this.categoryUpdateDBHandler = categoryUpdateDBHandler;
	}

	public List<Category> getCategories(){
		return this.categoryReadDBHandler.getAllCategories(GenericDBHandler.getInstance());
	}

	public Category getCategoryByName(final String categoryName) throws SQLException {
		String sql = categoryReadDBHandler.constructSqlString(categoryName);
		return categoryReadDBHandler.getCategoryByName(sql, GenericDBHandler.getInstance());
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
	
	public void createCategory(Category category) throws SQLException{
		String sql = categoryCreationDBHandler.constructSqlString(category);
		categoryCreationDBHandler.createCategory(sql, GenericDBHandler.getInstance());
	}
	
	public void updateCategory(Category category) throws ParseException, SQLException{
		String sql = categoryUpdateDBHandler.constructSqlString(category);
		categoryUpdateDBHandler.updateCategory(sql, GenericDBHandler.getInstance());
	}
	
	public static void deleteCategory(Category category) throws SQLException{
		String sql = CategoryDeletionDBHandler.constructSqlString(category);
		CategoryDeletionDBHandler.deleteCategory(sql, GenericDBHandler.getInstance());
	}

	public static void moveCategory(Category source, Category target) throws SQLException{
		String sql = LinkUpdateDBHandler.constructSqlStringMoveLink(source, target);
		LinkUpdateDBHandler.moveLinks(sql, GenericDBHandler.getInstance());
	}
	
	public static void deleteAllCategories() throws SQLException{
		CategoryDeletionDBHandler.deleteAllCategories(GenericDBHandler.getInstance());
	}
}
