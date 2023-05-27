package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LinkSearchDBHandler {
	public static String BASE_QUERY = "SELECT l.id AS linkId, l.title AS linkTitle, l.url AS URL, l.description AS linkDescription, l.created AS linkCreated," +
			" l.lastUpdated  as linkLastUpdated, l.categoryId as linkCategory, c.id as categoryId, c.name as category," +
			" c.description as categoryDescription, c.created as categoryCreated, c.lastUpdated as categoryLastUpdated" +
			" from link l left join category c on c.id = l.categoryId ";

	public static String WHERE = "where ";

	public static final String LCASE_START = "LCASE(";
	public static final String CLOSING_BRACKET=") ";

	public static final String LIKE_CONTAINS="LIKE '%";

	public static final String LIKE_STARTS_WITH="LIKE '";

	public static String LIKE_END = "%' ";

	public static String OR = "OR ";
	public static String AND = "AND ";
	public static String CATEGORY_ID = "categoryId ";
	public static String IS_NULL = "IS NULL";
	public static String EQUALS = "= ";

	public String constructSearchString(String searchTerm, boolean isUrl, boolean isTitle, boolean isDescription, Category category){
		String sql = "";
		if (searchTerm == null || "".equals(searchTerm)){
			return null;
		}

		if (!isUrl && !isTitle && !isDescription){
			return null;
		}

		String lowerCaseSearchTerm = searchTerm.toLowerCase();

		sql += BASE_QUERY;
		sql += WHERE;

		String criteria = "";
		if (isUrl){
			criteria += "(" + buildWhereClause("url", lowerCaseSearchTerm);
		}

		if (isTitle){
			criteria += buildCriteriaStart(criteria);
			criteria += buildWhereClause("title", lowerCaseSearchTerm);
		}

		if (isDescription){
			criteria += buildCriteriaStart(criteria);
			criteria += buildWhereClause("description", lowerCaseSearchTerm);
		}

		if (!"".equals(criteria)) {
			sql += criteria + ")";
		}

		if (ValueConstants.VALUE_N_A.equals(category.getName())){
			sql += AND + CATEGORY_ID + IS_NULL;
		}else if (!ValueConstants.VALUE_ALL.equals(category.getName())){
			sql += AND + CATEGORY_ID + EQUALS + "?";
		}

		return sql;
	}

	public String buildWhereClause(String columnName, String searchTerm){
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(LCASE_START)
				.append(columnName)
				.append(CLOSING_BRACKET).append(LIKE_CONTAINS).append(searchTerm).append(LIKE_END)
				.append(OR).append(LCASE_START).append(columnName).append(CLOSING_BRACKET)
				.append(LIKE_STARTS_WITH).append(searchTerm).append(LIKE_END);

		return stringBuilder.toString();
	}

	public String buildCriteriaStart(String criteria){
		return !"".equals(criteria) ? OR : "(";
	}

	public List<Link> findAllMatchingLinks(GenericDBHandler dbh, String sql, Category category) throws SQLException{
		Connection connection = dbh.getConnection();
		List<Link> links = new ArrayList<>();

		PreparedStatement stmt = connection.prepareStatement(sql);
		if (category.getId() != -1 && !ValueConstants.VALUE_ALL.equals(category.getName())) {
			stmt.setInt(1, category.getId());
		}

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			Link link = new Link(rs.getString("linkTitle"), rs.getString("URL"), rs.getString("linkDescription"));
			link.setId(rs.getInt("linkId"));
			link.setCreated(rs.getTimestamp("linkCreated"));
			link.setLastUpdated(rs.getTimestamp("linkLastUpdated"));
			int categoryId = rs.getInt("categoryId");
			if (categoryId > 0) {
				Category cat = new Category();
				cat.setId(rs.getInt("categoryId"));
				cat.setName(rs.getString("category"));
				link.setCategory(cat);
			}
			links.add(link);
		}
		stmt.close();
		rs.close();

		return links;
	}
}
