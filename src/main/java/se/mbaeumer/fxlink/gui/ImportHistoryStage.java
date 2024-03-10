package se.mbaeumer.fxlink.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import se.mbaeumer.fxlink.api.ImportItemHandler;
import se.mbaeumer.fxlink.models.ImportItem;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.stream.Collectors;

public class ImportHistoryStage extends Stage {
    private Scene scene;
    private FlowPane flowGeneral;

    private FlowPane flowSearch;

    private Label lblSearchText;

    private TextField tfSearch;

    private TableView<ImportItem> tvImportItems;
    private Button btnClose;
    private Button btnClearHistory;

    private ImportItemHandler importItemHandler;

    public ImportHistoryStage() throws SQLException {
        super();
        this.importItemHandler = new ImportItemHandler();
        this.initRootPane();
        this.initScene();
    }

    private void initRootPane() {
        this.flowGeneral = new FlowPane();
        this.flowGeneral.setOrientation(Orientation.HORIZONTAL);
        this.flowGeneral.setHgap(10);
        this.flowGeneral.setVgap(10);
        this.flowGeneral.setPadding(new Insets(5, 10, 5, 10));
    }

    private void initScene() throws SQLException {
        int width = 500;
        int height = 450;
        this.scene = new Scene(this.flowGeneral, width, height);
        this.scene.setFill(Color.WHITESMOKE);

        this.makeModal();

        this.setTitle("Import history");
        this.setScene(this.scene);

        this.initSearch();
        this.initTableView();
        this.initCloseButton();
        this.initHistoryButton();
    }

    private void initSearch() {
        this.flowSearch = new FlowPane();
        this.flowSearch.setOrientation(Orientation.HORIZONTAL);
        this.flowSearch.setHgap(10);
        this.flowSearch.setVgap(10);
        this.flowSearch.setPadding(new Insets(5, 10, 5, 10));
        this.flowGeneral.getChildren().add(this.flowSearch);

        this.lblSearchText = new Label("Search");
        this.flowSearch.getChildren().add(this.lblSearchText);

        this.tfSearch = new TextField();
        this.tfSearch.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    if (newValue.length() > 0) {
                        tvImportItems
                                .setItems(FXCollections
                                        .observableList(importItemHandler.readImportItems()
                                                .stream()
                                                .filter(item -> item.getFilename().contains(newValue.toLowerCase()))
                                                .collect(Collectors.toList())));

                    } else {
                        tvImportItems.setItems(FXCollections.observableList(importItemHandler.readImportItems()));
                    }
                } catch (SQLException e) {

                }
            }
        });
        this.flowSearch.getChildren().add(this.tfSearch);
    }

    private void makeModal() {
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);
    }

    private void initTableView() throws SQLException {
        this.tvImportItems = new TableView<>();

        this.tvImportItems.setItems(FXCollections.observableList(this.importItemHandler.readImportItems()));
        this.createTableViewColumns();

        this.flowGeneral.getChildren().add(this.tvImportItems);
        this.tvImportItems.setPrefWidth(480);
        this.tvImportItems.getColumns().get(0).setPrefWidth(this.tvImportItems.getPrefWidth() * 75 / 100);
        this.tvImportItems.getColumns().get(1).setPrefWidth(this.tvImportItems.getPrefWidth() * 25 / 100);
    }

    private void createTableViewColumns() {
        TableColumn<ImportItem, String> filenameCol = new TableColumn<>("Filename");
        filenameCol.setCellValueFactory(new PropertyValueFactory<>("filename"));

        TableColumn<ImportItem, String> createdCol = new TableColumn<>("Created");
        createdCol.setCellValueFactory(new PropertyValueFactory<>("created"));
        createdCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<ImportItem, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ImportItem, String> link) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        property.setValue(dateFormat.format(link.getValue().getCreated()));
                        return property;
                    }
                });

        this.tvImportItems.getColumns().addAll(filenameCol, createdCol);
    }

    private void initCloseButton() {
        this.btnClose = new Button("Close");
        this.btnClose.setOnAction((event) -> {
            close();
        });
        this.flowGeneral.getChildren().add(this.btnClose);
    }

    private void initHistoryButton() {
        this.btnClearHistory = new Button("Clear history");
        this.btnClearHistory.setOnAction(this::handleClick);
        this.flowGeneral.getChildren().add(this.btnClearHistory);
    }

    private void handleClick(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.WARNING, "The history will be deleted. Continue?", ButtonType.YES, ButtonType.NO);
        alert.initOwner(this);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            ImportItemHandler importItemHandler = new ImportItemHandler();
            try {
                importItemHandler.deleteAllImportItems();
                tvImportItems.setItems(FXCollections.observableList(importItemHandler.readImportItems()));
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } else {
            actionEvent.consume();
        }
    }
}
