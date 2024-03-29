package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class CategoryHandler {

	private final CategoryReadDBHandler categoryReadDBHandler;
	private final CategoryCreationDBHandler categoryCreationDBHandler;
	private final CategoryUpdateDBHandler categoryUpdateDBHandler;
	private final CategoryDeletionDBHandler categoryDeletionDBHandler;
	private final LinkUpdateDBHandler linkUpdateDBHandler;

	public CategoryHandler(CategoryReadDBHandler categoryReadDBHandler, CategoryCreationDBHandler categoryCreationDBHandler, CategoryUpdateDBHandler categoryUpdateDBHandler, CategoryDeletionDBHandler categoryDeletionDBHandler, LinkUpdateDBHandler linkUpdateDBHandler) {
		this.categoryReadDBHandler = categoryReadDBHandler;
		this.categoryCreationDBHandler = categoryCreationDBHandler;
		this.categoryUpdateDBHandler = categoryUpdateDBHandler;
		this.categoryDeletionDBHandler = categoryDeletionDBHandler;
		this.linkUpdateDBHandler = linkUpdateDBHandler;
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
	
	public void updateCategory(Category category) throws SQLException{
		String sql = categoryUpdateDBHandler.constructSqlString(category);
		categoryUpdateDBHandler.updateCategory(sql, GenericDBHandler.getInstance());
	}
	
	public void deleteCategory(Category category) throws SQLException{
		String sql = categoryDeletionDBHandler.constructSqlString(category);
		categoryDeletionDBHandler.deleteCategory(sql, GenericDBHandler.getInstance());
	}

	public void moveCategory(Category source, Category target) throws SQLException{
		String sql = linkUpdateDBHandler.constructSqlStringMoveLink(source, target);
		linkUpdateDBHandler.moveLinks(sql, GenericDBHandler.getInstance());
	}
	
	public void deleteAllCategories() throws SQLException{
		categoryDeletionDBHandler.deleteAllCategories(GenericDBHandler.getInstance());
	}
}
