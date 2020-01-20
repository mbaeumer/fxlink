package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ImportItemReadDBHandler {

    public List<ImportItem> getAllImportItems(String sql, DatabaseConnectionHandler databaseConnectionHandler) throws SQLException {
        Connection connection = databaseConnectionHandler.getConnection();
        List<ImportItem> items = new ArrayList();

        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            ImportItem importItem = new ImportItem();
            importItem.setId(rs.getInt("id"));
            importItem.setFilename(this.getFilename(rs.getString("filename")));
            importItem.setCreated(rs.getTimestamp("created"));
            items.add(importItem);
        }
        stmt.close();
        rs.close();

        return items;
    }

    public String getFilename(String filenameFromDatabase){
        if (filenameFromDatabase == null){
            throw new IllegalArgumentException("Could not handle filename");
        }
        String[] parts = filenameFromDatabase.split("/");
        return parts[parts.length - 1];
    }
}
