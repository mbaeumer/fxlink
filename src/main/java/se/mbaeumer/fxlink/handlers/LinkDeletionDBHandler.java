package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LinkDeletionDBHandler {

	public static final String SQL_BASE_DELETE = "DELETE FROM Link";
	public static final String SQL_WHERE_CLAUSE = " WHERE id=?";

	public static String constructSqlString(Link link){
		if (link == null){
			return null;
		}

		String sql = SQL_BASE_DELETE + SQL_WHERE_CLAUSE;
		sql = sql.replaceFirst("\\?", new Integer(link.getId()).toString() );

		return sql;
	}

	public static void deleteLink(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();

		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void deleteAllLinks(GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Link";
		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.executeUpdate();
		stmt.close();

	}

}
