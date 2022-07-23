package se.mbaeumer.fxlink.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import se.mbaeumer.fxlink.api.CategoryHandler;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;

import java.sql.SQLException;

/**
 * Created by martinbaumer on 23/03/16.
 */
public class MoveCategoryStage extends Stage{
    private final Group root = new Group();
    private Scene scene;
    private FlowPane flowGeneral;
    private GridPane gridData;
    private Label lblSource;
    private ComboBox<Category> cmbSourceCategories;
    private Label lblTarget;
    private ComboBox<Category> cmbTargetCategories;
    private final Category sourceCategory;
    private Button btnMove;
    private final CategoryHandler categoryHandler;

    public MoveCategoryStage(Category sourceCategory){
        super();
        this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(),
                new CategoryCreationDBHandler(), new CategoryUpdateDBHandler(),
                new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());
        this.sourceCategory = sourceCategory;
        this.initLayout();
        this.makeModal();
    }

    private void initLayout(){
        this.initScene();
        this.initRootPane();
        this.initGridPane();
        this.initSourceLabel();
        this.initSourceCategories();
        this.initTargetLabel();
        this.initTargetCategories();
        this.initMoveButton();
    }

    private void initScene(){
        int width = 350;
        int height = 150;
        this.scene = new Scene(this.root, width, height, Color.WHITESMOKE);
        this.setTitle("Move category");
        this.setScene(this.scene);
    }

    private void initRootPane(){
        this.flowGeneral = new FlowPane();
        this.flowGeneral.setOrientation(Orientation.HORIZONTAL);
        this.flowGeneral.setHgap(10);
        this.flowGeneral.setVgap(10);
        this.flowGeneral.setPadding(new Insets(5, 10, 5, 10));
        this.root.getChildren().add(this.flowGeneral);
    }

	private void makeModal(){
		this.initModality(Modality.APPLICATION_MODAL);
		this.initStyle(StageStyle.UTILITY);
		this.setResizable(false);
	}

    private void initGridPane(){
        this.gridData = new GridPane();
        gridData.setHgap(10);
        gridData.setVgap(10);
        this.flowGeneral.getChildren().add(this.gridData);
    }

    private void initSourceLabel(){
        this.lblSource = new Label("Move from category");
        this.gridData.add(this.lblSource, 0, 0);
    }

    private void initSourceCategories(){
        this.cmbSourceCategories = new ComboBox<Category>();
        // get the categories
        ObservableList<Category> categoryList =
                FXCollections.observableArrayList(categoryHandler.getCategories());
        this.cmbSourceCategories.setItems(categoryList);

        this.cmbSourceCategories.setCellFactory(new Callback<ListView<Category>,ListCell<Category>>(){
            @Override
            public ListCell<Category> call(ListView<Category> p) {
                final ListCell<Category> cell = new ListCell<Category>(){
                    @Override
                    protected void updateItem(Category t, boolean bln) {
                        super.updateItem(t, bln);
                        if(t != null){
                            setText(t.getName());
                        }else{
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });

        this.cmbSourceCategories.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category t, boolean bln) {
                super.updateItem(t, bln);
                if (t != null) {
                    setText(t.nameProperty().getValue());
                } else {
                    setText(null);
                }
            }
        });

        int index = 0;
        for (Category c : categoryList){
            if (c.getName().equalsIgnoreCase(this.sourceCategory.getName())){
                break;
            }
            index++;
        }
        this.cmbSourceCategories.getSelectionModel().select(index);
        this.gridData.add(this.cmbSourceCategories, 1, 0);
    }

    private void initTargetLabel(){
        this.lblTarget = new Label("...to category");
        this.gridData.add(this.lblTarget, 0, 1);
    }

    private void initTargetCategories(){
        this.cmbTargetCategories = new ComboBox<Category>();
        ObservableList<Category> categoryList =
                FXCollections.observableArrayList(categoryHandler.getCategories());
        this.cmbTargetCategories.setItems(categoryList);

        this.cmbTargetCategories.setCellFactory(new Callback<ListView<Category>,ListCell<Category>>(){
            @Override
            public ListCell<Category> call(ListView<Category> p) {
                final ListCell<Category> cell = new ListCell<Category>(){
                    @Override
                    protected void updateItem(Category t, boolean bln) {
                        super.updateItem(t, bln);
                        if(t != null){
                            setText(t.getName());
                        }else{
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });

        this.cmbTargetCategories.setButtonCell(new ListCell<Category>() {
            @Override
            protected void updateItem(Category t, boolean bln) {
                super.updateItem(t, bln);
                if (t != null) {
                    setText(t.nameProperty().getValue());
                } else {
                    setText(null);
                }
            }
        });

        this.cmbTargetCategories.getSelectionModel().select(0);
        this.gridData.add(this.cmbTargetCategories, 1, 1);
    }

    private void initMoveButton(){
        this.btnMove = new Button("Move");

        this.btnMove.setOnAction(actionEvent -> {
            try {
                if (isValidMove(cmbSourceCategories.getValue(), cmbTargetCategories.getValue())) {
                    categoryHandler.moveCategory(cmbSourceCategories.getValue(), cmbTargetCategories.getValue());
                }else{
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The selected categories must not be the same!", ButtonType.OK);
                    alert.showAndWait();
                }
            }catch(SQLException ex){
                Alert alert = new Alert(Alert.AlertType.ERROR, "Database error: Could not move links!", ButtonType.OK);
                alert.showAndWait();
            }
        });

        this.gridData.setAlignment(Pos.CENTER);
        this.gridData.add(this.btnMove, 0, 2);
        GridPane.setColumnSpan(this.btnMove, 2);
    }

    private boolean isValidMove(Category source, Category target){
        return !source.getName().equalsIgnoreCase(target.getName());
    }
}
