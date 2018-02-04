package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Tag;

import java.util.List;

public interface TagDBOperationHandler extends DatabaseOperationHandler {
    List<Tag> getAllTags(String sql, DatabaseConnectionHandler databaseConnectionHandler);
}
