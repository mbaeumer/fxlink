package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LinkSearchDBHandler {
	public static String BASE_QUERY = "SELECT l.id AS linkId, l.title AS linkTitle, l.url AS URL, l.description AS linkDescription, l.created AS linkCreated," + 
			" l.lastUpdated  as linkLastUpdated, l.categoryId as linkCategory, c.id as categoryId, c.name as category," + 
			" c.description as categoryDescription, c.created as categoryCreated, c.lastUpdated as categoryLastUpdated" +
			" from link l left join category c on c.id = l.categoryId ";
	
	public static String WHERE = "where ";
	public static String URL_CRITERIA_START = "URL LIKE '%";
	public static String URL_CRITERIA_END = "%' ";
	public static String TITLE_CRITERIA_START = "title LIKE '%";
	public static String TITLE_CRITERIA_END = "%'  ";	
	public static String DESCRIPTION_CRITERIA_START = "description LIKE '%";
	public static String DESCRIPTION_CRITERIA_END = "%' ";	
	public static String OR = "OR ";
	public static String AND = " AND ";
	public static String CATEGORY_ID = "categoryId ";
	public static String IS_NULL = "IS NULL";
	public static String EQUALS = "= ";
	
	public static String constructSearchString(String searchTerm, boolean isUrl, boolean isTitle, boolean isDescription, Category category){
		String sql = "";
		if (searchTerm == null || searchTerm == ""){
			return null;
		}
		
		if (!isUrl && !isTitle && !isDescription){
			return null;
		}
		
		sql += BASE_QUERY;
		sql += WHERE;
		
		String criteria = "";
		if (isUrl){
			criteria += URL_CRITERIA_START + searchTerm + URL_CRITERIA_END;
		}
		
		if (isTitle){
			if (criteria == ""){
				criteria += TITLE_CRITERIA_START;
			}else{
				criteria += OR + TITLE_CRITERIA_START;
			}
			criteria += searchTerm + TITLE_CRITERIA_END;
		}
		
		if (isDescription){
			if (criteria == ""){
				criteria += DESCRIPTION_CRITERIA_START;
			}else{
				criteria += OR + DESCRIPTION_CRITERIA_START;
			}
			criteria += searchTerm + DESCRIPTION_CRITERIA_END;
		}
		
		sql += criteria;

		if (category.getName() == ValueConstants.VALUE_N_A){
			sql += AND + CATEGORY_ID + IS_NULL;
		}else if (category.getName() != ValueConstants.VALUE_ALL){
			sql += AND + CATEGORY_ID + EQUALS + category.getId();
		}
		
		return sql;
	}
	
	public static List<Link> findAllMatchingLinks(GenericDBHandler dbh, String sql, String searchTerm, boolean isUrl, boolean isTitle, boolean isDescription) throws SQLException{
		Connection connection = dbh.getConnection();				
		List<Link> links = new ArrayList<Link>();
		
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			Link link = new Link(rs.getString("linkTitle"), rs.getString("URL"), rs.getString("linkDescription"));
			link.setId(rs.getInt("linkId"));
			link.setCreated(rs.getTimestamp("linkCreated"));
			link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
			int categoryId = rs.getInt("categoryId");
			if (categoryId > 0) {
				Category category = new Category();
				category.setId(rs.getInt("categoryId"));
				category.setName(rs.getString("category"));
				link.setCategory(category);
			}
			links.add(link);
		}
		stmt.close();
		rs.close();

		return links;
	}
}
