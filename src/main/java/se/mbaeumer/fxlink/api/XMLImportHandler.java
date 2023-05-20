package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.*;
import se.mbaeumer.fxlink.xmlimport.LinkXMLReader;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.List;

public class XMLImportHandler {
	
	private LinkXMLReader reader;
	private final CategoryHandler categoryHandler;

	private final FollowUpStatusReadDBHandler followUpStatusReadDBHandler;

	public XMLImportHandler(CategoryHandler categoryHandler, FollowUpStatusReadDBHandler followUpStatusReadDBHandler) {
		this.categoryHandler = categoryHandler;
		this.followUpStatusReadDBHandler = followUpStatusReadDBHandler;
	}

	public void readData(String filename) throws FileNotFoundException, XMLStreamException{
		List<FollowUpStatus> followUpStatuses = followUpStatusReadDBHandler.getFollowUpStatuses(GenericDBHandler.getInstance());
		reader = new LinkXMLReader(filename, followUpStatuses);
		reader.readDataFromFile();
		reader.getCategories();		
	}
	
	public void truncateDatabase() throws SQLException{
		LinkTagHandler.deleteAllLinkTags();
		TagHandler.deleteAllTags();
		LinkHandler.deleteAllLinks();
		categoryHandler.deleteAllCategories();

		ImportItemHandler importItemHandler = new ImportItemHandler();
		importItemHandler.deleteAllImportItems();
	}
	
	public void importData() throws SQLException{
		importCategories();
		importLinks();
		importTags();
		importLinkTags();
		importImportItems();
	}
	
	private void importCategories() throws SQLException{
		for (Category category : reader.getCategories()){
			CategoryImportDBHandler.importCategory(category, GenericDBHandler.getInstance());
		}
	}
	
	private void importLinks() throws SQLException{
		for (Link link : reader.getLinks()){
			LinkImportDBHandler.importLinks(GenericDBHandler.getInstance(), link);
		}
	}
	
	private void importTags() throws SQLException{
		for (Tag tag : reader.getTags()){
			TagImportDBHandler.importTag(tag, GenericDBHandler.getInstance());
		}
	}
	
	private void importLinkTags() throws SQLException{
		for (LinkTag linkTag : reader.getLinkTags()){
			LinkTagImportDBHandler.importLinkTag(linkTag, GenericDBHandler.getInstance());
		}
	}

	private void importImportItems(){
		ImportItemImportDBandler importItemImportDBandler = new ImportItemImportDBandler();
		for (ImportItem linkTag : reader.getImportItems()){
			try {
				importItemImportDBandler.importImportItem(linkTag, new HsqldbConnectionHandler());
			} catch (SQLException e) {
			}
		}
	}


}
