package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.FollowUpStatusReadDBHandler;
import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkCreationDBHandler;
import se.mbaeumer.fxlink.models.FailedLink;
import se.mbaeumer.fxlink.models.FollowUpStatus;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TextImportHandler {
	
	private final List<Link> importedLinks = new ArrayList<>();
	private final List<FailedLink> failedLinks = new ArrayList<>();
	private LinkCreationDBHandler linkCreationDBHandler = new LinkCreationDBHandler();
	private TitleHandler titleHandler;

	private FollowUpStatusReadDBHandler followUpStatusReadDBHandler = new FollowUpStatusReadDBHandler();
	
	public List<Link> getImportedLinks() {
		return importedLinks;
	}

	public List<FailedLink> getFailedLinks() {
		return failedLinks;
	}

	public void importFromTextFile(File textFile) throws IOException{
		FollowUpStatus followUpStatus = setDefaultFollowUpStatus();
		String fileName = textFile.getCanonicalPath();
        String line;

        FileReader fileReader = 
            new FileReader(fileName);

        BufferedReader bufferedReader =
            new BufferedReader(fileReader);

        while((line = bufferedReader.readLine()) != null) {
            if (line.length() > 0) {
				createLink(line, followUpStatus);
			}
        }   

        bufferedReader.close();
	}
	
	private void createLink(String line, FollowUpStatus followUpStatus){

		Link link = new Link(createTitle(line), line, ValueConstants.VALUE_NEW);
		link.setCategory(null);

		FailedLink fl;
		if (!URLValidator.isValidURL(line)){
			fl = new FailedLink(link, "The URL seems to be incorrect");
			failedLinks.add(fl);
			return;
		}
		
		try {
			link.setFollowUpStatus(followUpStatus);
			String sql = linkCreationDBHandler.constructSqlString(link);
			int newId = linkCreationDBHandler.createLink(sql, GenericDBHandler.getInstance());
			link.setId(newId);
			importedLinks.add(link);
		} catch (SQLException e) {
			fl = new FailedLink(link, SqlExceptionMapper.constructErrorMessage(e.getMessage()));
			failedLinks.add(fl);
		}
	}

	private String createTitle(String url){
		titleHandler = new TitleHandler(new LinkTitleUtilImpl(), new YoutubeCrawler());
		return titleHandler.generateTitle(new Link("", url,""));
	}

	private FollowUpStatus setDefaultFollowUpStatus(){
		List<FollowUpStatus> followUpStatuses = followUpStatusReadDBHandler.getFollowUpStatuses(GenericDBHandler.getInstance());
		return followUpStatuses
				.stream()
				.filter(s -> "NOT_NEEDED".equals(s.getName()))
				.findFirst().orElseThrow(IllegalArgumentException::new);
	}
}