package se.mbaeumer.fxlink.api;

import se.mbaeumer.fxlink.handlers.GenericDBHandler;
import se.mbaeumer.fxlink.handlers.LinkReadDBHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.StopWordHandler;
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
    private final StopWordHandler stopWordHandler;

    public CsvExportHandler(LinkReadDBHandler linkReadDBHandler, URLHelper urlHelper, StopWordHandler stopWordHandler) {
        this.linkReadDBHandler = linkReadDBHandler;
        this.urlHelper = urlHelper;
        this.stopWordHandler = stopWordHandler;
    }

    public void getData(final String filename, List<Category> categories){
        List<Link> links = new ArrayList<>();
        for (Category category : categories){
            links.addAll(linkReadDBHandler.getAllLinksByCategoryId(GenericDBHandler.getInstance(), category.getId(), null));
        }

        File csvOutputFile = new File(filename);

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

        sb.append(data.getCategory().getName()).append(",");
        String wordsAsString = words.stream().collect(Collectors.joining(" "));
        sb.append(wordsAsString);

        return sb.toString();
    }

    private List<String> splitUrl(final Link link){
        String url = urlHelper.withoutProtocol(link.getURL());
        url = urlHelper.withoutPrefix(url);
        String[] urlParts = urlHelper.getUrlParts(url);
        List<String> words = new ArrayList<>(Arrays.asList(urlParts));

        return stopWordHandler.removeStopWordsFromList(words);
    }
}
