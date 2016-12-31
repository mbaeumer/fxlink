package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LinkUpdateDBHandler {
	public static String SQL_BASE_UPDATE = "UPDATE Link SET title=?, url=?, description=?,";
	public static String SQL_UPDATE_CATEGORY = "categoryId=?,";
	public static String SQL_UPDATE_DATE = "lastUpdated=? ";
	public static String SQL_UPDATE_WHERE_CLAUSE = "WHERE id=?";

	
	public static String constructSqlString(Link link){
		String  sql = SQL_BASE_UPDATE;

		if (link == null){
			return null;
		}

		if (link.getCategory() != null){
			sql += SQL_UPDATE_CATEGORY;
		}

		sql += SQL_UPDATE_DATE + SQL_UPDATE_WHERE_CLAUSE;

		sql = sql.replaceFirst("\\?", "'" + link.getTitle() + "'");
		sql = sql.replaceFirst("\\?", "'" + link.getURL() + "'");
		sql = sql.replaceFirst("\\?", "'" + link.getDescription() + "'");

		if (link.getCategory() != null){
			sql = sql.replaceFirst("\\?", new Integer(link.getCategory().getId()).toString() );
		}

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));
		sql = sql.replaceFirst("\\?", "'" + tsLastUpdated + "'");

		sql = sql.replaceFirst("\\?", new Integer(link.getId()).toString() );

		return sql;
	}

	public static void updateLink(String sql, GenericDBHandler dbh) throws ParseException, SQLException{
		Connection connection = dbh.getConnection();

		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
		
		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void nullifyLinksWithCategory(Category category, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "UPDATE Link SET categoryId=null WHERE categoryId=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, category.getId());

		stmt.executeUpdate();
		stmt.close();
	}

	public static void moveLinks(Category source, Category target, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();

		String sql = "UPDATE Link SET categoryId=? WHERE categoryId=?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, target.getId());
		stmt.setInt(2, source.getId());

		stmt.executeUpdate();
		stmt.close();
	}
}
