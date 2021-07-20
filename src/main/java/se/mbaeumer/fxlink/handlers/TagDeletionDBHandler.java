package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TagDeletionDBHandler {
	public static final String SQL_BASE_DELETE = "DELETE FROM Tag ";
	public static final String WHERE_CLAUSE = "WHERE ID=?";

	public static String constructSqlString(Tag tag){
		if (tag == null){
			return null;
		}

		String sql = SQL_BASE_DELETE + WHERE_CLAUSE;
		sql = sql.replaceFirst("\\?", Integer.valueOf(tag.getId()).toString() );

		return sql;
	}

	public static void deleteTag(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.executeUpdate();
		stmt.close();
	}
	
	public static void deleteAllTags(GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		String sql = "DELETE FROM Tag";
		PreparedStatement stmt = connection.prepareStatement(sql);

		stmt.executeUpdate();
		stmt.close();
	}
}