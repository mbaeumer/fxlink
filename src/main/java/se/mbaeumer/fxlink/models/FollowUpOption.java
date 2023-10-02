package se.mbaeumer.fxlink.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FollowUpOption {
    private StringProperty name = new SimpleStringProperty(this, "name");
    public StringProperty nameProperty(){return this.name;}
    public String getName(){return this.name.get();}
    public void setName(String name){this.name.set(name);}

    public FollowUpOption(String name) {
        this.name.set(name);
    }

    public static FollowUpOption of(FollowUpStatus followUpStatus){
        return new FollowUpOption(followUpStatus.getName());
    }
}
