package se.mbaeumer.fxlink.models;

import javafx.beans.property.*;

import java.util.Date;

public class Link {
	
	protected IntegerProperty id = new SimpleIntegerProperty(this, "id");
	public IntegerProperty idProperty(){return this.id;}
	public int getId(){return this.id.get();}
	public void setId(int id){this.id.set(id);}
	
	protected StringProperty title = new SimpleStringProperty(this, "title");
	public StringProperty titleProperty(){return this.title;}
	public String getTitle(){return this.title.get();}
	public void setTitle(String title){this.title.set(title);}
	
	protected StringProperty url = new SimpleStringProperty(this, "url");
	public StringProperty urlProperty(){return this.url;}
	public String getURL(){return this.url.get();}
	public void setURL(String url){this.url.set(url);}
	
	protected StringProperty description = new SimpleStringProperty(this, "description");
	public StringProperty descriptionProperty(){return this.description;}
	public String getDescription(){return this.description.get();}
	public void setDescription(String description){this.description.set(description);}

	protected Property<Date> created = new SimpleObjectProperty<Date>();
	public Property<Date> createdProperty(){return this.created;}
	public Date getCreated(){return this.created.getValue();}
	public void setCreated(Date created){this.created.setValue(created);}
	
	protected Property<Date> lastUpdated = new SimpleObjectProperty<Date>();
	public Property<Date> lastUpdatedProperty(){return this.lastUpdated;}
	public Date getLastUpdated(){return this.lastUpdated.getValue();}
	public void setLastUpdated(Date lastUpdated){this.lastUpdated.setValue(lastUpdated);}
	
	protected Property<Category> category = new SimpleObjectProperty<Category>();
	public Property<Category> categoryProperty(){return this.category;}
	public Category getCategory(){return this.category.getValue();}
	public void setCategory(Category category){this.category.setValue(category);}

		
	public Link(String title, String url, String description){
		this.title.set(title);
		this.url.set(url);
		this.description.set(description);
		this.created.setValue(new Date());
		this.lastUpdated.setValue(new Date());
	}
}
