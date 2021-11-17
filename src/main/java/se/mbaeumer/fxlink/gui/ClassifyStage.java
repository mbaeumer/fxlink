package se.mbaeumer.fxlink.gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import se.mbaeumer.fxlink.api.LinkHandler;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassifyStage extends Stage {
    private Scene scene;
    private FlowPane flowGeneral;
    private TableView tvLinks;
    private LinkHandler linkHandler;
    private LinkReadDBHandler linkReadDBHandler;

    public ClassifyStage() {
        super();
        this.initRootPane();
        this.initScene();
        ChangeListener<Number> widthListener = (observable, old, newValue) -> {
            this.tvLinks.setPrefWidth(this.flowGeneral.getWidth()-15);
        };
        this.widthProperty().addListener(widthListener);
    }

    private void initRootPane(){
        this.flowGeneral = new FlowPane();
        this.flowGeneral.setOrientation(Orientation.HORIZONTAL);
        this.flowGeneral.setHgap(10);
        this.flowGeneral.setVgap(10);
        this.flowGeneral.setPadding(new Insets(5, 10, 5, 10));
    }

    private void initScene() {
        int width = 700;
        int height = 450;
        this.scene = new Scene(this.flowGeneral, width, height);
        this.scene.setFill(Color.WHITESMOKE);

        this.setTitle("Import history");
        this.setScene(this.scene);

        this.initTableView();
        this.createTableViewColumns();
        this.tvLinks.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.linkReadDBHandler = new LinkReadDBHandler();
        double pStackOverflow = calculateSingle("Stackoverflow blog", "stackoverflow");
        double pBlog = calculateSingle("Stackoverflow blog", "developer");
        calculateCombinedHardCoded(pStackOverflow, pBlog);
        calculateCombinedGeneric("Stackoverflow blog", List.of("stackoverflow", "developer"));
        //calculateCombinedGeneric("Java programming", List.of("stackoverflow", "java"));
    }

    public void initTableView(){
        this.tvLinks = new TableView();
        this.linkHandler = new LinkHandler(new LinkReadDBHandler(), new LinkTagReadDBHandler(),
                new LinkCreationDBHandler(), new LinkUpdateDBHandler(), new LinkDeletionDBHandler());
        this.tvLinks.setItems(FXCollections.observableList(this.linkHandler.getLinks()));

        this.flowGeneral.getChildren().add(this.tvLinks);
    }

    public void createTableViewColumns(){
        TableColumn urlCol = new TableColumn("Url");
        urlCol.setCellValueFactory(new PropertyValueFactory("url"));
        urlCol.setCellFactory(TextFieldTableCell.forTableColumn());

        // create filename column
        TableColumn titleCol = new TableColumn("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory("title"));
        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());

        // create description column
        TableColumn descriptionCol = new TableColumn("Description");
        descriptionCol.setCellValueFactory(new PropertyValueFactory("description"));
        descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn categoryCol = new TableColumn("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<Link, Category>("category"));
        categoryCol.setCellFactory(new Callback<TableColumn<Link, Category>, TableCell<Link, Category>>(){

            @Override
            public TableCell<Link, Category> call(TableColumn<Link, Category> param) {

                TableCell<Link, Category> categoryCell = new TableCell<Link, Category>(){

                    @Override
                    protected void updateItem(Category item, boolean empty) {
                        if (item != null) {
                            setText(item.getName());
                        }else{
                            setText(null);
                        }
                    }
                };
                return categoryCell;
            }
        });

        this.tvLinks.getColumns().addAll(urlCol, titleCol, descriptionCol, categoryCol);

    }

    private double calculateSingle(final String categoryName, final String word){
        List<Link> allLinks = this.linkReadDBHandler.getAllLinksWithCategory(GenericDBHandler.getInstance());
        List<Link> stackOvLinks = allLinks.stream()
                .filter(link -> link.getCategory()!=null && categoryName.equals(link.getCategory().getName()))
                .collect(Collectors.toList());
        List<Link> linksContainStackOv = allLinks.stream()
                .filter(link -> link.getURL().contains(word))
                .collect(Collectors.toList());
        List<Link> linksPBA = stackOvLinks.stream().filter(link -> link.getURL().contains(word))
                .collect(Collectors.toList());
        double pA = (double) stackOvLinks.size() / (double) allLinks.size();
        double pB = (double) linksContainStackOv.size() / (double) allLinks.size();
        double pBA = (double) linksPBA.size() / (double) stackOvLinks.size();

        double result = pBA * (pA / pB);
        System.out.println(result);

        return result;

        //How many links are assigned to “stackoverflow blog” → P(A)
        //How many links contain “stackoverflow”? → P(B)
        //How many links assigned to “stackoverflow blog” contain the word “stackoverflow” → P(B|A)

    }

    public void calculateCombinedGeneric(final String categoryName, final List<String> words){
        List<Link> allLinks = this.linkReadDBHandler.getAllLinksWithCategory(GenericDBHandler.getInstance());
        List<Link> linksInCategory = allLinks.stream()
                .filter(link -> link.getCategory()!=null && categoryName.equals(link.getCategory().getName()))
                .collect(Collectors.toList());

        double pInCategory = (double) linksInCategory.size() / (double) allLinks.size();

        Map<String, Double> pWords = new HashMap<>();
        for (String word : words){
            int count = linksInCategory.stream()
                    .filter(link -> link.getURL().contains(word))
                    .collect(Collectors.toList()).size();

            double pWord = smooth((double) count / (double) linksInCategory.size());
            pWords.put(word, pWord);
        }

        double dividend = pInCategory * pWords.values().stream().reduce(1.0, (a,b) -> a*b);
        Map<String, Double> pNegated = pWords.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey(),
                        e -> 1 - e.getValue() == 0 ? 0.001 : 1 - e.getValue()
        ));
        double result = dividend / (dividend + pNegated.values().stream().reduce(1.0, (a,b) -> a*b));
        System.out.println(result);

    }

    public double smooth(double value){
        return value == 0 ? 0.001 : value;
    }

    public void calculateCombinedHardCoded(double pStackoverflow, double pBlog){
        List<Link> allLinks = this.linkReadDBHandler.getAllLinksWithCategory(GenericDBHandler.getInstance());
        List<Link> linksInStackoverflowBlog = allLinks.stream()
                .filter(link -> link.getCategory()!=null && "Stackoverflow blog".equals(link.getCategory().getName()))
                .collect(Collectors.toList());

        double pStackoverflowBlog = (double) linksInStackoverflowBlog.size() / (double) allLinks.size();

        List<Link> linksInStackOverflowBlogContainStackoverflow = linksInStackoverflowBlog.stream()
                .filter(link -> link.getURL().contains("stackoverflow"))
                .collect(Collectors.toList());
        List<Link> linksInStackOverflowBlogContainBlog = linksInStackoverflowBlog.stream()
                .filter(link -> link.getURL().contains("developer"))
                .collect(Collectors.toList());

        pStackoverflow = (double) linksInStackOverflowBlogContainStackoverflow.size() /(double) linksInStackoverflowBlog.size();
        pBlog = (double) linksInStackOverflowBlogContainBlog.size() /(double) linksInStackoverflowBlog.size();
        if (pBlog == 0){
            pBlog = 0.001;
        }
        double numerator = pStackoverflowBlog * pStackoverflow * pBlog;
        double pNotStackOverflowBlog = 1 - pStackoverflowBlog;
        double pNotStackOverflow = 1 - pStackoverflow == 0 ? 0.001 : 1 - pStackoverflow;
        double pNotBlog = 1 - pBlog == 0 ? 0.001 : 1 - pBlog;
        double denominator = pStackoverflowBlog * pStackoverflow * pBlog + pNotStackOverflowBlog * pNotStackOverflow * pNotBlog;

        double result = numerator/denominator;
        System.out.println(result);
    }
}
