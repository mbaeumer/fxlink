package se.mbaeumer.fxlink.handlers;

import java.util.List;

public interface ManagedItemDBOperationHandler extends DatabaseOperationHandler {
    List<String> getAllManagedItems(String sql, DatabaseConnectionHandler databaseConnectionHandler);
}
