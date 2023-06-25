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

	protected Property<Date> created = new SimpleObjectProperty<>();
	public Property<Date> createdProperty(){return this.created;}
	public Date getCreated(){return this.created.getValue();}
	public void setCreated(Date created){this.created.setValue(created);}
	
	protected Property<Date> lastUpdated = new SimpleObjectProperty<>();
	public Property<Date> lastUpdatedProperty(){return this.lastUpdated;}
	public Date getLastUpdated(){return this.lastUpdated.getValue();}
	public void setLastUpdated(Date lastUpdated){this.lastUpdated.setValue(lastUpdated);}
	
	protected Property<Category> category = new SimpleObjectProperty<>();
	public Property<Category> categoryProperty(){return this.category;}
	public Category getCategory(){return this.category.getValue();}
	public void setCategory(Category category){this.category.setValue(category);}

	private BooleanProperty selected = new SimpleBooleanProperty();
	public BooleanProperty selectedProperty(){return selected;}
	public boolean isSelected(){return selected.get();}
	public void setSelected(boolean selected){this.selected.set(selected);}

	protected IntegerProperty followUpRank = new SimpleIntegerProperty(this, "followUpRank");
	public IntegerProperty followUpRankProperty(){return this.followUpRank;}
	public int getFollowUpRank(){return this.followUpRank.get();}
	public void setFollowUpRank(int followUpRank){this.followUpRank.set(followUpRank);}

	protected Property<FollowUpStatus> followUpStatus = new SimpleObjectProperty<>();
	public Property<FollowUpStatus> followUpStatusProperty(){return this.followUpStatus;}
	public FollowUpStatus getFollowUpStatus(){return this.followUpStatus.getValue();}
	public void setFollowUpStatus(FollowUpStatus followUpStatus){this.followUpStatus.setValue(followUpStatus);}

	public Link(String title, String url, String description){
		this.title.set(title);
		this.url.set(url);
		this.description.set(description);
		this.created.setValue(new Date());
		this.lastUpdated.setValue(new Date());
		this.followUpRank.set(-1);
	}
}
