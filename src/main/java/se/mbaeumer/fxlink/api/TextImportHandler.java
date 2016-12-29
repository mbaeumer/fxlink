package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkCreationDBHandler;
import se.mbaeumer.fxlink.models.FailedLink;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.URLValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TextImportHandler {
	
	private List<Link> importedLinks = new ArrayList<Link>();
	private List<FailedLink> failedLinks = new ArrayList<FailedLink>();
	
	public List<Link> getImportedLinks() {
		return importedLinks;
	}

	public List<FailedLink> getFailedLinks() {
		return failedLinks;
	}

	public void importFromTextFile(File textFile) throws IOException{
		String fileName = textFile.getCanonicalPath();

        // This will reference one line at a time
        String line = null;

        FileReader fileReader = 
            new FileReader(fileName);

        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader = 
            new BufferedReader(fileReader);

        while((line = bufferedReader.readLine()) != null) {
            createLink(line);
        }   

        // Always close files.
        bufferedReader.close();         
	}
	
	private void createLink(String line){
		Link link = new Link(null, line, null);
		link.setCategory(null);
		
		FailedLink fl = null;
		if (!URLValidator.isValidURL(line)){
			fl = new FailedLink(link, "The URL seems to be incorrect");
			failedLinks.add(fl);
			return;
		}
		
		try {
			LinkCreationDBHandler.createLink(link, GenericDBHandler.getInstance());
			importedLinks.add(link);
		} catch (SQLException e) {
			fl = new FailedLink(link, e.getMessage());
			failedLinks.add(fl);
		}
	}
}
