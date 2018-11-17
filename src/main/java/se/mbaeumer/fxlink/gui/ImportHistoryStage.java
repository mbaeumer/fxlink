package se.mbaeumer.fxlink.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import se.mbaeumer.fxlink.handlers.HsqldbConnectionHandler;
import se.mbaeumer.fxlink.handlers.ImportItemReadDBHandler;
import se.mbaeumer.fxlink.models.Link;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ImportHistoryStage extends Stage {
    private Scene scene;
    private FlowPane flowGeneral;
    private TableView tvImportItems;
    private Button btnClose;

    public ImportHistoryStage() throws SQLException {
        super();
        this.initRootPane();
        this.initScene();
    }

    private void initRootPane(){
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

        this.initTableView();
        this.initCloseButton();
    }

    private void makeModal(){
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);
    }

    private void initTableView() throws SQLException {
        this.tvImportItems = new TableView();

        ImportItemReadDBHandler importItemReadDBHandler = new ImportItemReadDBHandler();
        String sql = "select * from ImportItem";
        this.tvImportItems.setItems(FXCollections.observableList(importItemReadDBHandler.getAllImportItems(sql, new HsqldbConnectionHandler())));
        this.createTableViewColumns();

        this.flowGeneral.getChildren().add(this.tvImportItems);
        this.tvImportItems.setPrefWidth(480);
        ((TableColumn)this.tvImportItems.getColumns().get(0)).setPrefWidth(this.tvImportItems.getPrefWidth() * 75/100);
        ((TableColumn)this.tvImportItems.getColumns().get(1)).setPrefWidth(this.tvImportItems.getPrefWidth() * 25/100);
    }

    private void createTableViewColumns(){
        TableColumn filenameCol = new TableColumn("Filename");
        filenameCol.setCellValueFactory(new PropertyValueFactory("filename"));
        filenameCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Link, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Link, String> link) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        property.setValue(dateFormat.format(link.getValue().getCreated()));
                        return property;
                    }
                });

        TableColumn createdCol = new TableColumn("Created");
        createdCol.setCellValueFactory(new PropertyValueFactory("created"));
        createdCol.setCellValueFactory(
                new Callback<TableColumn.CellDataFeatures<Link, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<Link, String> link) {
                        SimpleStringProperty property = new SimpleStringProperty();
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        property.setValue(dateFormat.format(link.getValue().getCreated()));
                        return property;
                    }
                });

        this.tvImportItems.getColumns().addAll(filenameCol, createdCol);
    }

    private void initCloseButton(){
        this.btnClose = new Button("Close");
        this.btnClose.setOnAction((event) -> {close();});
        this.flowGeneral.getChildren().add(this.btnClose);
    }
}
