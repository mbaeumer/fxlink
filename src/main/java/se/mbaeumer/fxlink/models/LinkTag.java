package se.mbaeumer.fxlink.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class LinkTag {
	protected IntegerProperty id = new SimpleIntegerProperty(this, "id");
	public IntegerProperty idProperty(){return this.id;}
	public int getId(){return this.id.get();}
	public void setId(int id){this.id.set(id);}
	
	protected IntegerProperty linkId = new SimpleIntegerProperty(this, "linkId");
	public IntegerProperty linkIdProperty(){return this.linkId;}
	public int getLinkId(){return this.linkId.get();}
	public void setLinkId(int linkId){this.linkId.set(linkId);}
	
	protected IntegerProperty tagId = new SimpleIntegerProperty(this, "tagId");
	public IntegerProperty tagIdProperty(){return this.tagId;}
	public int getTagId(){return this.tagId.get();}
	public void setTagId(int tagId){this.tagId.set(tagId);}
}
