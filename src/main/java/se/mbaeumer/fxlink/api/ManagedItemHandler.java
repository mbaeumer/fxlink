package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.ManagedItemDBHandler;

import java.util.List;

public class ManagedItemHandler {
	public static List<String> getManagedItems(){
		return ManagedItemDBHandler.getAllManagedItems(GenericDBHandler.getInstance());
	}
}
