package se.mbaeumer.fxlink.gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import se.mbaeumer.fxlink.api.CategoryHandler;
import se.mbaeumer.fxlink.api.LinkHandler;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Probability;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.*;
import java.util.stream.Collectors;

public class ClassifyStage extends Stage {
    private Scene scene;
    private FlowPane flowGeneral;
    private TableView<Link> tvLinks;
    private FlowPane flowSuggestions;
    private LinkHandler linkHandler;
    private LinkReadDBHandler linkReadDBHandler;
    private CategoryHandler categoryHandler;
    private URLHelper urlHelper;
    private List<Link> allLinks;
    private List<Link> allLinksWithCategories;
    private List<Category> categories;

    public ClassifyStage() {
        super();
        this.initRootPane();
        this.initScene();
        ChangeListener<Number> widthListener = (observable, old, newValue) -> {
            this.tvLinks.setPrefWidth(this.flowGeneral.getWidth()-15);
            this.flowSuggestions.setPrefWidth(this.flowGeneral.getWidth()-15);
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

        this.initHandlers();
        this.initData();
        this.initTableView();
        this.createTableViewColumns();
        this.initEventHandler();
        this.tvLinks.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.initSuggestionFlowPane();
    }

    private void initHandlers(){
        this.linkHandler = new LinkHandler(new LinkReadDBHandler(), new LinkTagReadDBHandler(),
                new LinkCreationDBHandler(), new LinkUpdateDBHandler(), new LinkDeletionDBHandler());
        this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(), new CategoryCreationDBHandler(),
                new CategoryUpdateDBHandler(), new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());
        this.linkReadDBHandler = new LinkReadDBHandler();
        this.urlHelper = new URLHelper();
    }

    private void initData(){
        this.allLinks = this.linkHandler.getLinks();
        this.allLinksWithCategories = this.linkReadDBHandler.getAllLinksWithCategory(GenericDBHandler.getInstance());
        this.categories = this.categoryHandler.getCategories();
    }

    public void initTableView(){
        this.tvLinks = new TableView();
        this.tvLinks.setItems(FXCollections.observableList(this.allLinks));
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

    public void initEventHandler(){
        this.tvLinks.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent me) {
                        Link link = tvLinks.getSelectionModel().getSelectedItem();
                        List<Probability> probabilities = classify(link);
                        showSuggestions(probabilities);
                    }
                });
    }

    private void initSuggestionFlowPane(){
        this.flowSuggestions = new FlowPane(Orientation.HORIZONTAL);
        this.flowSuggestions.setHgap(5);
        this.flowGeneral.getChildren().add(this.flowSuggestions);
    }

    private void showSuggestions(List<Probability> probabilities){
        this.flowSuggestions.getChildren().clear();
        for (Probability probability : probabilities){
            Button button = new Button(probability.toString());
            flowSuggestions.getChildren().add(button);
        }
    }

    public List<Probability> classify(final Link link){
        List<String> words = splitUrl(link);
        List<Probability> probabilities = new ArrayList<>();
        for (Category category : categories){
            probabilities.add(calculateCombinedGeneric(category.getName(), words));
        }

        List<Probability> sorted = probabilities.stream()
                .filter(probability -> !Double.isNaN(probability.getProbability()))
                .sorted(Comparator.comparing(Probability::getProbability).reversed())
                //.filter(p -> p.getProbability() > 0.05)
                .collect(Collectors.toList());
                //.stream()
                //.sorted(Comparator.comparing(Probability::getProbability).reversed())
                //.collect(Collectors.toList());
        return sorted;
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

    private double calculateSingle(final String categoryName, final String word){
        return 0.0;

        //How many links are assigned to “stackoverflow blog” → P(A)
        //How many links contain “stackoverflow”? → P(B)
        //How many links assigned to “stackoverflow blog” contain the word “stackoverflow” → P(B|A)
    }

    public Probability calculateCombinedGeneric(final String categoryName, final List<String> words){
        List<Link> linksInCategory = allLinks.stream()
                .filter(link -> link.getCategory()!=null && categoryName.equals(link.getCategory().getName()))
                .collect(Collectors.toList());

        double pInCategory = (double) linksInCategory.size() / (double) allLinksWithCategories.size();

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
                        e -> smooth(1 - e.getValue())
        ));
        double part2 = pNegated.values().stream().reduce(1.0, (a,b) -> a*b);
        double denominator = (dividend + part2);
        double result = dividend / denominator;

        return new Probability(categoryName, result*100.00);
    }

    public double smooth(double value){
        return value == 0 ? 0.0001 : value;
    }
}
