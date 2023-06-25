package se.mbaeumer.fxlink.models;

import javafx.beans.property.*;

import java.util.Date;

public class ImportItem {
    protected IntegerProperty id = new SimpleIntegerProperty(this, "id");
    public IntegerProperty idProperty(){return this.id;}
    public int getId(){return this.id.get();}
    public void setId(int id){this.id.set(id);}

    protected StringProperty filename = new SimpleStringProperty(this, "filename");
    public StringProperty filenameProperty(){return this.filename;}
    public String getFilename(){return this.filename.get();}
    public void setFilename(String filename){this.filename.set(filename);}

    protected Property<Date> created = new SimpleObjectProperty<>(this, "created");
    public Property<Date> createdProperty(){return this.created;}
    public Date getCreated(){return this.created.getValue();}
    public void setCreated(Date created){this.created.setValue(created);}
}
