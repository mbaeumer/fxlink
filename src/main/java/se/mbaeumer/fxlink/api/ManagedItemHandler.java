package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.HsqldbConnectionHandler;
import se.mbaeumer.fxlink.handlers.ManagedItemDBOperationHandler;

import java.util.List;

public class ManagedItemHandler {
	public static List<String> getManagedItems(ManagedItemDBOperationHandler managedItemDBOperationHandler){
		String sql = managedItemDBOperationHandler.constructSqlString(null);
		return managedItemDBOperationHandler.getAllManagedItems(sql, new HsqldbConnectionHandler());
	}
}
