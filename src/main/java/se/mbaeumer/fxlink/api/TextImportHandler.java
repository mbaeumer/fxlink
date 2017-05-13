package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkCreationDBHandler;
import se.mbaeumer.fxlink.models.FailedLink;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.SqlExceptionMapper;
import se.mbaeumer.fxlink.util.URLValidator;
import se.mbaeumer.fxlink.util.ValueConstants;

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
		String fileNameOnly = textFile.getName();

        String line = null;

        FileReader fileReader = 
            new FileReader(fileName);

        BufferedReader bufferedReader =
            new BufferedReader(fileReader);

        while((line = bufferedReader.readLine()) != null) {
            createLink(line, fileNameOnly);
        }   

        bufferedReader.close();
	}
	
	private void createLink(String line, String fileName){

		Link link = new Link(ValueConstants.VALUE_NEW, line, createDescription(fileName));
		link.setCategory(null);

		FailedLink fl = null;
		if (!URLValidator.isValidURL(line)){
			fl = new FailedLink(link, "The URL seems to be incorrect");
			failedLinks.add(fl);
			return;
		}
		
		try {
			String sql = LinkCreationDBHandler.constructSqlString(link);
			int newId = LinkCreationDBHandler.createLink(sql, GenericDBHandler.getInstance());
			link.setId(newId);
			importedLinks.add(link);
		} catch (SQLException e) {
			fl = new FailedLink(link, SqlExceptionMapper.constructErrorMessage(e.getMessage()));
			failedLinks.add(fl);
		}
	}

	private String createDescription(String fileName){
		String description = "Imported from " + fileName;
		return  description;
	}
}
