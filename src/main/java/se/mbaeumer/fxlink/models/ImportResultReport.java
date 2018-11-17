package se.mbaeumer.fxlink.models;

import java.util.Date;
import java.util.List;

public class ImportResultReport {
	private String filename;
	private Date importDate;
	private List<Link> successfulLinks;
	private List<FailedLink> failedLinks;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public Date getImportDate() {
		return importDate;
	}
	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}
	public List<Link> getSuccessfulLinks() {
		return successfulLinks;
	}
	public void setSuccessfulLinks(List<Link> successfulLinks) {
		this.successfulLinks = successfulLinks;
	}
	public List<FailedLink> getFailedLinks() {
		return failedLinks;
	}
	public void setFailedLinks(List<FailedLink> failedLinks) {
		this.failedLinks = failedLinks;
	}
}
