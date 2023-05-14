package se.mbaeumer.fxlink.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FollowUpStatus {
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
}
