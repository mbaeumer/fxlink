package se.mbaeumer.fxlink.xmlexport;

import se.mbaeumer.fxlink.api.ImportItemHandler;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.*;
import se.mbaeumer.fxlink.util.ValueConstants;
import se.mbaeumer.fxlink.xmlimport.LinkXMLReader;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.util.List;

public class LinkXMLWriter {
	
	private XMLOutputFactory xmlOutputFactory;
	private XMLEventWriter xmlEventWriter;
	private XMLEventFactory xmlEventFactory;
	private XMLEvent xmlEndLine;
	private XMLEvent xmlTab;
	private StartDocument startDoc;
	private EndDocument endDoc;
	
	private List<Link> links;
	private List<Category> categories;
	private List<Tag> tags;
	private List<LinkTag> linkTags;
	private List<ImportItem> importItems;
	
	private String configFile;
	
	
	public LinkXMLWriter(String fileName) throws FileNotFoundException, XMLStreamException {
		this.configFile = fileName;
		this.init();
	}
	
	private void init() throws FileNotFoundException, XMLStreamException {
		this.xmlOutputFactory = XMLOutputFactory.newInstance();
		this.xmlEventWriter = this.xmlOutputFactory.createXMLEventWriter(new FileOutputStream(configFile));
		this.xmlEventFactory = XMLEventFactory.newInstance();
		this.xmlEndLine = this.xmlEventFactory.createDTD("\n");
		this.xmlTab = this.xmlEventFactory.createDTD("\t");
		
		this.startDoc = this.xmlEventFactory.createStartDocument();
		this.endDoc = this.xmlEventFactory.createEndDocument();
	}
	
	private void getDataForExport() throws SQLException {
		// get all categories
		this.categories = CategoryReadDBHandler.getAllCategories(GenericDBHandler.getInstance());
		this.links = LinkReadDBHandler.getAllLinks(GenericDBHandler.getInstance());
		TagDBHandler tagDBHandler = new TagDBHandler();

		this.tags = tagDBHandler.getAllTags(tagDBHandler.constructSqlString(null), new HsqldbConnectionHandler());
		this.linkTags = LinkTagReadDBHandler.getAllLinkTagEntries(GenericDBHandler.getInstance());

		ImportItemHandler importItemHandler = new ImportItemHandler();
		this.importItems = importItemHandler.readImportItems();
	}
	
