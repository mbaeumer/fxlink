package se.mbaeumer.fxlink.models;

import javafx.beans.property.*;

public class SelectableCategory {
    private StringProperty name = new SimpleStringProperty();
    private BooleanProperty selected = new SimpleBooleanProperty(false);
    private IntegerProperty id = new SimpleIntegerProperty();

    public String getName() {
        return name.get();
    }
    public void setName(String name){
        this.name.set(name);
    }
    public StringProperty nameProperty() {
        return name;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
    public boolean isSelected() {
        return selected.get();
    }
    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public void setId(int id){
        this.id.set(id);
    }
    public int getId(){
        return id.get();
    }
}
