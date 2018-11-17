package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.HsqldbConnectionHandler;
import se.mbaeumer.fxlink.handlers.ImportItemCreationDBHandler;
import se.mbaeumer.fxlink.handlers.ImportItemReadDBHandler;
import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.SQLException;

public class ImportItemHandler {

    public void doSom(final String fileName) throws SQLException {
        ImportItemCreationDBHandler importItemReadDBHandler = new ImportItemCreationDBHandler();
        ImportItem importItem = new ImportItem();
        importItem.setFilename(fileName);
        String sql = importItemReadDBHandler.constructSql(importItem);
        importItemReadDBHandler.createImportItem(sql, new HsqldbConnectionHandler());
    }
}
