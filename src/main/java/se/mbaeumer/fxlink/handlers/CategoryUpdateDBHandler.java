package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CategoryUpdateDBHandler {
	public static String SQL_BASE_UPDATE = "UPDATE Category SET name=?, description=?, lastUpdated=? WHERE id=?";

	public String constructSqlString(Category category){
		if (category == null){
			return null;
		}

		String sql = SQL_BASE_UPDATE;
		sql = sql.replaceFirst("\\?", "'" + category.getName() + "'");
		sql = sql.replaceFirst("\\?", "'" + category.getDescription() + "'");

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp tsLastUpdated = Timestamp.valueOf(df.format(new Date()));
		sql = sql.replaceFirst("\\?", "'" + tsLastUpdated + "'");

		sql = sql.replaceFirst("\\?", Integer.valueOf(category.getId()).toString() );

		return sql;
	}

	public void updateCategory(String sql, GenericDBHandler dbh) throws SQLException{
		Connection connection = dbh.getConnection();

		PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

		stmt.executeUpdate();
		stmt.close();

	}
}
