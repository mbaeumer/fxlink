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
import se.mbaeumer.fxlink.api.LinkHandler;
import se.mbaeumer.fxlink.api.NaiveBayesClassifier;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Probability;
import se.mbaeumer.fxlink.util.LinkSplitter;
import se.mbaeumer.fxlink.util.StopWordHandler;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.*;

public class ClassifyStage extends Stage {
    private Scene scene;
    private FlowPane flowGeneral;
    private TableView<Link> tvLinks;
    private FlowPane flowSuggestions;
    private LinkHandler linkHandler;
    private NaiveBayesClassifier naiveBayesClassifier;
    private List<Link> allLinks;

    public ClassifyStage() {
        super();
        this.initRootPane();
        this.initScene();
        ChangeListener<Number> widthListener = (observable, old, newValue) -> {
            this.tvLinks.setPrefWidth(this.flowGeneral.getWidth()-15);
            this.flowSuggestions.setPrefWidth(this.flowGeneral.getWidth()-15);
            this.setColumnWidths();
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

        this.setTitle("Classification demo");
        this.setScene(this.scene);

        this.initHandlers();
        this.initData();
        this.initTableView();
        this.createTableViewColumns();
        this.initEventHandler();
        this.setColumnWidths();
        this.tvLinks.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.initSuggestionFlowPane();
    }

    private void initHandlers(){
        this.linkHandler = new LinkHandler(new LinkReadDBHandler(), new LinkTagReadDBHandler(),
                new LinkCreationDBHandler(), new LinkUpdateDBHandler(), new LinkDeletionDBHandler(), new FollowUpStatusReadDBHandler());
        this.naiveBayesClassifier = new NaiveBayesClassifier(new LinkSplitter(new URLHelper()), new LinkReadDBHandler(),
                this.linkHandler, new StopWordHandler());
    }

    private void initData(){
        this.allLinks = this.linkHandler.getLinks();
    }

    public void initTableView(){
        this.tvLinks = new TableView<>();
        this.tvLinks.setItems(FXCollections.observableList(this.allLinks));
        this.flowGeneral.getChildren().add(this.tvLinks);
    }

    public void createTableViewColumns(){
        TableColumn urlCol = new TableColumn<>("Url");
        urlCol.setCellValueFactory(new PropertyValueFactory("url"));
        urlCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn titleCol = new TableColumn("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory("title"));
        titleCol.setCellFactory(TextFieldTableCell.forTableColumn());

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

        this.tvLinks.getColumns().addAll(urlCol, titleCol, categoryCol);
    }

    public void initEventHandler(){
        this.tvLinks.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent me) {
                        Link link = tvLinks.getSelectionModel().getSelectedItem();
                        if (link != null) {
                            List<Probability> probabilities = naiveBayesClassifier.classify(link);
                            showSuggestions(probabilities);
                        }
                    }
                });
    }

    private void setColumnWidths(){
        this.tvLinks.getColumns().get(0).setPrefWidth((this.tvLinks.getPrefWidth()*35)/100);
        this.tvLinks.getColumns().get(1).setPrefWidth((this.tvLinks.getPrefWidth()*35)/100);
        this.tvLinks.getColumns().get(2).setPrefWidth((this.tvLinks.getPrefWidth()*30)/100);
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
}
