package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkTagDeletionDBHandler;

import java.sql.SQLException;

public class LinkTagHandler {
	public static void deleteAllLinkTags() throws SQLException{
		LinkTagDeletionDBHandler.deleteAllLinkTags(GenericDBHandler.getInstance());
	}
}