	public void writeDataToFile() throws XMLStreamException, SQLException {
		this.getDataForExport();
		// add start document
		this.xmlEventWriter.add(this.startDoc);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
				"", "fxlink-data");
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeCategories();
		this.writeLinks();
		this.writeTags();
		this.writeLinkTags();
		this.writeImportItems();
		
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", "fxlink-data");
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.endDoc);
		this.xmlEventWriter.close();

	}
	
	private void writeCategories() throws XMLStreamException {
		this.xmlEventWriter.add(this.xmlTab);
		StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
				"", LinkXMLReader.CATEGORIES);
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeCategoryData();
		
		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", LinkXMLReader.CATEGORIES);
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}
	
	private void writeCategoryData() throws XMLStreamException {
		for (Category category : this.categories){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"", LinkXMLReader.CATEGORY);
			this.xmlEventWriter.add(rootStartElement);
			
			this.writeCategoryAttributes(category);
			
			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", LinkXMLReader.CATEGORY);
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}
	
	private void writeCategoryAttributes(Category category) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", Integer.toString(category.getId()));
		this.xmlEventWriter.add(attribute);
		
		if (category.getName().equalsIgnoreCase(ValueConstants.VALUE_NEW)
				|| category.getName().equalsIgnoreCase(ValueConstants.VALUE_N_A)){
			category.setName("");
		}
		
		attribute = this.xmlEventFactory.createAttribute("name", category.getName());
		this.xmlEventWriter.add(attribute);
		
		if (category.getDescription().equalsIgnoreCase(ValueConstants.VALUE_NEW)
				|| category.getDescription().equalsIgnoreCase(ValueConstants.VALUE_N_A)){
			category.setDescription("");
		}
		attribute = this.xmlEventFactory.createAttribute("description", category.getDescription());
		this.xmlEventWriter.add(attribute);
		attribute = this.xmlEventFactory.createAttribute("created", category.getCreated().toString());
		this.xmlEventWriter.add(attribute);
		attribute = this.xmlEventFactory.createAttribute("lastUpdated", category.getLastUpdated().toString());
		this.xmlEventWriter.add(attribute);
	}
	
	private void writeLinks() throws XMLStreamException {
		this.xmlEventWriter.add(this.xmlTab);
		StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
				"", LinkXMLReader.LINKS);
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeLinkData();
		
		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", LinkXMLReader.LINKS);
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}
	
	private void writeLinkData() throws XMLStreamException {
		for (Link link : this.links){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"", LinkXMLReader.LINK);
			
			this.xmlEventWriter.add(rootStartElement);
			
			this.writeLinkAttributes(link);
			
			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", LinkXMLReader.LINK);
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}
	
	private void writeLinkAttributes(Link link) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", Integer.valueOf(link.getId()).toString());
		this.xmlEventWriter.add(attribute);
		
		if (link.getURL().equalsIgnoreCase(ValueConstants.VALUE_NEW)
				|| link.getURL().equalsIgnoreCase(ValueConstants.VALUE_N_A)){
			link.setURL("");
		}
		attribute = this.xmlEventFactory.createAttribute("url", link.getURL());
		this.xmlEventWriter.add(attribute);
		
		if (link.getTitle() == null 
				|| link.getTitle().equalsIgnoreCase(ValueConstants.VALUE_NEW)
				|| link.getTitle().equalsIgnoreCase(ValueConstants.VALUE_N_A)){
			link.setTitle("");
		}
		attribute = this.xmlEventFactory.createAttribute("title", link.getTitle());
		this.xmlEventWriter.add(attribute);
		
		if (link.getDescription() == null 
				|| link.getDescription().equalsIgnoreCase(ValueConstants.VALUE_NEW)
				|| link.getDescription().equalsIgnoreCase(ValueConstants.VALUE_N_A)){
			link.setDescription("");
		}
		attribute = this.xmlEventFactory.createAttribute("description", link.getDescription());
		this.xmlEventWriter.add(attribute);
		
		if (link.getCategory() != null){
			attribute = this.xmlEventFactory.createAttribute("categoryid", Integer.valueOf(link.getCategory().getId()).toString());
			this.xmlEventWriter.add(attribute);
		}

		attribute = this.xmlEventFactory.createAttribute("created", link.getCreated().toString());
		this.xmlEventWriter.add(attribute);
		
		attribute = this.xmlEventFactory.createAttribute("lastUpdated", link.getLastUpdated().toString());
		this.xmlEventWriter.add(attribute);
	}
	
	private void writeTags() throws XMLStreamException {
		this.xmlEventWriter.add(this.xmlTab);
		StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
				"", LinkXMLReader.TAGS);
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeTagData();
		
		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", LinkXMLReader.TAGS);
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}
	
	private void writeTagData() throws XMLStreamException {
		for (Tag tag : this.tags){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"", LinkXMLReader.TAG);
			
			this.xmlEventWriter.add(rootStartElement);
			
			this.writeTagAttributes(tag);
			
			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", LinkXMLReader.TAG);
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}
	
	private void writeTagAttributes(Tag tag) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", Integer.valueOf(tag.getId()).toString());
		this.xmlEventWriter.add(attribute);
		
		if (tag.getName().equalsIgnoreCase(ValueConstants.VALUE_NEW)
				|| tag.getName().equalsIgnoreCase(ValueConstants.VALUE_N_A)){
			tag.setName("");
		}
		attribute = this.xmlEventFactory.createAttribute("name", tag.getName());
		this.xmlEventWriter.add(attribute);
		
		if (tag.getDescription().equalsIgnoreCase(ValueConstants.VALUE_NEW)
				|| tag.getDescription().equalsIgnoreCase(ValueConstants.VALUE_N_A)){
			tag.setDescription("");
		}
		attribute = this.xmlEventFactory.createAttribute("description", tag.getDescription());
		this.xmlEventWriter.add(attribute);
		
		attribute = this.xmlEventFactory.createAttribute("created", tag.getCreated().toString());
		this.xmlEventWriter.add(attribute);
		
		attribute = this.xmlEventFactory.createAttribute("lastUpdated", tag.getLastUpdated().toString());
		this.xmlEventWriter.add(attribute);
	}
	
	private void writeLinkTags() throws XMLStreamException {
		this.xmlEventWriter.add(this.xmlTab);
		StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
				"", LinkXMLReader.LINKTAGS);
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeLinkTagData();
		
		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", LinkXMLReader.LINKTAGS);
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}
	
	private void writeLinkTagData() throws XMLStreamException {
		for (LinkTag linkTag : this.linkTags){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"", LinkXMLReader.LINKTAG);
			
			this.xmlEventWriter.add(rootStartElement);
			
			this.writeLinkTagAttributes(linkTag);
			
			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", LinkXMLReader.LINKTAG);
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}
	
	private void writeLinkTagAttributes(LinkTag tag) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", Integer.valueOf(tag.getId()).toString());
		this.xmlEventWriter.add(attribute);
		
		attribute = this.xmlEventFactory.createAttribute("linkid", Integer.valueOf(tag.getLinkId()).toString());
		this.xmlEventWriter.add(attribute);
		
		attribute = this.xmlEventFactory.createAttribute("tagid", Integer.valueOf(tag.getTagId()).toString());
		this.xmlEventWriter.add(attribute);
	}

	private void writeImportItems() throws XMLStreamException {
		this.xmlEventWriter.add(this.xmlTab);
		StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
				"", LinkXMLReader.IMPORTITEMS);
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);

		this.writeImportItemData();

		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", LinkXMLReader.IMPORTITEMS);
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}

	private void writeImportItemData() throws XMLStreamException {
		for (ImportItem importItem : this.importItems){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"",LinkXMLReader.IMPORTITEM);

			this.xmlEventWriter.add(rootStartElement);

			this.writeImportItemAttributes(importItem);

			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", LinkXMLReader.IMPORTITEM);
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}

	private void writeImportItemAttributes(ImportItem importItem) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", Integer.valueOf(importItem.getId()).toString());
		this.xmlEventWriter.add(attribute);

		attribute = this.xmlEventFactory.createAttribute("filename", importItem.getFilename());
		this.xmlEventWriter.add(attribute);

		attribute = this.xmlEventFactory.createAttribute("created", importItem.getCreated().toString());
		this.xmlEventWriter.add(attribute);
	}
}
