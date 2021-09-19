package se.mbaeumer.fxlink.gui;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import se.mbaeumer.fxlink.api.CategoryHandler;
import se.mbaeumer.fxlink.handlers.CategoryCreationDBHandler;
import se.mbaeumer.fxlink.handlers.CategoryDeletionDBHandler;
import se.mbaeumer.fxlink.handlers.CategoryReadDBHandler;
import se.mbaeumer.fxlink.handlers.CategoryUpdateDBHandler;
import se.mbaeumer.fxlink.models.Category;

import java.sql.SQLException;

import static se.mbaeumer.fxlink.gui.FXLink.DATABASE_ERROR_OCCURRED;

public class CreateCategoryStage extends Stage {

    private Scene scene;
    private FlowPane flowGeneral;
    private GridPane gridPane;

    private Label lblNameText;
    private TextField tfCategoryName;
    private Label lblDescriptionText;
    private TextArea taCategoryDescription;
    
    private Label lblStatus;
    private FlowPane flowCommand;
    private Button btnCreate;
    private Button btnCancel;

    private CategoryHandler categoryHandler;

    public CreateCategoryStage() {
        super();

        this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(),
                new CategoryCreationDBHandler(), new CategoryUpdateDBHandler(),
                new CategoryDeletionDBHandler());
        this.initRootPane();
        this.initScene();

        this.initGridPane();
        this.initRowForName();
        this.initRowForDescription();

        this.initCommandPane();
        this.initCreateButton();
        this.initCancelButton();
        this.initStatusLabel();
    }

    private void initRootPane(){
        this.flowGeneral = new FlowPane();
        this.flowGeneral.setOrientation(Orientation.HORIZONTAL);
        this.flowGeneral.setHgap(10);
        this.flowGeneral.setVgap(10);
        this.flowGeneral.setPadding(new Insets(5, 10, 5, 10));
    }

    private void initScene(){
        int width = 350;
        int height = 150;
        this.scene = new Scene(this.flowGeneral, width, height);
        this.scene.setFill(Color.WHITESMOKE);

        this.makeModal();

        this.setTitle("Create category");
        this.setScene(this.scene);
    }

    private void makeModal(){
        this.initModality(Modality.APPLICATION_MODAL);
        this.initStyle(StageStyle.UTILITY);
        this.setResizable(false);
    }

    private void initGridPane(){
        this.gridPane = new GridPane();
        this.flowGeneral.getChildren().add(gridPane);

    }

    private void initRowForName(){
        this.lblNameText = new Label("Name:");
        this.gridPane.add(lblNameText, 0, 0);

        this.tfCategoryName = new TextField();
        this.tfCategoryName.textProperty().addListener(this::changed);

        this.gridPane.add(tfCategoryName, 1, 0);
    }

    private void initRowForDescription(){
        this.lblDescriptionText = new Label("Description:");
        this.gridPane.add(lblDescriptionText, 0, 1);

        this.taCategoryDescription = new TextArea();
        this.taCategoryDescription.setPrefRowCount(3);
        this.taCategoryDescription.setPrefColumnCount(1);
        this.gridPane.add(taCategoryDescription, 1, 1);
    }
    
    private void initStatusLabel(){
        this.lblStatus = new Label("");
        this.flowCommand.getChildren().add(lblStatus);
    }
    
    private void initCommandPane(){
        this.flowCommand = new FlowPane();
        this.flowCommand.setOrientation(Orientation.HORIZONTAL);
        this.flowCommand.setHgap(5);
        this.flowGeneral.getChildren().add(this.flowCommand);
    }

    private void initCreateButton(){
        this.btnCreate = new Button("Create");
        this.btnCreate.setDisable(true);
        this.btnCreate.setOnAction(this::handleSaveCategory);
        this.flowCommand.getChildren().add(this.btnCreate);
    }

    private void initCancelButton(){
        this.btnCancel = new Button("Cancel");
        this.btnCancel.setOnAction(this::handleClose);
        this.flowCommand.getChildren().add(this.btnCancel);
    }

    private void handleSaveCategory(ActionEvent event) {
        Category category = new Category();
        category.setName(tfCategoryName.getText());
        category.setDescription(taCategoryDescription.getText());

        try {
            categoryHandler.createCategory(category);
            resetForm();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, DATABASE_ERROR_OCCURRED, ButtonType.OK);
            alert.showAndWait();
        }
    }

    private void resetForm(){
        this.tfCategoryName.setText("");
        this.taCategoryDescription.setText("");
    }

    private void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        btnCreate.setDisable(false);
        if (newValue.length() > 0) {
            try {
                Category category = categoryHandler.getCategoryByName(newValue);
                if (category != null) {
                    lblStatus.setText("This category already exists");
                    btnCreate.setDisable(true);
                } else {
                    lblStatus.setText("");
                }
            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, DATABASE_ERROR_OCCURRED, ButtonType.OK);
                alert.showAndWait();
            }
        } else {
            lblStatus.setText("");
        }
    }

    private void handleClose(ActionEvent event) {
        close();
    }
}
