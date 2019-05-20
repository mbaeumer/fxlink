package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.HsqldbConnectionHandler;
import se.mbaeumer.fxlink.handlers.ImportItemCreationDBHandler;
import se.mbaeumer.fxlink.handlers.ImportItemDeletionDBHandler;
import se.mbaeumer.fxlink.handlers.ImportItemReadDBHandler;
import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.SQLException;
import java.util.List;

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

    public List<ImportItem> readImportItems() throws SQLException {
        ImportItemReadDBHandler importItemReadDBHandler = new ImportItemReadDBHandler();
        String sql = "select * from ImportItem i order by i.created desc ";
        return importItemReadDBHandler.getAllImportItems(sql, new HsqldbConnectionHandler());
    }
}
