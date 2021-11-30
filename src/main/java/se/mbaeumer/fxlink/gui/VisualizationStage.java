package se.mbaeumer.fxlink.gui;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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

import java.util.HashMap;
import java.util.Map;

public class VisualizationStage extends Stage {
    private Scene scene;
    private FlowPane flowGeneral;
    private FlowPane flowNumbers;
    private FlowPane flowBarChart;
    private Label lblLinks;
    private Label lblCategories;
    private BarChart<String,Number> bc;

    private LinkHandler linkHandler;
    private CategoryHandler categoryHandler;

    public VisualizationStage() {
        super();
        this.initRootPane();
        this.initScene();

        ChangeListener<Number> widthListener = (observable, old, newValue) -> {
            initSizes();
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
        this.initChartFlowPane();
        this.initBarChart();
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

    private void initChartFlowPane(){
        this.flowBarChart = new FlowPane();
        this.flowBarChart.setOrientation(Orientation.HORIZONTAL);
        this.flowBarChart.setHgap(5);
        this.flowBarChart.setVgap(10);
        this.flowBarChart.setPadding(new Insets(5, 10, 5, 10));
        this.flowBarChart.setEffect(this.createShadow());
        this.flowBarChart.setBackground(createBackground());
        this.flowGeneral.getChildren().add(this.flowBarChart);
    }

    private void initBarChart(){
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.bc = new BarChart<String,Number>(xAxis,yAxis);

        XYChart.Series series1 = new XYChart.Series();

        Map<String, Long> values = new HashMap<>();
        int i = 5;
        values.put("one", Long.valueOf(i));
        values.put("two", Long.valueOf(i));
        values.put("three", Long.valueOf(i));

        values = this.linkHandler.getCategoryCounts();

        values.entrySet().stream()
                .forEach(stringIntegerEntry -> series1.getData().add(new XYChart.Data(stringIntegerEntry.getKey(), stringIntegerEntry.getValue())));

        //values.entrySet()
        /*
        for (int i=0; i<csvDataRows.size(); i++){
            series1.getData().add(new XYChart.Data(getDateString(csvDataRows.get(i).getDateTime()), csvDataRows.get(i).getNumber()));
        }
        series1.setName("Number of new cases/day in " + cmbCountries.getSelectionModel().getSelectedItem());

         */
        bc.getData().addAll(series1);
        this.flowBarChart.getChildren().add(bc);
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
        this.flowBarChart.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.bc.setPrefWidth(this.flowBarChart.getWidth()-15);
    }
}
