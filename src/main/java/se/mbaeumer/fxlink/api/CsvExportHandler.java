package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.URLHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExportHandler {

    private final LinkReadDBHandler linkReadDBHandler;
    private final URLHelper urlHelper;

    public CsvExportHandler(LinkReadDBHandler linkReadDBHandler, URLHelper urlHelper) {
        this.linkReadDBHandler = linkReadDBHandler;
        this.urlHelper = urlHelper;
    }

    public void getData(){
        List<Link> links = linkReadDBHandler.getAllLinksWithCategory(GenericDBHandler.getInstance());

        final String filename = "/Users/martinbaumer/Downloads/fxlink-data-stackoverflog.csv";
        File csvOutputFile = new File(filename);
        //PrintWriter pwr = new PrintWriter(csvOutputFile);

        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            pw.println("category,text");
            links.stream()
                    .map(this::convertToCSV)
                    .forEach(pw::println);
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    public String convertToCSV(Link data) {
        List<String> words = splitUrl(data);
        StringBuilder sb = new StringBuilder();
        String categoryName = "Stackoverflow blog".equals(data.getCategory().getName()) ? "Stackoverflow blog" : "None";
        if ("Stackoverflow blog".equals(data.getCategory().getName())){
            System.out.println("found " + categoryName);
        }
        sb.append(categoryName).append(",");
        String wordsAsString = words.stream().collect(Collectors.joining(" "));
        sb.append(wordsAsString);

        return sb.toString();
    }

    private List<String> splitUrl(final Link link){
        String url = urlHelper.withoutProtocol(link.getURL());
        url = urlHelper.withoutPrefix(url);
        String[] urlParts = urlHelper.getUrlParts(url);
        List<String> words = new ArrayList<>(Arrays.asList(urlParts));

        List<String> toExclude = words.stream().filter(key -> "for".equals(key)
                || key.length() <=1 || "of".equals(key) || "with".equals(key)
                || "the".equals(key) || "to".equals(key)
                || "com".equals(key) || "de".equals(key) || key.matches(".*\\d.*")
                || "html".equals(key) || "htm".equals(key)).collect(Collectors.toList());
        words.removeAll(toExclude);

        return words;
    }
}
