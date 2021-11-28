package se.mbaeumer.fxlink.gui;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import se.mbaeumer.fxlink.api.CategoryHandler;
import se.mbaeumer.fxlink.api.LinkHandler;
import se.mbaeumer.fxlink.handlers.*;

public class VisualizationStage extends Stage {
    private Scene scene;
    private FlowPane flowGeneral;
    private FlowPane flowNumbers;
    private Label lblLinks;
    private Label lblCategories;

    private LinkHandler linkHandler;
    private CategoryHandler categoryHandler;

    public VisualizationStage() {
        super();
        this.initRootPane();
        this.initScene();

        ChangeListener<Number> widthListener = (observable, old, newValue) -> {
            this.flowNumbers.setPrefWidth(this.flowGeneral.getWidth()-15);
            this.lblLinks.setPrefWidth((this.flowNumbers.getWidth()-20.0)/2.0);
            this.lblCategories.setPrefWidth((this.flowNumbers.getWidth()-20.0)/2.0);
            //this.setColumnWidths();
        };
        this.widthProperty().addListener(widthListener);

    }

    private void initRootPane(){
        this.flowGeneral = new FlowPane();
        this.flowGeneral.setOrientation(Orientation.HORIZONTAL);
        this.flowGeneral.setHgap(10);
        this.flowGeneral.setVgap(10);
        this.flowGeneral.setPadding(new Insets(5, 10, 5, 10));
        this.flowGeneral.setEffect(this.createShadow());
    }

    private void initScene() {
        int width = 700;
        int height = 450;
        this.scene = new Scene(this.flowGeneral, width, height);
        this.scene.setFill(Color.WHITESMOKE);

        this.setTitle("Import history");
        this.setScene(this.scene);

        this.initHandlers();
        this.initNumberFlowPane();
        this.initLinkLabel();
        this.initCategoryLabel();
        this.initSizes();
        /*
        this.initData();
        this.initTableView();
        this.createTableViewColumns();
        this.initEventHandler();
        this.setColumnWidths();
        this.tvLinks.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.initSuggestionFlowPane();
         */
    }

    private void initHandlers(){
        this.linkHandler = new LinkHandler(new LinkReadDBHandler(), new LinkTagReadDBHandler(),
                new LinkCreationDBHandler(), new LinkUpdateDBHandler(), new LinkDeletionDBHandler());
        this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(), new CategoryCreationDBHandler(),
                new CategoryUpdateDBHandler(), new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());
    }

    private void initNumberFlowPane(){
        this.flowNumbers = new FlowPane();
        this.flowNumbers.setOrientation(Orientation.HORIZONTAL);
        this.flowNumbers.setHgap(5);
        this.flowNumbers.setVgap(10);
        this.flowNumbers.setPadding(new Insets(5, 10, 5, 10));
        //this.flowNumbers.setEffect(this.createShadow());
        //this.flowNumbers.setBackground(createBackground());
        this.flowGeneral.getChildren().add(this.flowNumbers);
    }

    private void initLinkLabel(){
        this.lblLinks = new Label();
        this.lblLinks.setText("Number of links: " + String.valueOf(this.linkHandler.getLinks().size()));
        this.lblLinks.setFont(Font.font("Cambria", 32));
        this.lblLinks.setBackground(createBackground());
        this.lblLinks.setEffect(this.createShadow());
        this.lblLinks.setPadding(new Insets(0, 0, 0, 5));
        this.flowNumbers.getChildren().add(this.lblLinks);
    }

    private void initCategoryLabel(){
        this.lblCategories = new Label();
        this.lblCategories.setText("Number of categories: " + String.valueOf(this.categoryHandler.getCategories().size()));
        this.lblCategories.setFont(Font.font("Cambria", 32));
        this.lblCategories.setBackground(createBackground());
        this.lblCategories.setEffect(createShadow());
        this.lblCategories.setPadding(new Insets(0, 0, 0, 5));
        this.flowNumbers.getChildren().add(this.lblCategories);
    }

    private DropShadow createShadow(){
        return new DropShadow(5, Color.GRAY);
    }

    private Background createBackground(){
        Insets bgInsets = new Insets(5);
        BackgroundFill bgFill = new BackgroundFill(Color.WHITE, null, bgInsets);
        return new Background(bgFill);
    }

    private void initSizes(){
        this.flowNumbers.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.lblLinks.setPrefWidth((this.flowNumbers.getWidth()-20.0)/2.0);
        this.lblCategories.setPrefWidth((this.flowNumbers.getWidth()-20.0)/2.0);
    }
}
