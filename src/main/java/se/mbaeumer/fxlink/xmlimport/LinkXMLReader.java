package se.mbaeumer.fxlink.xmlimport;

import se.mbaeumer.fxlink.models.*;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class LinkXMLReader {
	public static final String CATEGORIES = "categories";
	public static final String CATEGORY = "category";
	public static final String LINKS = "links";
	public static final String LINK = "link";
	public static final String TAGS = "tags";
	public static final String TAG = "tag";
	public static final String LINKTAGS = "linktags";
	public static final String LINKTAG = "linktag";
	public static final String IMPORTITEMS = "importitems";
	public static final String IMPORTITEM = "importitem";
	
	private XMLInputFactory xmlInputFactory;
	private XMLEventReader xmlEventReader;
	private FileInputStream fileInputStream;
	private String configFile;
	
	private List<Category> categories = new ArrayList<>();
	private List<Link> links = new ArrayList<>();
	private List<Tag> tags = new ArrayList<>();
	private List<LinkTag> linkTags = new ArrayList<>();
	private Set<ImportItem> importItems = new HashSet<>();

	private List<FollowUpStatus> followUpStatuses;
	
	public LinkXMLReader(final String fileName, final List<FollowUpStatus> followUpStatuses) throws FileNotFoundException, XMLStreamException {
		this.configFile = fileName;
		this.followUpStatuses = followUpStatuses;
		this.init();
	}
	
	private void init() throws FileNotFoundException, XMLStreamException {
		this.xmlInputFactory = XMLInputFactory.newInstance();
		// Setup a new eventReader
		this.fileInputStream = new FileInputStream(configFile);
		this.xmlEventReader = this.xmlInputFactory
				.createXMLEventReader(this.fileInputStream);
	}
	
	public void readDataFromFile() throws XMLStreamException {
		while (this.xmlEventReader.hasNext()) {
			XMLEvent event = this.xmlEventReader.nextEvent();
			if (event.isStartElement()) {
				StartElement startElement = event.asStartElement();
				if (CATEGORIES.equals(startElement.getName().getLocalPart())) {
					this.categories = new ArrayList<>();
				}else if (CATEGORY.equals(startElement.getName().getLocalPart())) {
					Category category = new Category();
					category.setId(Integer.parseInt(startElement.getAttributeByName(
							new QName("id")).getValue()));
					category.setName(startElement.getAttributeByName(
							new QName("name")).getValue());
					category.setDescription(startElement.getAttributeByName(
							new QName("description")).getValue());

					DateInfo dateInfo = extractDateInfo(startElement);
					category.setCreated(dateInfo.getCreated());
					category.setLastUpdated(dateInfo.getLastUpdated());

					this.categories.add(category);

				}else if (LINKS.equals(startElement.getName().getLocalPart())) {
					this.links = new ArrayList<>();
				}else if (LINK.equals(startElement.getName().getLocalPart())) {
					String title = startElement.getAttributeByName(
							new QName("title")).getValue();
					String url = startElement.getAttributeByName(
							new QName("url")).getValue();
					String description = startElement.getAttributeByName(
							new QName("description")).getValue();
					Link link = new Link(title, url, description);
					link.setFollowUpRank(getFollowUpRank(startElement));
					link.setFollowUpStatus(getFollowUpStatus(startElement));

					link.setId(Integer.parseInt(startElement.getAttributeByName(
							new QName("id")).getValue()));

					DateInfo dateInfo = extractDateInfo(startElement);
					link.setCreated(dateInfo.getCreated());
					link.setLastUpdated(dateInfo.getLastUpdated());
					link.setFollowUpDate(dateInfo.getFollowUpDate());
					
					Attribute attribute = startElement.getAttributeByName(
							new QName("categoryid"));
					if (attribute != null){
						String categoryId = attribute.getValue();
						
						if (categoryId == null || categoryId.equals("")) {
							link.setCategory(null);
						}else{
							Category category = new Category();
							category.setId(Integer.parseInt(categoryId));
							link.setCategory(category);
						}
					}else{
						link.setCategory(null);
					}
					
					this.links.add(link);
				}else if (TAGS.equals(startElement.getName().getLocalPart())) {
					this.tags = new ArrayList<>();
				}else if (TAG.equals(startElement.getName().getLocalPart())) {
					Tag tag = new Tag();
					tag.setId(Integer.parseInt(startElement.getAttributeByName(
							new QName("id")).getValue()));
					tag.setName(startElement.getAttributeByName(
							new QName("name")).getValue());
					tag.setDescription(startElement.getAttributeByName(
							new QName("description")).getValue());

					DateInfo dateInfo = extractDateInfo(startElement);
					tag.setCreated(dateInfo.getCreated());
					tag.setLastUpdated(dateInfo.getLastUpdated());
					this.tags.add(tag);
				}else if (LINKTAGS.equals(startElement.getName().getLocalPart())) {
					this.linkTags = new ArrayList<>();
				}else if (LINKTAG.equals(startElement.getName().getLocalPart())) {
					LinkTag linkTag = new LinkTag();
					linkTag.setId(Integer.parseInt(startElement.getAttributeByName(
							new QName("id")).getValue()));
					linkTag.setLinkId(Integer.parseInt(startElement.getAttributeByName(
							new QName("linkid")).getValue()));
					linkTag.setTagId(Integer.parseInt(startElement.getAttributeByName(
							new QName("tagid")).getValue()));
					this.linkTags.add(linkTag);
				}else if (IMPORTITEMS.equals(startElement.getName().getLocalPart())) {
					this.importItems = new HashSet<>();
				}else if (IMPORTITEM.equals(startElement.getName().getLocalPart())) {
					ImportItem importItem = new ImportItem();
					importItem.setId(Integer.parseInt(startElement.getAttributeByName(
							new QName("id")).getValue()));
					importItem.setFilename(startElement.getAttributeByName(
							new QName("filename")).getValue());
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					Date date;

					try {
						date = df.parse(startElement.getAttributeByName(
								new QName("created")).getValue());
						importItem.setCreated(date);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					this.importItems.add(importItem);
				}

			}
		}
	}

	public DateInfo extractDateInfo(StartElement startElement){
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		DateInfo dateInfo = new DateInfo();

		try {
			dateInfo.setCreated(df.parse(startElement.getAttributeByName(
					new QName("created")).getValue()));
			dateInfo.setLastUpdated(df.parse(startElement.getAttributeByName(
					new QName("lastUpdated")).getValue()));
			if (startElement.getAttributeByName(new QName("followUpDate")) != null){
				dateInfo.setFollowUpDate(df.parse(startElement.getAttributeByName(
						new QName("followUpDate")).getValue()));
			}else{
				dateInfo.setFollowUpDate(null);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		return dateInfo;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public List<Link> getLinks() {
		return links;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public List<LinkTag> getLinkTags() {
		return linkTags;
	}

	public Set<ImportItem> getImportItems() {
		return importItems;
	}

	private int getFollowUpRank(final StartElement startElement){
		int attributeValue;
		try {
			String stringValue = startElement.getAttributeByName(
					new QName("followUpRank")).getValue();
			attributeValue = Integer.parseInt(stringValue);
		}catch (NullPointerException | NumberFormatException ex) {
			attributeValue = -1;
		}
		return attributeValue;
	}

	private FollowUpStatus getFollowUpStatus(final StartElement startElement){
		int id;
		try {
			String stringValue = startElement.getAttributeByName(
					new QName("followUpStatus")).getValue();
			id = Integer.parseInt(stringValue);
		}catch (NullPointerException | NumberFormatException ex) {
			FollowUpStatus defaultFollowUpStatus = this.followUpStatuses
					.stream()
					.filter(fus -> "NOT_NEEDED".equals(fus.getName()))
					.findFirst().orElseThrow(IllegalArgumentException::new);
			id = defaultFollowUpStatus.getId();
		}

		int finalId = id;
		return this.followUpStatuses.stream().filter(fus -> fus.getId() == finalId).findFirst().orElse(getDefaultFollowUpStatus());
	}

	private FollowUpStatus getDefaultFollowUpStatus(){
		return this.followUpStatuses
				.stream()
				.filter(fus -> "NOT_NEEDED".equals(fus.getName()))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
}