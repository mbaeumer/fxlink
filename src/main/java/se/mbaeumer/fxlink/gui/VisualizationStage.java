package se.mbaeumer.fxlink.gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VisualizationStage extends Stage {
    private Scene scene;
    private ScrollPane scrollPane;
    private FlowPane flowGeneral;
    private FlowPane flowNumbers;
    private FlowPane flowBarChart;
    private ComboBox<String> cmbMinCategoryCount;
    private Label lblLinks;
    private Label lblCategories;
    private BarChart<String,Number> bcCategory;

    private FlowPane flowPieChart;
    private BarChart<Object, Long> bcHour;

    private LinkHandler linkHandler;
    private CategoryHandler categoryHandler;
    private Map<Integer, String> weekdays = Map.ofEntries(
            Map.entry(1, "Sunday"),
            Map.entry(2, "Monday"),
            Map.entry(3, "Tuesday"),
            Map.entry(4, "Wednesday"),
            Map.entry(5, "Thursday"),
            Map.entry(6, "Friday"),
            Map.entry(7, "Saturday")
            );

    public VisualizationStage() {
        super();

        this.initRootPane();
        this.initScrollPane();
        this.initScene();

        ChangeListener<Number> widthListener = (observable, old, newValue) -> {
            initSizes();
        };
        this.widthProperty().addListener(widthListener);
        this.heightProperty().addListener(widthListener);

    }

    private void initScrollPane(){
        this.scrollPane = new ScrollPane(this.flowGeneral);
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
        this.scene = new Scene(this.scrollPane, width, height);
        this.scene.setFill(Color.WHITESMOKE);

        this.setTitle("Data overview");
        this.setScene(this.scene);

        this.initHandlers();
        this.initNumberFlowPane();
        this.initLinkLabel();
        this.initCategoryLabel();
        this.initBarChartFlowPane();
        this.initComboBox();
        this.initCategoryBarChart(0);
        this.initPieChartFlowPane();
        this.initWeekdayPieChart();
        this.initHourBarChart();
        this.initSizes();
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
        this.flowGeneral.getChildren().add(this.flowNumbers);
    }

    private void initLinkLabel(){
        this.lblLinks = new Label();
        this.lblLinks.setText("Number of links: " + this.linkHandler.getLinks().size());
        this.lblLinks.setFont(Font.font("Cambria", 32));
        this.lblLinks.setBackground(createBackground());
        this.lblLinks.setEffect(this.createShadow());
        this.lblLinks.setPadding(new Insets(0, 0, 0, 5));
        this.flowNumbers.getChildren().add(this.lblLinks);
    }

    private void initCategoryLabel(){
        this.lblCategories = new Label();
        this.lblCategories.setText("Number of categories: " + this.categoryHandler.getCategories().size());
        this.lblCategories.setFont(Font.font("Cambria", 32));
        this.lblCategories.setBackground(createBackground());
        this.lblCategories.setEffect(createShadow());
        this.lblCategories.setPadding(new Insets(0, 0, 0, 5));
        this.flowNumbers.getChildren().add(this.lblCategories);
    }

    private void initBarChartFlowPane(){
        this.flowBarChart = new FlowPane();
        this.flowBarChart.setOrientation(Orientation.HORIZONTAL);
        this.flowBarChart.setHgap(5);
        this.flowBarChart.setVgap(10);
        this.flowBarChart.setPadding(new Insets(10, 10, 5, 10));
        this.flowBarChart.setEffect(this.createShadow());
        this.flowBarChart.setBackground(createBackground());
        this.flowGeneral.getChildren().add(this.flowBarChart);
    }

    private void initComboBox(){
        this.cmbMinCategoryCount = new ComboBox<>();

        List<String> alternatives = List.of("All", ">5", ">20", ">50", ">75", ">100");
        this.cmbMinCategoryCount.setItems(FXCollections.observableList(alternatives));

        this.cmbMinCategoryCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleComboBoxChange(newValue);
        });
        this.cmbMinCategoryCount.getSelectionModel().selectFirst();

        this.flowBarChart.getChildren().add(this.cmbMinCategoryCount);
    }

    private void handleComboBoxChange(final String value){
        int min = 0;
        if (">5".equals(value)){
            min = 5;
        }else if (">20".equals(value)){
            min = 20;
        }else if (">50".equals(value)){
            min = 50;
        }else if (">75".equals(value)){
            min = 75;
        }else if (">100".equals(value)){
            min = 100;
        }

        this.initCategoryBarChart(min);
        this.initSizes();
    }

    private void initPieChartFlowPane(){
        this.flowPieChart = new FlowPane();
        this.flowPieChart.setOrientation(Orientation.HORIZONTAL);

        this.flowPieChart.setHgap(5);
        this.flowPieChart.setVgap(10);
        this.flowPieChart.setPadding(new Insets(5, 10, 5, 10));
        this.flowPieChart.setEffect(this.createShadow());
        this.flowPieChart.setBackground(this.createBackground());

        this.flowGeneral.getChildren().add(this.flowPieChart);
    }

    private void initCategoryBarChart(int minValue){
        if (this.flowBarChart.getChildren().contains(this.bcCategory)){
            this.flowBarChart.getChildren().remove(this.bcCategory);
        }
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.bcCategory = new BarChart<>(xAxis,yAxis);

        XYChart.Series series1 = new XYChart.Series();

        Map<String, Long> allCategoryCounts = this.linkHandler.getCategoryCounts();
        Map<String, Long> filteredValues = allCategoryCounts.entrySet()
                .stream().filter(entry -> entry.getValue() >= minValue)
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        filteredValues.entrySet().stream()
                .forEach(stringIntegerEntry -> series1.getData().add(new XYChart.Data(stringIntegerEntry.getKey(), stringIntegerEntry.getValue())));

        series1.setName("Number of links per category");
        bcCategory.getData().addAll(series1);
        this.flowBarChart.getChildren().add(bcCategory);
    }

    private void initWeekdayPieChart(){
        PieChart pieChart = new PieChart();
        Map<Object, Long> weekDayCount = this.linkHandler.getWeekdayCount();

        ObservableList<PieChart.Data> data =
                FXCollections.observableArrayList(weekDayCount.entrySet().stream()
                .map(entry -> {return new PieChart.Data(this.weekdays.get(entry.getKey()), entry.getValue());})
                .collect(Collectors.toList()));

        pieChart.setData(data);
        pieChart.setTitle("Weekdays");

        this.flowPieChart.getChildren().add(pieChart);

    }

    private void initHourBarChart(){
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        this.bcHour = new BarChart(xAxis,yAxis);

        XYChart.Series series1 = new XYChart.Series();

        Map<Object, Long> values = this.linkHandler.getHourCount();

        values.entrySet().stream()
                .forEach(stringIntegerEntry -> series1.getData().add(new XYChart.Data(stringIntegerEntry.getKey().toString(), stringIntegerEntry.getValue())));

        series1.setName("Hours");
        this.bcHour.getData().addAll(series1);

        this.flowPieChart.getChildren().add(this.bcHour);

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
        System.out.println("Scroll pane: " + scrollPane.getWidth());
        System.out.println("Flow pane: " + flowGeneral.getWidth());
        System.out.println("Flow barchart: " + flowBarChart.getWidth());
        System.out.println("Flow barchart pref: " + flowBarChart.getPrefWidth());
        System.out.println("barchart: " + bcCategory.getWidth());
        this.flowGeneral.setPrefWidth(this.scrollPane.getWidth() - 15);
        this.flowNumbers.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.lblLinks.setPrefWidth((this.flowNumbers.getWidth()-20.0)/2.0);
        this.lblCategories.setPrefWidth((this.flowNumbers.getWidth()-20.0)/2.0);
        this.flowBarChart.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.flowPieChart.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.bcCategory.setPrefWidth(this.flowBarChart.getWidth()-15);
        System.out.println("barchart: " + bcCategory.getWidth());
        this.cmbMinCategoryCount.setPrefWidth(this.flowBarChart.getWidth()-15);
    }
}
