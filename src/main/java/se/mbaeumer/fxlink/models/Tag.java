package se.mbaeumer.fxlink.models;

import javafx.beans.property.*;

import java.util.Date;

public class Tag {
	private IntegerProperty id = new SimpleIntegerProperty(this, "id");
	public IntegerProperty idProperty(){return this.id;}
	public int getId(){return this.id.get();}
	public void setId(int id){this.id.set(id);}
	
	private StringProperty name = new SimpleStringProperty(this, "name");
	public StringProperty nameProperty(){return this.name;}
	public String getName(){return this.name.get();}
	public void setName(String name){this.name.set(name);}
	
	private StringProperty description = new SimpleStringProperty(this, "description");
	public StringProperty descriptionProperty(){return this.description;}
	public String getDescription(){return this.description.get();}
	public void setDescription(String description){this.description.set(description);}

	private Property<Date> created = new SimpleObjectProperty<>();
	public Property<Date> createdProperty(){return this.created;}
	public Date getCreated(){return this.created.getValue();}
	public void setCreated(Date created){this.created.setValue(created);}
	
	private Property<Date> lastUpdated = new SimpleObjectProperty<>();
	public Property<Date> lastUpdatedProperty(){return this.lastUpdated;}
	public Date getLastUpdated(){return this.lastUpdated.getValue();}
	public void setLastUpdated(Date lastUpdated){this.lastUpdated.setValue(lastUpdated);}

}
