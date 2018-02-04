package se.mbaeumer.fxlink.handlers;

import se.mbaeumer.fxlink.models.Category;

public interface DatabaseOperationHandler {
    String constructSqlString(Object object);
}
