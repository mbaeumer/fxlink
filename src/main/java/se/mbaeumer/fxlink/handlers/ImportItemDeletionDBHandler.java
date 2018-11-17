package se.mbaeumer.fxlink.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ImportItemDeletionDBHandler {
    public static final String SQL_BASE_DELETE = "DELETE FROM ImportItem ";

    public void deleteAllImportItems(DatabaseConnectionHandler dbh) throws SQLException {
        Connection connection = dbh.getConnection();
        PreparedStatement stmt = connection.prepareStatement(SQL_BASE_DELETE);
        stmt.executeUpdate();
        stmt.close();
    }
}
