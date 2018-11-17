package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.HsqldbConnectionHandler;
import se.mbaeumer.fxlink.handlers.ImportItemCreationDBHandler;
import se.mbaeumer.fxlink.handlers.ImportItemDeletionDBHandler;
import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.SQLException;

public class ImportItemHandler {

    public void createImportItem(final String fileName) throws SQLException {
        ImportItemCreationDBHandler importItemCreationDBHandler = new ImportItemCreationDBHandler();
        ImportItem importItem = new ImportItem();
        importItem.setFilename(fileName);
        String sql = importItemCreationDBHandler.constructSql(importItem);
        importItemCreationDBHandler.createImportItem(sql, new HsqldbConnectionHandler());
    }

    public void deleteAllImportItems() throws SQLException {
        ImportItemDeletionDBHandler importItemDeletionDBHandler = new ImportItemDeletionDBHandler();
        importItemDeletionDBHandler.deleteAllImportItems(new HsqldbConnectionHandler());
    }
}
