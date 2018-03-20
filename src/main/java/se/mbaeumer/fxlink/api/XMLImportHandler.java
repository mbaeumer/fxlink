package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.LinkTag;
import se.mbaeumer.fxlink.models.Tag;
import se.mbaeumer.fxlink.xmlimport.LinkXMLReader;

import javax.xml.stream.XMLStreamException;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class XMLImportHandler {
	
	private LinkXMLReader reader;
	public XMLImportHandler(){
		
	}
	
	public void readData(String filename) throws FileNotFoundException, XMLStreamException{
		reader = new LinkXMLReader(filename);
		reader.readDataFromFile();
		reader.getCategories();		
	}
	
	public void truncateDatabase() throws SQLException{
		// truncate in the following order:
		//1. link tags
		LinkTagHandler.deleteAllLinkTags();
		//2. tags
		TagHandler.deleteAllTags();
		//3. links
		LinkHandler.deleteAllLinks();
		//4. categories
		CategoryHandler.deleteAllCategories();
	}
	
	public void importData() throws SQLException{
		// 1. categories
		importCategories();
		
		// 2. links
		importLinks();
		
		// 3. tags
		importTags();
		
		// 4. linktags
		importLinkTags();
	}
	
	private void importCategories() throws SQLException{
		for (Category category : reader.getCategories()){
			CategoryImportDBHandler.importCategory(category, GenericDBHandler.getInstance());
		}
	}
	
	private void importLinks() throws SQLException{
		for (Link link : reader.getLinks()){
			LinkImportDBHandler.importLinksIntoDatabase(GenericDBHandler.getInstance(), link);
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
}
