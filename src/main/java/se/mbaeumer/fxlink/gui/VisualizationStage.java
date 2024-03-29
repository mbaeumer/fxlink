package se.mbaeumer.fxlink.gui;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import se.mbaeumer.fxlink.api.CategoryHandler;
import se.mbaeumer.fxlink.api.LinkHandler;
import se.mbaeumer.fxlink.api.WordCountHandler;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.util.LinkSplitter;
import se.mbaeumer.fxlink.util.URLHelper;

import java.util.Map;
import java.util.stream.Collectors;

// TODO: Remove warnings regarding "Raw use of parameterized..."


public class VisualizationStage extends Stage {
    private Scene scene;
    private ScrollPane scrollPane;
    private FlowPane flowGeneral;
    private FlowPane flowNumbers;
    private FlowPane flowCategoryCount;
    private FlowPane flowWordCount;
    private ComboBox<String> cmbMinCategoryCount;
    private Slider sliderCategoryCount;
    private Label lblLinks;
    private Label lblCategories;
    private BarChart<String,Number> bcCategory;
    private Slider sliderWordCount;
    private BarChart<String, Number> bcWordCount;

    private FlowPane flowPieChart;
    private BarChart<Object, Long> bcHour;

    private LinkHandler linkHandler;
    private CategoryHandler categoryHandler;
    private WordCountHandler wordCountHandler;
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
            adjustSizes();
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
        this.initCategoryCountFlowPane();
        this.initSlider();
        this.initCategoryBarChart(0);
        this.initWordCountFlowPane();
        this.initWordCountSlider();
        this.initWordCountBarChart();
        this.initPieChartFlowPane();
        this.initWeekdayPieChart();
        this.initHourBarChart();
    }

    private void initHandlers(){
        this.linkHandler = new LinkHandler(new LinkReadDBHandler(), new LinkTagReadDBHandler(),
                new LinkCreationDBHandler(), new LinkUpdateDBHandler(), new LinkDeletionDBHandler(), new FollowUpStatusReadDBHandler());
        this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(), new CategoryCreationDBHandler(),
                new CategoryUpdateDBHandler(), new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());
        this.wordCountHandler = new WordCountHandler(new LinkReadDBHandler(), new LinkSplitter(new URLHelper()));
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

    private void initCategoryCountFlowPane(){
        this.flowCategoryCount = new FlowPane();
        this.flowCategoryCount.setOrientation(Orientation.HORIZONTAL);
        this.flowCategoryCount.setHgap(5);
        this.flowCategoryCount.setVgap(10);
        this.flowCategoryCount.setPadding(new Insets(10, 10, 5, 10));
        this.flowCategoryCount.setEffect(this.createShadow());
        this.flowCategoryCount.setBackground(createBackground());
        this.flowGeneral.getChildren().add(this.flowCategoryCount);
    }

    private void initSlider(){
        this.sliderCategoryCount = new Slider();

        this.sliderCategoryCount.setMin(0);
        this.sliderCategoryCount.setMax(100);
        this.sliderCategoryCount.setValue(5);
        this.sliderCategoryCount.setMajorTickUnit(10);
        this.sliderCategoryCount.setShowTickLabels(true);

        this.sliderCategoryCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            XYChart.Series<String, Number> categoryCountSeries = this.getCategoryCountSeries(newValue.intValue());

            this.bcCategory.getData().setAll(categoryCountSeries);

            for (XYChart.Series<String,Number> serie: bcCategory.getData()){
                for (XYChart.Data<String, Number> item: serie.getData()){
                    Tooltip tooltip = new Tooltip(item.getXValue() + ":" + item.getYValue());
                    tooltip.setShowDelay(Duration.seconds(1));
                    Tooltip.install(item.getNode(), tooltip);
                }
            }

        });

        this.flowCategoryCount.getChildren().add(this.sliderCategoryCount);


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
        this.flowCategoryCount.getChildren().remove(this.bcCategory);
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setCenterShape(true);
        xAxis.setAnimated(false);
        this.bcCategory = new BarChart<>(xAxis,yAxis);

        XYChart.Series<String, Number> series1 = getCategoryCountSeries(minValue);
        bcCategory.getData().addAll(series1);

        for (XYChart.Series<String,Number> serie: bcCategory.getData()){
            for (XYChart.Data<String, Number> item: serie.getData()){
                Tooltip tooltip = new Tooltip(item.getXValue() + ":" + item.getYValue());
                tooltip.setShowDelay(Duration.seconds(1));
                Tooltip.install(item.getNode(), tooltip);
            }
        }
        this.flowCategoryCount.getChildren().add(bcCategory);
    }

    private XYChart.Series<String, Number> getCategoryCountSeries(int minValue) {
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();

        Map<String, Long> allCategoryCounts = this.linkHandler.getCategoryCounts();
        Map<String, Long> filteredValues = allCategoryCounts.entrySet()
                .stream().filter(entry -> entry.getValue() >= minValue)
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

        filteredValues.entrySet().stream()
                .forEach(stringIntegerEntry -> series1.getData().add(new XYChart.Data<>(stringIntegerEntry.getKey(), stringIntegerEntry.getValue())));

        series1.setName("Number of links per category");
        return series1;
    }

    private void initWordCountFlowPane(){
        this.flowWordCount = new FlowPane();
        this.flowWordCount.setOrientation(Orientation.HORIZONTAL);
        this.flowWordCount.setHgap(5);
        this.flowWordCount.setVgap(10);
        this.flowWordCount.setPadding(new Insets(10, 10, 5, 10));
        this.flowWordCount.setEffect(this.createShadow());
        this.flowWordCount.setBackground(createBackground());
        this.flowGeneral.getChildren().add(this.flowWordCount);
    }

    private void initWordCountSlider(){
        this.sliderWordCount = new Slider();

        this.sliderWordCount.setMin(0);
        this.sliderWordCount.setMax(100);
        this.sliderWordCount.setValue(5);
        this.sliderWordCount.setMajorTickUnit(10);
        this.sliderWordCount.setShowTickLabels(true);

        this.sliderWordCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            XYChart.Series wordCountSeries = this.createWordCountSeries(newValue.intValue());
            this.bcWordCount.getData().setAll(wordCountSeries);

            for (XYChart.Series<String,Number> serie: bcWordCount.getData()){
                for (XYChart.Data<String, Number> item: serie.getData()){
                    Tooltip tooltip = new Tooltip(item.getXValue() + ":" + item.getYValue());
                    tooltip.setShowDelay(Duration.seconds(1));
                    Tooltip.install(item.getNode(), tooltip);
                }
            }
        });

        this.flowWordCount.getChildren().add(this.sliderWordCount);
    }

    private void initWordCountBarChart(){
        this.flowWordCount.getChildren().remove(this.bcWordCount);
        final CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCenterShape(true);
        xAxis.setAnimated(false);
        final NumberAxis yAxis = new NumberAxis();
        this.bcWordCount = new BarChart<>(xAxis,yAxis);
        this.bcWordCount.getData().clear();
        this.bcWordCount.getData().addAll(createWordCountSeries(0));



        this.flowWordCount.getChildren().add(bcWordCount);
    }

    private XYChart.Series<String, Number> createWordCountSeries(int min){
        XYChart.Series<String, Number> series1 = new XYChart.Series<>();

        Map<String, Integer> allCategoryCounts = this.wordCountHandler.getWordCount();
        Map<String, Integer> filteredWordCount = allCategoryCounts.entrySet().stream()
                .filter(stringIntegerEntry -> stringIntegerEntry.getValue() >= min)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue
                        ));

        filteredWordCount.entrySet().stream()
                .forEach(stringIntegerEntry -> series1.getData().add(new XYChart.Data<>(stringIntegerEntry.getKey(), stringIntegerEntry.getValue())));
        series1.setName("Word count");

        return series1;
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

        XYChart.Series series1 = new XYChart.Series<>();

        Map<Object, Long> values = this.linkHandler.getHourCount();

        values.entrySet().stream()
                .forEach(stringIntegerEntry -> series1.getData().add(new XYChart.Data<>(stringIntegerEntry.getKey().toString(), stringIntegerEntry.getValue())));

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

    private void adjustSizes(){
        logWidth();
        this.flowGeneral.setPrefWidth(this.scrollPane.getWidth()-15.0);
        this.flowNumbers.setPrefWidth(this.flowGeneral.getWidth()-15.0);
        this.lblLinks.setPrefWidth((this.flowNumbers.getWidth()-20.0)/2.0);
        this.lblCategories.setPrefWidth((this.flowNumbers.getWidth()-20.0)/2.0);
        this.flowCategoryCount.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.sliderCategoryCount.setPrefWidth(this.flowCategoryCount.getWidth()-15);
        this.flowWordCount.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.flowPieChart.setPrefWidth(this.flowGeneral.getWidth()-15);
        this.bcCategory.setPrefWidth(this.flowCategoryCount.getWidth()-15);
        this.sliderWordCount.setPrefWidth(this.flowWordCount.getWidth()-15);
        this.bcWordCount.setPrefWidth(this.flowWordCount.getWidth()-15);
        //this.cmbMinCategoryCount.setPrefWidth(this.flowCategoryCount.getWidth()-15);
        logWidth();
    }

    private void logWidth(){
        System.out.println("---------Resizing-----------");
        System.out.println("Scroll pane: " + scrollPane.getWidth());
        System.out.println("Flow general pane: " + flowGeneral.getWidth());
        System.out.println("Flow general pane pref: " + flowGeneral.getPrefWidth());
        System.out.println("Flow barchart: " + flowCategoryCount.getWidth());
        System.out.println("Flow barchart pref: " + flowCategoryCount.getPrefWidth());
        System.out.println("barchart category: " + bcCategory.getWidth());
    }
}
