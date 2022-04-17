package se.mbaeumer.fxlink.gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import se.mbaeumer.fxlink.api.CategoryHandler;
import se.mbaeumer.fxlink.api.CsvExportHandler;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.SelectableCategory;
import se.mbaeumer.fxlink.util.StopWordHandler;
import se.mbaeumer.fxlink.util.URLHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CsvExportStage extends Stage {
    private Scene scene;
    private FlowPane flowGeneral;
    private ListView listView;
    private FlowPane flowActions;
    private Button btnExport;
    private Button btnClose;
    private CategoryHandler categoryHandler;
    private List<SelectableCategory> selectableCategories;

    public CsvExportStage() {
        super();
        this.initRootPane();
        this.initScene();
        /*
        ChangeListener<Number> widthListener = (observable, old, newValue) -> {
            this.tvLinks.setPrefWidth(this.flowGeneral.getWidth()-15);
            this.flowSuggestions.setPrefWidth(this.flowGeneral.getWidth()-15);
            this.setColumnWidths();
        };
        this.widthProperty().addListener(widthListener);
         */
    }

    private void initRootPane(){
        this.flowGeneral = new FlowPane();
        this.flowGeneral.setOrientation(Orientation.VERTICAL);
        this.flowGeneral.setHgap(10);
        this.flowGeneral.setVgap(5);
        this.flowGeneral.setPadding(new Insets(5, 10, 5, 10));
    }

    private void initScene() {
        int width = 450;
        int height = 450;
        this.scene = new Scene(this.flowGeneral, width, height);
        this.scene.setFill(Color.WHITESMOKE);

        this.setTitle("CSV Export");
        this.setScene(this.scene);

        this.initHandlers();
        this.initData();
        this.initListView();
        this.initActionPane();
        this.initExportButton();
        this.initCloseButton();
    }

    private void initHandlers(){
        this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(), new CategoryCreationDBHandler(),
                new CategoryUpdateDBHandler(), new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());
    }

    private void initListView(){
        this.listView = new ListView<SelectableCategory>();
        this.listView.setCellFactory(CheckBoxListCell.forListView(SelectableCategory::selectedProperty, new StringConverter<SelectableCategory>() {

                    @Override
                    public SelectableCategory fromString(String arg0) {
                        return null;
                    }

                    @Override
                    public String toString(SelectableCategory arg0) {
                        return arg0.getName();
                    }}
                )
        );
        this.listView.setItems(FXCollections.observableList(this.selectableCategories));
        this.flowGeneral.getChildren().add(this.listView);
    }

    private void initData(){
        List<Category> categories = categoryHandler.getCategories();
        this.selectableCategories = categories.stream()
                .map(category -> mapToSelectable(category)).collect(Collectors.toList());
    }

    private static SelectableCategory mapToSelectable(Category category){
        SelectableCategory selectableCategory = new SelectableCategory();
        selectableCategory.setSelected(false);
        selectableCategory.setId(category.getId());
        selectableCategory.setName(category.getName());

        return selectableCategory;
    }

    private void initActionPane(){
        this.flowActions = new FlowPane();
        this.flowActions.setOrientation(Orientation.HORIZONTAL);
        this.flowActions.setHgap(5);
        this.flowActions.setVgap(5);
        this.flowActions.setPadding(new Insets(0, 0, 5, 0));
        this.flowGeneral.getChildren().add(this.flowActions);
    }

    private void initExportButton(){
        this.btnExport = new Button("Export");
        this.btnExport.setOnAction(this::handleExport);
        this.flowActions.getChildren().add(this.btnExport);
    }

    private void handleExport(ActionEvent actionEvent){
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File importFile = fc.showSaveDialog(null);

        if (importFile == null){
            actionEvent.consume();
            return;
        }

        try {
            String path = importFile.getCanonicalPath();
            if (!path.endsWith(".csv")){
                path += ".csv";
            }

            List<SelectableCategory> selectableCategories = new ArrayList<>(listView.getItems());
            List<Category> categories = selectableCategories
                    .stream()
                    .filter(selectableCategory -> selectableCategory.isSelected())
                    .map(selectableCategory -> mapToCategory(selectableCategory))
                    .collect(Collectors.toList());
            CsvExportHandler csvExportHandler =
                    new CsvExportHandler(new LinkReadDBHandler(), new URLHelper(), new StopWordHandler());
            csvExportHandler.getData(path, categories);

        }catch (IOException e ) {
            e.printStackTrace();
        }

    }

    private Category mapToCategory(SelectableCategory selectableCategory){
        Category category = new Category();
        category.setId(selectableCategory.getId());
        category.setName(selectableCategory.getName());

        return category;
    }

    private void initCloseButton(){
        this.btnClose = new Button("Close");
        this.btnClose.setOnAction(actionEvent -> close());
        this.flowActions.getChildren().add(this.btnClose);
    }
}
