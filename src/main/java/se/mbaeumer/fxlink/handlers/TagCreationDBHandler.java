package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagCreationDBHandler {

	public static final String BASE_INSERT = "INSERT INTO Tag VALUES(DEFAULT, ?, ?, DEFAULT, DEFAULT)";

	public static String constructSqlString(Tag tag){
		if (tag == null){
			return null;
		}
		String sql = BASE_INSERT;

		sql = sql.replaceFirst("\\?", "'" + tag.getName() + "'");
		sql = sql.replaceFirst("\\?", "'" + tag.getDescription() + "'");

		return sql;
	}

	public static int createTag(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();
		
		int tagId = -1;
		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

		stmt.executeUpdate();
		ResultSet rs = stmt.getGeneratedKeys();

		while (rs.next()){
			tagId = rs.getInt("id");
		}

		stmt.close();		
		return tagId;
	}

}
