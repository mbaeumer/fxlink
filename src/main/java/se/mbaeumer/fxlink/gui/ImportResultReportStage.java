package se.mbaeumer.fxlink.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import se.mbaeumer.fxlink.api.CategoryHandler;
import se.mbaeumer.fxlink.api.LinkHandler;
import se.mbaeumer.fxlink.api.SuggestionDataHandler;
import se.mbaeumer.fxlink.api.SuggestionHandler;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import se.mbaeumer.fxlink.util.URLHelper;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ImportResultReportStage extends Stage {
	private Scene scene;
	private FlowPane flowGeneral;
	private Label lblImportFileName = new Label();
	private Label lblSuccessfulImportsText = new Label("Successful imports");
	private Label lblSuccessfulImportsValue = new Label();
	private Label lblFailedImportsText = new Label("Failed imports");
	private Label lblFailedImportsValue = new Label();
	private ComboBox<Category> cmbMoveToCategory;
	private Button btnMoveToCategory;
	private TabPane tabPane;
	private Tab tabSuccess;
	private Tab tabFailed;
	private TableView<Link> tvSuccessfulLinks;
	private TableView tvFailedLinks;
	private Button btnClose;
	private ImportResultReport importReport;

	private FlowPane flowSelection;
	private FlowPane flowSuggestions;
	private Button btnSelectAll;
	private Button btnDeselectAll;

	private LinkHandler linkHandler;
	private CategoryHandler categoryHandler;
	
	public ImportResultReportStage(ImportResultReport report){
		super();
		this.initRootPane();
		this.initScene();

		this.linkHandler = new LinkHandler(new LinkReadDBHandler(), new LinkTagReadDBHandler(),
				new LinkCreationDBHandler(), new LinkUpdateDBHandler(), new LinkDeletionDBHandler());
		this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(),
				new CategoryCreationDBHandler(), new CategoryUpdateDBHandler(),
				new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());
		this.importReport = report;
		this.initLayout();
		this.initSizes();
		this.setSuccessLinksTableLayout();
		this.setFailedLinksTableLayout();

		this.flowGeneral.widthProperty().addListener((obs, oldVal, newVal) -> {
			this.tabPane.setPrefWidth(this.flowGeneral.widthProperty().doubleValue()-20);
			this.setSuccessLinksTableLayout();
			this.setFailedLinksTableLayout();
		});
	}
	
	private void initScene(){
		int width = 750;
		int height = 800;
		this.scene = new Scene(this.flowGeneral, width, height);
		this.scene.setFill(Color.WHITESMOKE);
		
		this.makeModal();
		
		this.setTitle("Import results");
		this.setScene(this.scene);
	}
	
	private void makeModal(){
		this.initModality(Modality.APPLICATION_MODAL);
		this.initStyle(StageStyle.UTILITY);
	}
	
	private void initRootPane(){
		this.flowGeneral = new FlowPane();
		this.flowGeneral.setOrientation(Orientation.HORIZONTAL);
		this.flowGeneral.setHgap(10);
		this.flowGeneral.setVgap(10);
		this.flowGeneral.setPadding(new Insets(5, 10, 5, 10));
		this.flowGeneral.setVgap(10);
	}
	
	public void initLayout(){
		this.initImportInfoLabels();
		this.createMoveToCategoryComboBox();
		this.createMoveToCategoryButton();
		this.initSelectionPane();
		this.initSelectAllButton();
		this.initDeselectAllButton();
		this.initTabPane();
		this.initSuccessLinkTableView();
		this.initFailedLinksTableView();
		this.initSuggestionPane();
		this.initCloseButton();
	}

	private void initSizes(){
		this.tabPane.setPrefWidth(this.flowGeneral.widthProperty().doubleValue()-20);
		this.tvSuccessfulLinks.prefWidthProperty().bind(this.tabPane.prefWidthProperty());
		this.tvFailedLinks.prefWidthProperty().bind(this.tabPane.prefWidthProperty());
		this.lblImportFileName.prefWidthProperty().bind(this.flowGeneral.widthProperty());
		this.flowSelection.prefWidthProperty().bind(this.flowGeneral.widthProperty());
		this.flowSuggestions.prefWidthProperty().bind(this.flowGeneral.widthProperty());
	}
	
	private void initImportInfoLabels(){
		this.lblImportFileName.setText("Imported from " + this.importReport.getFilename());
		
		this.lblSuccessfulImportsValue.setText(Integer.toString(this.importReport.getSuccessfulLinks().size()));
		this.lblFailedImportsValue.setText(Integer.toString(this.importReport.getFailedLinks().size()));
		this.flowGeneral.getChildren().addAll(this.lblImportFileName, this.lblSuccessfulImportsText,
				this.lblSuccessfulImportsValue, this.lblFailedImportsText, this.lblFailedImportsValue);
	}

	private void createMoveToCategoryComboBox(){
		this.cmbMoveToCategory = new ComboBox<>();

		ObservableList<Category> categoryList =
				FXCollections.observableArrayList(categoryHandler.getCategories());
		categoryList.add(0, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_N_A));

		this.cmbMoveToCategory.setItems(categoryList);

		this.cmbMoveToCategory.setCellFactory(new Callback<ListView<Category>,ListCell<Category>>(){

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

		this.cmbMoveToCategory.setButtonCell(new ListCell<Category>() {
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

		this.cmbMoveToCategory.getSelectionModel().selectFirst();
		this.flowGeneral.getChildren().add(this.cmbMoveToCategory);
	}

	private void createMoveToCategoryButton(){
		this.btnMoveToCategory = new Button("Move to category");
		this.btnMoveToCategory.setOnAction(this::handleMoveToCategory);
		this.flowGeneral.getChildren().add(this.btnMoveToCategory);
	}

	private void handleMoveToCategory(ActionEvent actionEvent){
		Category category = cmbMoveToCategory.getValue();
		for (Link link : getSelectedLinks()){
			link.setCategory(category);
			try {
				linkHandler.updateLink(link);
			}catch(SQLException | ParseException pe){
				Alert alert = new Alert(Alert.AlertType.ERROR, "The link could not be updated", ButtonType.OK);
				alert.showAndWait();
			}
		}
	}

	private List<Link> getSelectedLinks(){
		List<Link> selectedLinks = new ArrayList<>();
		for (Link link : tvSuccessfulLinks.getItems()){
			if (link.isSelected() && link.getId() > 0){
				selectedLinks.add(link);
			}
		}

		return selectedLinks;
	}

	private void initSelectionPane(){
		this.flowSelection = new FlowPane(Orientation.HORIZONTAL);
		this.flowSelection.setHgap(10);

		this.flowGeneral.getChildren().add(this.flowSelection);
	}

	private void initSelectAllButton(){
		this.btnSelectAll = new Button("Select all");
		this.btnSelectAll.setOnAction(this::handleSelect);
		this.flowSelection.getChildren().add(this.btnSelectAll);
	}

	private void handleSelect(ActionEvent actionEvent){
		for (Link link : importReport.getSuccessfulLinks()){
			link.setSelected(true);
		}
	}

	private void initDeselectAllButton(){
		this.btnDeselectAll = new Button("Deselect all");
		this.btnDeselectAll.setOnAction(this::handleDeselect);
		this.flowSelection.getChildren().add(this.btnDeselectAll);
	}

	private void handleDeselect(ActionEvent actionEvent){
		for (Link link : importReport.getSuccessfulLinks()){
			link.setSelected(false);
		}
	}

	private void initTabPane(){
		this.createTabPane();
		this.createTabs();
		this.flowGeneral.getChildren().add(this.tabPane);
	}

	private void createTabPane(){
		this.tabPane = new TabPane();
		this.tabPane.getSelectionModel().selectedItemProperty().addListener(
			new ChangeListener<Tab>() {
				@Override
				public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
					cmbMoveToCategory.setDisable(true);
					btnMoveToCategory.setDisable(true);
					btnSelectAll.setDisable(true);
					btnDeselectAll.setDisable(true);
					if (tabPane.getSelectionModel().isSelected(0)){
						cmbMoveToCategory.setDisable(false);
						btnMoveToCategory.setDisable(false);
						btnSelectAll.setDisable(false);
						btnDeselectAll.setDisable(false);
					}
				}
			}
		);
	}

	private void createTabs(){
		this.tabSuccess = new Tab("Success");
		this.tabFailed = new Tab("Failed");
		this.tabPane.getTabs().add(this.tabSuccess);
		this.tabPane.getTabs().add(this.tabFailed);
		this.tabSuccess.setClosable(false);
		this.tabFailed.setClosable(false);
	}

	private void initSuccessLinkTableView(){
		this.createSuccessLinksTableView();
		this.createSuccessLinksTableViewColumns();
		this.tvSuccessfulLinks.addEventHandler(MouseEvent.MOUSE_CLICKED,
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent me) {
						Link link = tvSuccessfulLinks.getSelectionModel().getSelectedItem();
						initSuggestions(link);

					}
				});

		this.tabSuccess.setContent(this.tvSuccessfulLinks);
	}

	private void createSuccessLinksTableView(){
		this.tvSuccessfulLinks = new TableView<Link>();
		this.tvSuccessfulLinks.setItems(FXCollections.observableList(this.importReport.getSuccessfulLinks()));
		this.tvSuccessfulLinks.setEditable(true);
	}

	private void createSuccessLinksTableViewColumns(){
		TableColumn<Link, Boolean> selectedCol = new TableColumn<Link, Boolean>("Selected");
		selectedCol.setCellValueFactory(c -> c.getValue().selectedProperty());
		selectedCol.setCellFactory( tc -> new CheckBoxTableCell<>());

		TableColumn urlCol = new TableColumn<>("Url");
		urlCol.setCellValueFactory(new PropertyValueFactory("url"));
		urlCol.setCellFactory(TextFieldTableCell.forTableColumn());
		urlCol.setEditable(false);

		// create title column
		TableColumn titleCol = new TableColumn<>("Title");
		titleCol.setCellValueFactory(new PropertyValueFactory("title"));
		titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
		titleCol.setEditable(false);

		TableColumn descriptionCol = new TableColumn<>("Description");
		descriptionCol.setCellValueFactory(new PropertyValueFactory("description"));
		descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
		descriptionCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<Link, String>>() {
					public void handle(TableColumn.CellEditEvent<Link, String> t) {
						Link link = t.getRowValue();
						link.setDescription(t.getNewValue());
						updateLink(link);
						tvSuccessfulLinks.setItems(FXCollections.observableList(importReport.getSuccessfulLinks()));
					}
				}
		);

		TableColumn categoryCol = new TableColumn<>("Category");
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

		TableColumn createdCol = new TableColumn<>("Created");
		createdCol.setCellValueFactory(new PropertyValueFactory("created"));
		createdCol.setCellValueFactory(
				new Callback<TableColumn.CellDataFeatures<Link, String>, ObservableValue<String>>() {
					@Override
					public ObservableValue<String> call(TableColumn.CellDataFeatures<Link, String> film) {
						SimpleStringProperty property = new SimpleStringProperty();
						DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
						property.setValue(dateFormat.format(film.getValue().getCreated()));
						return property;
					}
				});


		this.tvSuccessfulLinks.getColumns().addAll(selectedCol, urlCol, titleCol, descriptionCol, categoryCol, createdCol);
	}

	private void updateLink(Link link){
		try {
			linkHandler.updateLink(link);
		} catch (ParseException | SQLException e) {
			e.printStackTrace();
		}
	}

	private void setSuccessLinksTableLayout(){
		this.tvSuccessfulLinks.getColumns().get(0).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*10)/100);
		this.tvSuccessfulLinks.getColumns().get(1).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*20)/100);
		this.tvSuccessfulLinks.getColumns().get(2).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*40)/100);
		this.tvSuccessfulLinks.getColumns().get(3).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*20)/100);
		this.tvSuccessfulLinks.getColumns().get(4).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*10)/100);
	}

	private void initFailedLinksTableView(){
		this.createFailedLinksTableView();
		this.createFailedLinksTableViewColumns();
		this.tabFailed.setContent(this.tvFailedLinks);
	}
	
	@SuppressWarnings({ "unchecked" })
	private void createFailedLinksTableView(){
		this.tvFailedLinks = new TableView<FailedLink>();
		this.tvFailedLinks.setItems(FXCollections.observableList(this.importReport.getFailedLinks()));
		this.tvFailedLinks.setEditable(false);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createFailedLinksTableViewColumns(){
		TableColumn urlCol = new TableColumn("Url");
		urlCol.setCellValueFactory(new PropertyValueFactory<FailedLink, Link>("link"));
		urlCol.setCellFactory(new Callback<TableColumn<FailedLink, Link>, TableCell<FailedLink, Link>>(){

	        @Override
	        public TableCell<FailedLink, Link> call(TableColumn<FailedLink, Link> param) {

	            TableCell<FailedLink, Link> linkCell = new TableCell<FailedLink, Link>(){

	                @Override
	                protected void updateItem(Link item, boolean empty) {
	                    if (item != null) {
	                    	setText(item.getURL());
	                    }else{
	                    	setText(null);
	                    }
	                }                    
	            };               
	            return linkCell;                
	        }
	    });

		// create the cause column
		TableColumn causeCol = new TableColumn("Cause");
		causeCol.setCellValueFactory(new PropertyValueFactory("cause"));
		causeCol.setCellFactory(TextFieldTableCell.forTableColumn());
		
		this.tvFailedLinks.getColumns().addAll(urlCol, causeCol);
	}
	
	@SuppressWarnings("rawtypes")
	private void setFailedLinksTableLayout(){
		for (Object o : this.tvFailedLinks.getColumns()){
			TableColumn tc = (TableColumn) o;
			tc.setPrefWidth((this.tvFailedLinks.getPrefWidth()*50)/100);
		}
	}

	private void initSuggestionPane(){
		this.flowSuggestions = new FlowPane(Orientation.HORIZONTAL);
		this.flowSuggestions.setPadding(new Insets(5, 5, 0, 5));
		this.flowGeneral.getChildren().add(this.flowSuggestions);
	}

	private void initSuggestions(final Link link){
		this.flowSuggestions.getChildren().clear();
		URLHelper urlHelper = new URLHelper();
		SuggestionDataHandler suggestionDataHandler = new SuggestionDataHandler(urlHelper);
		LinkReadDBHandler linkReadDBHandler = new LinkReadDBHandler();
		SuggestionHandler suggestionHandler = new SuggestionHandler(suggestionDataHandler, urlHelper, linkReadDBHandler);
		List<Suggestion> suggestions = suggestionHandler.getSuggestions(link);

		for (Suggestion suggestion : suggestions){
			Button button = new Button(suggestion.getCategory());
			button.setOnAction(actionEvent -> setCategory(actionEvent, link));
			this.flowSuggestions.getChildren().add(button);
		}
	}

	private void setCategory(ActionEvent actionEvent, Link link){
		try {
			Category category = categoryHandler.getCategoryByName(((Button)actionEvent.getSource()).getText());
			link.setCategory(category);
			linkHandler.updateLink(link);
		} catch (SQLException | ParseException throwables) {
			throwables.printStackTrace();
		}
	}
	
	private void initCloseButton(){
		this.btnClose = new Button("Close");
		this.btnClose.setOnAction((event) -> {close();});
		this.flowGeneral.getChildren().add(this.btnClose);
	}
}
