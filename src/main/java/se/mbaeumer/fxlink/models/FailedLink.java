package se.mbaeumer.fxlink.models;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FailedLink {
	protected StringProperty cause = new SimpleStringProperty(this, "cause");
	public StringProperty causeProperty(){return this.cause;}
	public String getCause(){return this.cause.get();}
	public void setCause(String cause){this.cause.set(cause);}

	protected Property<Link> link = new SimpleObjectProperty<Link>();
	public Property<Link> linkProperty(){return this.link;}
	public Link getLink(){return this.link.getValue();}
	public void setLink(Link link){this.link.setValue(link);}
	
	public FailedLink(Link link, String cause){
		this.link.setValue(link);
		this.cause.set(cause);
	}
}
