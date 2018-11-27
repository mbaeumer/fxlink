package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class ImportItemImportDBandler {

    public void importImportItem(ImportItem linkTag, DatabaseConnectionHandler databaseConnectionHandler) throws SQLException {
        Connection connection = databaseConnectionHandler.getConnection();

        String sql = "INSERT INTO ImportItem "
                + "VALUES(?, ?, ?) ";

        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setInt(1, linkTag.getId());
        stmt.setString(2, linkTag.getFilename());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Timestamp tsCreated = Timestamp.valueOf(df.format(linkTag.getCreated()));
        stmt.setTimestamp(3, tsCreated);

        stmt.executeUpdate();

        stmt.close();

    }
}
