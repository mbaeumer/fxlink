package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class ImportItemCreationDBHandler {
    public static final String BASE_INSERT = "INSERT INTO ImportItem VALUES(DEFAULT, FILENAME_PLACEHOLDER, DEFAULT)";

    public String constructSql(ImportItem importItem){
        String sql = BASE_INSERT;
        sql = sql.replaceFirst(Pattern.quote("FILENAME_PLACEHOLDER"), "'" + importItem.getFilename() + "'");
        return sql;
    }

    public int createImportItem(String sql, DatabaseConnectionHandler databaseConnectionHandler) throws SQLException {
        Connection connection = databaseConnectionHandler.getConnection();

        int importItemId = -1;
        PreparedStatement stmt = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

        stmt.executeUpdate();
        ResultSet rs = stmt.getGeneratedKeys();

        while (rs.next()){
            importItemId = rs.getInt("id");
        }
        stmt.close();

        return importItemId;
    }
}
