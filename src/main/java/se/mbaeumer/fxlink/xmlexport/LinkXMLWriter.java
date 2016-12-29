package se.mbaeumer.fxlink.xmlexport;

import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.LinkTag;
import se.mbaeumer.fxlink.models.Tag;
import se.mbaeumer.fxlink.util.ValueConstants;

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
		this.tags = TagReadDBHandler.getAllTags(GenericDBHandler.getInstance());
		this.linkTags = LinkTagReadDBHandler.getAllLinkTagEntries(GenericDBHandler.getInstance());
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
		
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", "fxlink-data");
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.endDoc);
		this.xmlEventWriter.close();

	}
	
	private void writeCategories() throws XMLStreamException {
		this.xmlEventWriter.add(this.xmlTab);
		StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
				"", "categories");
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeCategoryData();
		
		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", "categories");
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}
	
	private void writeCategoryData() throws XMLStreamException {
		for (Category category : this.categories){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"", "category");
			this.xmlEventWriter.add(rootStartElement);
			
			this.writeCategoryAttributes(category);
			
			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", "category");
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}
	
	private void writeCategoryAttributes(Category category) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", new Integer(category.getId()).toString());
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
				"", "links");
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeLinkData();
		
		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", "links");
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}
	
	private void writeLinkData() throws XMLStreamException {
		for (Link link : this.links){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"", "link");
			
			this.xmlEventWriter.add(rootStartElement);
			
			this.writeLinkAttributes(link);
			
			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", "link");
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}
	
	private void writeLinkAttributes(Link link) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", new Integer(link.getId()).toString());
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
			attribute = this.xmlEventFactory.createAttribute("categoryid", new Integer(link.getCategory().getId()).toString());
			this.xmlEventWriter.add(attribute);
		}else{
			
		}
		
		attribute = this.xmlEventFactory.createAttribute("created", link.getCreated().toString());
		this.xmlEventWriter.add(attribute);
		
		attribute = this.xmlEventFactory.createAttribute("lastUpdated", link.getLastUpdated().toString());
		this.xmlEventWriter.add(attribute);
	}
	
	private void writeTags() throws XMLStreamException {
		this.xmlEventWriter.add(this.xmlTab);
		StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
				"", "tags");
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeTagData();
		
		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", "tags");
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}
	
	private void writeTagData() throws XMLStreamException {
		for (Tag tag : this.tags){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"", "tag");
			
			this.xmlEventWriter.add(rootStartElement);
			
			this.writeTagAttributes(tag);
			
			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", "tag");
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}
	
	private void writeTagAttributes(Tag tag) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", new Integer(tag.getId()).toString());
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
				"", "linktags");
		this.xmlEventWriter.add(rootStartElement);
		this.xmlEventWriter.add(this.xmlEndLine);
		
		this.writeLinkTagData();
		
		this.xmlEventWriter.add(this.xmlTab);
		EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
				"", "linktags");
		this.xmlEventWriter.add(rootEndElement);
		this.xmlEventWriter.add(this.xmlEndLine);
	}
	
	private void writeLinkTagData() throws XMLStreamException {
		for (LinkTag linkTag : this.linkTags){
			this.xmlEventWriter.add(this.xmlTab);
			this.xmlEventWriter.add(this.xmlTab);
			StartElement rootStartElement = this.xmlEventFactory.createStartElement("",
					"", "linktag");
			
			this.xmlEventWriter.add(rootStartElement);
			
			this.writeLinkTagAttributes(linkTag);
			
			EndElement rootEndElement = this.xmlEventFactory.createEndElement("",
					"", "linktag");
			this.xmlEventWriter.add(rootEndElement);
			this.xmlEventWriter.add(this.xmlEndLine);
		}
	}
	
	private void writeLinkTagAttributes(LinkTag tag) throws XMLStreamException {
		XMLEvent attribute = this.xmlEventFactory.createAttribute("id", new Integer(tag.getId()).toString());
		this.xmlEventWriter.add(attribute);
		
		attribute = this.xmlEventFactory.createAttribute("linkid", new Integer(tag.getLinkId()).toString());
		this.xmlEventWriter.add(attribute);
		
		attribute = this.xmlEventFactory.createAttribute("tagid", new Integer(tag.getTagId()).toString());
		this.xmlEventWriter.add(attribute);
	}
}
