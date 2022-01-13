package se.mbaeumer.fxlink.gui;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import se.mbaeumer.fxlink.api.*;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.ImportResultReport;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Tag;
import se.mbaeumer.fxlink.util.*;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class FXLink extends Application{

	public static final String IMPORT_BACKUP = "Import backup";
	public static final String SAVE_BACKUP = "Save backup";
	public static final String THE_CURRENT_CONTENT_WILL_BE_OVERWRITTEN_CONTINUE = "The current content will be overwritten. Continue?";
	public static final String LINKS = "Links";
	public static final String DELETE = "Delete";
	public static final String THE_SELECTED_LINKS_WILL_BE_DELETED_CONTINUE = "The selected links will be deleted. Continue?";
	public static final String MOVE_TO_CATEGORY = "Move to category";
	public static final String THE_LINK_COULD_NOT_BE_UPDATED = "The link could not be updated";
	public static final String SEARCH_TERM = "Search term";
	public static final String URL = "URL";
	public static final String DATABASE_ERROR_OCCURRED = "Database error occurred";
	public static final String PLEASE_ENTER_A_SEARCH_TERM_AND_SELECT_AT_LEAST_ONE_CRITERIA = "Please enter a search term and select at least one criteria";
	private static final String STAGE_TITLE = "FX Link";
	private static final String DATABASE_DOES_NOT_EXIST = "The database files do not exist!";
	private static final String ITEMS = "Items";
	private static final String CATEGORY = "Category";
	private static final String RESET = "Reset";
	private static final String IMPORT_TEXT_FILE = "Import text file";
	private final Group root = new Group();
	private Scene scene;
	private FlowPane flowGeneral;
	private FlowPane flowFilter;
	private FlowPane flowSelection;
	private FlowPane flowActions;
	private Label lblItems;
	private ComboBox<String> cmbItems;
	private Label lblCategories;
	private ComboBox<Category> cmbCategories;
	private Button btnResetFilter;
	private Button btnShowImportHistory;
	private Button btnClassify;
	private Button btnExportCsv;
	private Button btnVisualize;
	private Button btnImportTextFile;
	private Button btnShowSearchPane;
	private Button btnDeleteLinks;
	private Button btnCreateItem;
	private ComboBox<Category> cmbMoveToCategory;
	private Button btnMoveToCategory;
	private Button btnGenerateTitle;

	private FlowPane flowSearch;
	private Button btnSearch;
	private Label lblSearchTerm;
	private TextField tfSearchTerm;
	private CheckBox chkSearchURL;
	private CheckBox chkSearchTitle;
	private CheckBox chkSearchDescription;
	private ComboBox<Category> cmbCategoriesSearch;
	private Label lblSearchError;

	private TableView<Link> tblLinks;
	private TableView<Category> tblCategories;
	private TableView<Tag> tblTags;
	
	private Button btnWriteBackup;
	private Button btnReadBackup;
	private Button btnSelectAll;
	private Button btnDeselectAll;
	
	private ContextMenu contLinks;
	private Link selectedLink = null;
	private ContextMenu contTags;
	private FlowPane flowStatus;
	private Label lblStatusItemCountText;
	private Label lblStatusItemCount;
	private Tag selectedTag = null;
	private ContextMenu contCategories;
	private Category selectedCategory = null;

	private LinkHandler linkHandler;
	private CategoryHandler categoryHandler;
	private TitleHandler titleHandler;

	public void start(Stage stage) {
		DatabaseCheckUtil dbCheckUtil = new DatabaseCheckUtilImpl();
		if (dbCheckUtil.checkDatabaseFolder() == DatabaseCheckResult.OK
				&& dbCheckUtil.checkDatabaseFiles() == DatabaseCheckResult.OK){
			this.scene = new Scene(this.root, 1100, 700, Color.WHITESMOKE);
			stage.setTitle(STAGE_TITLE);
			stage.setScene(this.scene);
			stage.show();
			this.initHandlers();
			this.initLayout();
		}else{
			Alert alert = new Alert(Alert.AlertType.ERROR, DATABASE_DOES_NOT_EXIST, ButtonType.OK);
			alert.showAndWait();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void initHandlers(){
		this.linkHandler = new LinkHandler(new LinkReadDBHandler(), new LinkTagReadDBHandler(),
				new LinkCreationDBHandler(), new LinkUpdateDBHandler(), new LinkDeletionDBHandler());
		this.categoryHandler = new CategoryHandler(new CategoryReadDBHandler(),
				new CategoryCreationDBHandler(), new CategoryUpdateDBHandler(),
				new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());
		this.titleHandler = new TitleHandler(new LinkTitleUtilImpl(), new YoutubeCrawler());

	}
	
	public void initLayout() {
		this.createGeneralFlowPane();
		this.createFilterFlowPane();
		this.createActionFlowPane();
		this.createSelectionFlowPane();
		this.createLinkTableView();
		this.createCategoryTableView();
		this.createTagTableView();
		this.createStatusFlowPane();
	}
	
	public void createGeneralFlowPane() {
		this.flowGeneral = new FlowPane();
		this.flowGeneral.setOrientation(Orientation.VERTICAL);
		this.flowGeneral.setPrefWrapLength(700);
		this.flowGeneral.setVgap(5);
		this.root.getChildren().add(this.flowGeneral);
	}
	
	public void createFilterFlowPane(){
		this.flowFilter = new FlowPane();
		this.flowFilter.setOrientation(Orientation.HORIZONTAL);
		this.flowFilter.setHgap(10);
		this.flowFilter.prefWidthProperty().bind(this.flowGeneral.widthProperty());
		this.flowGeneral.getChildren().add(this.flowFilter);
		FlowPane.setMargin(flowFilter, new Insets(5));
		this.createItemLabel();
		this.createItemComboBox();
		this.createCategoryLabel();
		this.createCategoryComboBox();
		this.createResetButton();
		this.createShowImportHistoryButton();
		this.createExperimentalButton();
		this.createVisualizationButton();
		//this.createCsvExportButton();
	}
	
	public void createItemLabel(){
		this.lblItems = new Label(ITEMS);
		this.flowFilter.getChildren().add(this.lblItems);
	}
	
	private void createItemComboBox(){
		this.cmbItems = new ComboBox<>();
		List<String> items;
		items = ManagedItemHandler.getManagedItems(new ManagedItemDBHandler());
		this.cmbItems.setItems(FXCollections.observableArrayList(items));
		this.cmbItems.valueProperty().addListener((observable, oldValue, newValue) ->{
			switchTableView(cmbItems.getValue());
		});

		this.cmbItems.getSelectionModel().selectFirst();
		this.flowFilter.getChildren().add(this.cmbItems);
	}
	
	private void createCategoryLabel(){
		this.lblCategories = new Label(CATEGORY);
		this.flowFilter.getChildren().add(this.lblCategories);
	}
	
	private void createCategoryComboBox(){
		this.cmbCategories = new ComboBox<>();
		loadCategoriesForFilter();
		this.cmbCategories.setCellFactory(new Callback<ListView<Category>,ListCell<Category>>(){
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
		
		this.cmbCategories.setButtonCell(new ListCell<Category>() {
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

		this.cmbCategories.getSelectionModel().selectFirst(); //select the first element
		this.cmbCategories.valueProperty().addListener((observable, oldValue, newValue) -> {
			filterCategories();
		});

		this.flowFilter.getChildren().add(this.cmbCategories);
	}

	private void loadCategoriesForFilter() {
		ObservableList<Category> categoryList =
	            FXCollections.observableArrayList(categoryHandler.getCategories());
		categoryList.add(0, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_ALL));
		categoryList.add(1, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_N_A));

		this.cmbCategories.setItems(categoryList);
		this.cmbCategories.getSelectionModel().selectFirst();
	}

	private void loadCategoriesForMove() {
		ObservableList<Category> categoryList =
				FXCollections.observableArrayList(categoryHandler.getCategories());
		categoryList.add(0, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_N_A));

		this.cmbMoveToCategory.setItems(categoryList);
		this.cmbMoveToCategory.getSelectionModel().selectFirst();
	}

	private void createResetButton(){
		btnResetFilter = new Button(RESET);
		this.btnResetFilter.setOnAction(actionEvent -> {
			cmbCategories.getSelectionModel().selectFirst();
			cmbItems.getSelectionModel().selectFirst();
		});

		this.flowFilter.getChildren().add(this.btnResetFilter);
	}

	private void createShowImportHistoryButton(){
		btnShowImportHistory = new Button("Show import history");
		this.btnShowImportHistory.setOnAction(actionEvent -> {
			ImportHistoryStage importHistoryStage;
			try {
				importHistoryStage = new ImportHistoryStage();
				importHistoryStage.showAndWait();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		this.flowFilter.getChildren().add(this.btnShowImportHistory);
	}

	private void createExperimentalButton(){
		this.btnClassify = new Button("Classification");
		this.btnClassify.setOnAction(actionEvent -> {
			ClassifyStage classifyStage = new ClassifyStage();
			classifyStage.showAndWait();
		});

		this.flowFilter.getChildren().add(this.btnClassify);
	}

	private void createVisualizationButton(){
		this.btnVisualize = new Button("Visualization");
		this.btnVisualize.setOnAction(actionEvent -> {
			VisualizationStage visualizationStage = new VisualizationStage();
			visualizationStage.showAndWait();
		});

		this.flowFilter.getChildren().add(this.btnVisualize);
	}

	private void createCsvExportButton(){
		this.btnExportCsv = new Button("CSV Export");
		this.btnExportCsv.setOnAction(actionEvent -> {
			CsvExportHandler csvExportHandler =
					new CsvExportHandler(new LinkReadDBHandler(), new URLHelper());
			csvExportHandler.getData();
			//ClassifyStage classifyStage = new ClassifyStage();
			//classifyStage.showAndWait();
		});
		this.flowFilter.getChildren().add(this.btnExportCsv);
	}

	private void createActionFlowPane(){
		this.flowActions = new FlowPane();
		this.flowActions.setOrientation(Orientation.HORIZONTAL);
		this.flowActions.setHgap(10);
		this.flowActions.prefWidthProperty().bind(this.flowGeneral.widthProperty());
		this.flowGeneral.getChildren().add(this.flowActions);
		FlowPane.setMargin(flowActions, new Insets(5));
		this.createNewItemButton();
		this.createDeleteLinksButton();
		this.createShowSearchPaneButton();
        this.createGenerateTitleButton();
		this.createMoveToCategoryComboBox();
		this.createMoveToCategoryButton();
		this.createImportButton();
		this.createWriteBackupButton();
		this.createReadBackupButton();
	}

	private void createImportButton(){
		btnImportTextFile = new Button(IMPORT_TEXT_FILE);

		this.btnImportTextFile.setOnAction(this::handleImportButton);
		this.flowActions.getChildren().add(this.btnImportTextFile);
	}

	private void handleImportButton(ActionEvent actionEvent){
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(
				new ExtensionFilter("txt", "*.txt"));
		File importFile = fc.showOpenDialog(null);
		if (importFile == null){
			actionEvent.consume();
			return;
		}

		// handle duplicate imports here
		String message = "It seems that this file has been imported already. Proceed anyway?";
		boolean isDuplicate = false;
		try {
			logFileImport(importFile.getCanonicalPath());
		} catch (SQLException | IOException  e) {
			isDuplicate = true;
		}

		// if there seems to be a duplicate
		if (isDuplicate){
			Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.YES, ButtonType.NO);
			Optional<ButtonType> result = alert.showAndWait();

			if (result.isPresent() && result.get() == ButtonType.YES){
				importTextFile(importFile);
			}

		}else{
			importTextFile(importFile);
		}
	}

	private void importTextFile(File importFile) {
		TextImportHandler tih = new TextImportHandler();
		try {
			tih.importFromTextFile(importFile);
			ImportResultReport report = new ImportResultReport();
			report.setFilename(importFile.getCanonicalPath());
			report.setImportDate(new Date());
			report.setSuccessfulLinks(tih.getImportedLinks());
			report.setFailedLinks(tih.getFailedLinks());
			ImportResultReportStage reportStage = new ImportResultReportStage(report);
			reportStage.showAndWait();
			cmbItems.setValue("Links");
			refreshLinkTable();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void logFileImport(final String filename) throws SQLException {
		ImportItemHandler importItemHandler = new ImportItemHandler();
		importItemHandler.createImportItem(filename);
	}
	
	private void createWriteBackupButton(){
		btnWriteBackup = new Button(SAVE_BACKUP);
		this.btnWriteBackup.setOnAction(this::handleWriteBackup);
		this.flowActions.getChildren().add(this.btnWriteBackup);
	}

	private void handleWriteBackup(ActionEvent arg0){
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(
				new ExtensionFilter("XML", "*.xml"));
		File importFile = fc.showSaveDialog(null);

		if (importFile == null){
			arg0.consume();
			return;
		}

		try {
			String path = importFile.getCanonicalPath();
			if (!path.endsWith(".xml")){
				path += ".xml";
			}

			XMLExportHandler.exportData(path);
		}catch (IOException | XMLStreamException | SQLException e1 ) {
			e1.printStackTrace();
		}
	}
	
	private void createReadBackupButton(){
		this.btnReadBackup = new Button(IMPORT_BACKUP);
		this.btnReadBackup.setOnAction(this::handleReadBackup);
		this.flowActions.getChildren().add(this.btnReadBackup);
	}

	private void handleReadBackup(ActionEvent actionEvent){
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(
				new ExtensionFilter("XML", "*.xml"));
		File importFile = fc.showOpenDialog(null);

		if (importFile == null){
			actionEvent.consume();
		}

		// ask the use to confirm that the database content
		// will be overwritten now
		Alert alert = new Alert(Alert.AlertType.WARNING, THE_CURRENT_CONTENT_WILL_BE_OVERWRITTEN_CONTINUE, ButtonType.YES, ButtonType.NO);
		Optional<ButtonType> result = alert.showAndWait();

		XMLImportHandler xmlImportHandler = new XMLImportHandler(categoryHandler);
		if (result.isPresent() && result.get() == ButtonType.YES) {
			try {
				xmlImportHandler.readData(importFile.getCanonicalPath());
				xmlImportHandler.truncateDatabase();
				xmlImportHandler.importData();
				loadCategoriesForFilter();
				loadCategoriesForMove();
				cmbItems.setValue(LINKS);
				refreshLinkTable();
			} catch (IOException | XMLStreamException | SQLException e) {
				e.printStackTrace();
			}
		}else{
			actionEvent.consume();
		}
	}
	
	private void createShowSearchPaneButton(){
		this.btnShowSearchPane = new Button(getSearchPaneTitle());
		
		this.btnShowSearchPane.setOnAction(this::handleShowSearchPane);
		this.flowActions.getChildren().add(this.btnShowSearchPane);
	}

	private void handleShowSearchPane(ActionEvent actionEvent){
		if (isSearchPaneVisible()){
			removeSearchPane();
			refreshLinkTable();
		}else{
			createSearchFlowPane();
		}
		btnShowSearchPane.setText(getSearchPaneTitle());
	}

	private void createDeleteLinksButton(){
		this.btnDeleteLinks = new Button(DELETE);
		this.btnDeleteLinks.setOnAction(this::deleteSelectedLinks);
		this.flowActions.getChildren().add(this.btnDeleteLinks);
	}

	private void deleteSelectedLinks(ActionEvent actionEvent){
		List<Link> selectedLinks = getSelectedLinks();

		if (selectedLinks.size() > 0) {
			Alert alert = new Alert(Alert.AlertType.WARNING, THE_SELECTED_LINKS_WILL_BE_DELETED_CONTINUE, ButtonType.YES, ButtonType.NO);
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.YES) {
				for (Link link : selectedLinks) {
					try {
						linkHandler.deleteLink(link);
					} catch (SQLException sqle) {
						alert = new Alert(Alert.AlertType.ERROR, "The link could not be deleted", ButtonType.OK);
						alert.showAndWait();
					}
				}
			}
			refreshLinkTable();
		}
	}

	private void createNewItemButton(){
		this.btnCreateItem = new Button("New");
		this.btnCreateItem.setOnAction(this::handleNewItemButtonClick);
		this.flowActions.getChildren().add(this.btnCreateItem);
	}

	private void handleNewItemButtonClick(ActionEvent actionEvent){
		if (cmbItems.getValue().equalsIgnoreCase("Links")){
			LinkViewDetailStage linkDetail = new LinkViewDetailStage(LinkHandler.createPseudoLink());
			linkDetail.showAndWait();
			refreshLinkTable();
		}else{
			CreateCategoryStage createCategoryStage = new CreateCategoryStage();
			createCategoryStage.showAndWait();
			refreshCategoryTable(null);
		}
	}

	private void createMoveToCategoryComboBox(){
		this.cmbMoveToCategory = new ComboBox<Category>();

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
		this.flowActions.getChildren().add(this.cmbMoveToCategory);
	}

	private void createMoveToCategoryButton(){
		this.btnMoveToCategory = new Button(MOVE_TO_CATEGORY);
		this.btnMoveToCategory.setOnAction(this::handleMoveCategory);
		this.flowActions.getChildren().add(this.btnMoveToCategory);
	}

	private void handleMoveCategory(ActionEvent actionEvent){
		Category category = cmbMoveToCategory.getValue();
		for (Link link : getSelectedLinks()){
			link.setCategory(category);
			try {
				linkHandler.updateLink(link);
			}catch(SQLException | ParseException pe){
				Alert alert = new Alert(Alert.AlertType.ERROR, THE_LINK_COULD_NOT_BE_UPDATED, ButtonType.OK);
				alert.showAndWait();
			}
		}
		filterCategories();
	}

	private void createGenerateTitleButton(){
		this.btnGenerateTitle = new Button("Generate title");
		this.btnGenerateTitle.setOnAction(this::handleGenerateTitle);
		this.flowActions.getChildren().add(this.btnGenerateTitle);
	}

	private void handleGenerateTitle(ActionEvent actionEvent){
		for (Link link : getSelectedLinks()){
			link.setTitle(this.titleHandler.generateTitle(link));
			try {
				linkHandler.updateLink(link);
			}catch(SQLException | ParseException pe){
				Alert alert = new Alert(Alert.AlertType.ERROR, THE_LINK_COULD_NOT_BE_UPDATED, ButtonType.OK);
				alert.showAndWait();
			}
		}
		filterCategories();
	}

	private void createSelectionFlowPane(){
		this.flowSelection = new FlowPane(Orientation.HORIZONTAL);
		this.flowSelection.setHgap(10);
		this.flowSelection.prefWidthProperty().bind(this.flowGeneral.widthProperty());
		this.flowGeneral.getChildren().add(this.flowSelection);
		FlowPane.setMargin(flowSelection, new Insets(5));
		this.createSelectAllButton();
		this.createDeselectAllButton();
	}

	private void createSelectAllButton(){
		this.btnSelectAll = new Button("Select all");
		this.btnSelectAll.setOnAction(actionEvent ->  {
			for (Link link : tblLinks.getItems()){
				link.setSelected(true);
			}
		});

		this.flowSelection.getChildren().add(this.btnSelectAll);
	}

	private void createDeselectAllButton(){
		this.btnDeselectAll = new Button("Deselect all");
		this.btnDeselectAll.setOnAction(actionEvent -> {
			for (Link link : tblLinks.getItems()){
				link.setSelected(false);
			}
		});

		this.flowSelection.getChildren().add(this.btnDeselectAll);
	}

	private List<Link> getSelectedLinks(){
		List<Link> selectedLinks = new ArrayList<>();
		for (Link link : tblLinks.getItems()){
			if (link.isSelected() && link.getId() > 0){
				selectedLinks.add(link);
			}
		}
		return selectedLinks;
	}
	
	private String getSearchPaneTitle(){
		String title = "Show search";
		if (this.isSearchPaneVisible()){
			title = "Hide search";
		}
		return title;
	}
	
	private void createSearchFlowPane(){
		this.flowSearch = new FlowPane();
		this.flowSearch.setOrientation(Orientation.HORIZONTAL);
		this.flowSearch.setHgap(10);
		
		this.showSearchPane();
		FlowPane.setMargin(flowSearch, new Insets(5, 5, 0, 5));
		
		this.createSearchTermLabel();
		this.createSearchTermTextField();
		this.createURLSearchCheckBox();
		this.createTitleSearchCheckBox();
		this.createDescriptionSearchCheckBox();
		this.createSearchCategoryComboBox();
		this.createSearchButton();
		this.createSearchErrorStatusLabel();
		this.flowSearch.prefHeightProperty().set(27);
	}
	
	private void createSearchTermLabel(){
		this.lblSearchTerm = new Label(SEARCH_TERM);
		this.flowSearch.getChildren().add(this.lblSearchTerm);
	}
	
	private void createSearchTermTextField(){
		this.tfSearchTerm = new TextField();
		this.tfSearchTerm.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.length() > 0){
					runSearch();
				}else{
					refreshLinkTable();
					updateStatusBar(false);
				}
			}
		});
		this.flowSearch.getChildren().add(this.tfSearchTerm);

	}
	
	private void createURLSearchCheckBox(){
		this.chkSearchURL = new CheckBox(URL);
		this.chkSearchURL.setSelected(true);
		this.chkSearchURL.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				runSearch();
			}
		});

		this.flowSearch.getChildren().add(this.chkSearchURL);
	}
	
	private void createTitleSearchCheckBox(){
		this.chkSearchTitle= new CheckBox("title");
		this.chkSearchTitle.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				runSearch();
			}
		});
		this.flowSearch.getChildren().add(this.chkSearchTitle);
	}

	private void createDescriptionSearchCheckBox(){
		this.chkSearchDescription = new CheckBox("description");
		this.chkSearchDescription.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				runSearch();
			}
		});
		this.flowSearch.getChildren().add(this.chkSearchDescription);
	}

	private void createSearchCategoryComboBox(){
		this.cmbCategoriesSearch = new ComboBox<Category>();

		ObservableList<Category> categoryList =
				FXCollections.observableArrayList(categoryHandler.getCategories());
		categoryList.add(0, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_ALL));
		categoryList.add(1, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_N_A));

		this.cmbCategoriesSearch.setItems(categoryList);

		this.cmbCategoriesSearch.setCellFactory(new Callback<ListView<Category>,ListCell<Category>>(){
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

		this.cmbCategoriesSearch.setButtonCell(new ListCell<Category>() {
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

		this.cmbCategoriesSearch.getSelectionModel().selectFirst(); //select the first element
		this.cmbCategoriesSearch.valueProperty().addListener(
				new ChangeListener<Category>(){
					@SuppressWarnings("rawtypes")
					@Override
					public void changed(ObservableValue ov, Category c1, Category c2){
						runSearch();
					}
				});

		this.flowSearch.getChildren().add(this.cmbCategoriesSearch);
	}
	
	private void createSearchButton(){
		this.btnSearch = new Button("Search");
		this.btnSearch.setOnAction(actionEvent -> runSearch());

		this.flowSearch.getChildren().add(this.btnSearch);
	}

	private void createSearchErrorStatusLabel(){
		this.lblSearchError = new Label();
		this.flowSearch.getChildren().add(this.lblSearchError);
		this.lblSearchError.setTextFill(Color.ORANGERED);
		this.lblSearchError.setVisible(false);
	}
	
	private boolean isSearchTermGiven(){
		return this.tfSearchTerm.getText().length() > 0;
	}
	
	private boolean isCriteriaSelected(){
		return this.chkSearchURL.isSelected() 
				|| this.chkSearchTitle.isSelected()
				|| this.chkSearchDescription.isSelected();
	}
	
	private void showSearchPane(){
		this.flowGeneral.getChildren().add(2, this.flowSearch);
	}
	
	private void removeSearchPane(){
		if (this.isSearchPaneVisible()){
			this.flowGeneral.getChildren().remove(2);
		}
	}
	
	private boolean isSearchPaneVisible(){
		return this.flowGeneral.getChildren().contains(this.flowSearch);
	}

	private void runSearch(){
		if (isSearchTermGiven() && isCriteriaSelected()){
			this.lblSearchError.setVisible(false);
			try {
				List<Link> links = SearchHandler.findLinks(tfSearchTerm.getText(), chkSearchURL.isSelected(),
						chkSearchTitle.isSelected(), chkSearchDescription.isSelected(), cmbCategoriesSearch.getValue());
				refreshSearchResult(links);
			} catch (SQLException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, DATABASE_ERROR_OCCURRED, ButtonType.OK);
				alert.showAndWait();
			}
		}else{
			this.lblSearchError.setText(PLEASE_ENTER_A_SEARCH_TERM_AND_SELECT_AT_LEAST_ONE_CRITERIA);
			this.lblSearchError.setVisible(true);
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createLinkTableView(){
		this.tblLinks = new TableView();

		//LinkHandler linkHandler = new LinkHandler(new LinkReadDBHandler());

		this.tblLinks.setItems(FXCollections.observableList(this.linkHandler.getLinks()));
		this.tblLinks.getItems().add(LinkHandler.createPseudoLink());

		this.createLinkTableColumns();
		this.tblLinks.setEditable(true);

		this.tblLinks.prefWidthProperty().bind(this.scene.widthProperty());
		this.tblLinks.prefWidthProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue ov, Object oldValue, Object newValue){
				setLinkTableLayout();
			}
		});
		this.setLinkTableLayout();
		
		tblLinks.addEventHandler(MouseEvent.MOUSE_CLICKED, 
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent me) {
						if (contLinks != null){
							contLinks.hide();
						}
						if (me.getButton() == MouseButton.SECONDARY){
							selectedLink = tblLinks.getSelectionModel().getSelectedItem();
							if (selectedLink != null){
								createLinkContextMenu();
								contLinks.show(tblLinks, me.getScreenX(), me.getScreenY());
							}
						}
					}
				});

		this.flowGeneral.getChildren().add(this.tblLinks);

		FlowPane.setMargin(flowGeneral, new Insets(5));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createLinkTableColumns(){
		// selected column
		TableColumn<Link, Boolean> selectedCol = new TableColumn<Link, Boolean>("Selected");
		selectedCol.setCellValueFactory(c -> c.getValue().selectedProperty());
		selectedCol.setCellFactory( tc -> new CheckBoxTableCell<>());

		// create the url column
		TableColumn urlCol = new TableColumn("Url");
		urlCol.setCellValueFactory(new PropertyValueFactory("url"));
		urlCol.setCellFactory(TextFieldTableCell.forTableColumn());
		urlCol.setOnEditCommit(
		    new EventHandler<CellEditEvent<Link, String>>() {
		        public void handle(CellEditEvent<Link, String> t) {
					if (isLinkInformationCorrect(t.getNewValue()) && isURLCorrect(t.getNewValue())){
						((Link) t.getTableView().getItems().get(
								t.getTablePosition().getRow())
						).setURL(t.getNewValue());
						if (insertOrUpdateLink(t.getRowValue())){
							refreshLinkTable();
						}
					}
		        }
		    }
		);

		// create filename column
		TableColumn titleCol = new TableColumn("Title");
		titleCol.setCellValueFactory(new PropertyValueFactory("title"));
		titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
		titleCol.setOnEditCommit(
				new EventHandler<CellEditEvent<Link, String>>() {
					public void handle(CellEditEvent<Link, String> t) {
						Link link = t.getRowValue();
						link.setTitle(t.getNewValue());
						if (isLinkInformationCorrect(link.getURL())){
							if (insertOrUpdateLink(link)){
								refreshLinkTable();
							}
						}
					}
				}
		);

		// create description column
		TableColumn descriptionCol = new TableColumn("Description");
		descriptionCol.setCellValueFactory(new PropertyValueFactory("description"));
		descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
		descriptionCol.setOnEditCommit(
		    new EventHandler<CellEditEvent<Link, String>>() {
		        public void handle(CellEditEvent<Link, String> t) {
					Link link = t.getRowValue();
					link.setDescription(t.getNewValue());
					if (isLinkInformationCorrect(link.getURL())){
						if (insertOrUpdateLink(link)){
							refreshLinkTable();
						}
					}
		        }
		    }
		);

		TableColumn categoryCol = new TableColumn("Category");
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

		// create the created column
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
		
		TableColumn lastUpdatedCol = new TableColumn("Last modified");
		lastUpdatedCol.setCellValueFactory(new PropertyValueFactory("lastUpdated"));
		lastUpdatedCol.setCellValueFactory(
				   new Callback<TableColumn.CellDataFeatures<Link, String>, ObservableValue<String>>() {
					      @Override
					      public ObservableValue<String> call(TableColumn.CellDataFeatures<Link, String> link) {
					         SimpleStringProperty property = new SimpleStringProperty();
					         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					         property.setValue(dateFormat.format(link.getValue().getLastUpdated()));
					         return property;
					      }
					   });

		// add all columns to the table view
		this.tblLinks.getColumns().addAll(selectedCol, urlCol, titleCol, descriptionCol, categoryCol, createdCol, lastUpdatedCol);
	}
	
	@SuppressWarnings("rawtypes")
	private void setLinkTableLayout(){
		for (Object o : this.tblLinks.getColumns()){
			TableColumn tc = (TableColumn) o;
			tc.setPrefWidth((this.tblLinks.getPrefWidth()*15)/100);
		}
		this.tblLinks.getColumns().get(0).setPrefWidth((this.tblLinks.getPrefWidth()*10)/100);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createCategoryTableView(){
		this.tblCategories = new TableView<Category>();
		this.tblCategories.prefWidthProperty().bind(this.scene.widthProperty());
		this.tblCategories.prefWidthProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue ov, Object oldValue, Object newValue){
				setCategoryTableLayout();
			}
		});

		this.tblCategories.setItems(FXCollections.observableList(categoryHandler.getCategories()));
		this.tblCategories.getItems().add(CategoryHandler.createPseudoCategory(ValueConstants.VALUE_NEW));

		this.createCategoryTableColumns();
		this.tblCategories.setEditable(true);
		this.setCategoryTableLayout();
		
		tblCategories.addEventHandler(MouseEvent.MOUSE_CLICKED, 
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent me) {
						if (contCategories != null){
							contCategories.hide();
						}
						if (me.getButton() == MouseButton.SECONDARY){
							selectedCategory = tblCategories.getSelectionModel().getSelectedItem();
							if (selectedCategory != null){
								createCategoryContextMenu();
								contCategories.show(tblCategories, me.getScreenX(), me.getScreenY());
							}
						}
					}
				});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createCategoryTableColumns(){
		// create the name column
		TableColumn nameCol = new TableColumn("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory("name"));
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		nameCol.setOnEditCommit(
		    new EventHandler<CellEditEvent<Category, String>>() {
		        public void handle(CellEditEvent<Category, String> t) {
		        	if (isCategoryNameCorrect(t.getNewValue())){		        		
		        		   ((Category) t.getTableView().getItems().get(
		   		                t.getTablePosition().getRow())
		   		                ).setName(t.getNewValue());
		   		            refreshCategoryTable(t.getRowValue());		        		
		        	}
		        }
		    }
		);

		// create description column
		TableColumn descriptionCol = new TableColumn("description");
		descriptionCol.setCellValueFactory(new PropertyValueFactory("description"));
		descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
		descriptionCol.setOnEditCommit(
		    new EventHandler<CellEditEvent<Category, String>>() {
		        public void handle(CellEditEvent<Category, String> t) {
		        	if (isCategoryInformationCorrect(t.getRowValue())){		        		
		        		((Category) t.getTableView().getItems().get(
				                t.getTablePosition().getRow())
				                ).setDescription(t.getNewValue());
				            refreshCategoryTable(t.getRowValue());		        		
		        	}
		        }
		    }
		);
		
		// create the created column
		TableColumn createdCol = new TableColumn("Created");
		createdCol.setCellValueFactory(new PropertyValueFactory("created"));
		createdCol.setCellValueFactory(
				   new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
					      @Override
					      public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> category) {
					         SimpleStringProperty property = new SimpleStringProperty();
					         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					         property.setValue(dateFormat.format(category.getValue().getCreated()));
					         return property;
					      }
					   });

		TableColumn<Category, String> lastUpdatedCol = new TableColumn<Category, String>("Last modified");
		lastUpdatedCol.setCellValueFactory(new PropertyValueFactory<Category, String>("lastUpdated"));
		lastUpdatedCol.setCellValueFactory(
				   new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
					      @Override
					      public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> category) {
					         SimpleStringProperty property = new SimpleStringProperty();
					         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					         property.setValue(dateFormat.format(category.getValue().getLastUpdated()));
					         return property;
					      }
					   });
						
		// add all columns to the table view
		this.tblCategories.getColumns().addAll(nameCol, descriptionCol, createdCol, lastUpdatedCol);

	}
	
	@SuppressWarnings("rawtypes")
	private void setCategoryTableLayout(){
		for (Object o : this.tblCategories.getColumns()){
			TableColumn tc = (TableColumn) o;
			tc.setPrefWidth((this.tblCategories.getPrefWidth()*25)/100);
		}
	}
	
	private void createTagTableView(){
		// create the table view itself
		this.tblTags = new TableView<Tag>();
	
		this.tblTags.prefWidthProperty().bind(this.scene.widthProperty());
		this.tblTags.prefWidthProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue ov, Object oldValue, Object newValue){
				setTagTableLayout();
			}
		});

		this.tblTags.setItems(FXCollections.observableList(TagHandler.getTags()));
		this.tblTags.getItems().add(TagHandler.createPseudoTag());

		this.createTagTableColumns();
		this.tblTags.setEditable(true);
		this.setTagTableLayout();
		
		tblTags.addEventHandler(MouseEvent.MOUSE_CLICKED, 
				new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent me) {
						if (contTags != null){
							contTags.hide();
						}
						if (me.getButton() == MouseButton.SECONDARY){
							selectedTag = tblTags.getSelectionModel().getSelectedItem();
							if (selectedTag != null){
								createTagContextMenu();
								contTags.show(tblTags, me.getScreenX(), me.getScreenY());
							}
						}
					}
				});
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void createTagTableColumns(){
		// create the name column
		TableColumn nameCol = new TableColumn("Name");
		nameCol.setCellValueFactory(new PropertyValueFactory("name"));
		nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
		nameCol.setOnEditCommit(
		    new EventHandler<CellEditEvent<Tag, String>>() {
		        public void handle(CellEditEvent<Tag, String> t) {
		        	if (isTagNameCorrect(t.getNewValue())){		        		
		        		((Tag) t.getTableView().getItems().get(
				                t.getTablePosition().getRow())
				                ).setName(t.getNewValue());
				            refreshTagTable(t.getRowValue());
		        	}
		        }
		    }
		);

		// create description column
		TableColumn descriptionCol = new TableColumn("description");
		descriptionCol.setCellValueFactory(new PropertyValueFactory("description"));
		descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
		descriptionCol.setOnEditCommit(
		    new EventHandler<CellEditEvent<Tag, String>>() {
		        public void handle(CellEditEvent<Tag, String> t) {
		        	if (isTagInformationCorrect(t.getRowValue())){		        		
		        		((Tag) t.getTableView().getItems().get(
				                t.getTablePosition().getRow())
				                ).setDescription(t.getNewValue());
				            refreshTagTable(t.getRowValue());		        		
		        	}
		        }
		    }
		);
				
		// create the created column
		TableColumn createdCol = new TableColumn("Created");
		createdCol.setCellValueFactory(new PropertyValueFactory("created"));
		createdCol.setCellValueFactory(
		   new Callback<TableColumn.CellDataFeatures<Tag, String>, ObservableValue<String>>() {
				  @Override
				  public ObservableValue<String> call(TableColumn.CellDataFeatures<Tag, String> tag) {
					 SimpleStringProperty property = new SimpleStringProperty();
					 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					 property.setValue(dateFormat.format(tag.getValue().getCreated()));
					 return property;
				  }
		   }
	    );

		TableColumn<Tag, String> lastUpdatedCol = new TableColumn<Tag, String>("Last modified");
		lastUpdatedCol.setCellValueFactory(new PropertyValueFactory<Tag, String>("lastUpdated"));
		lastUpdatedCol.setCellValueFactory(
		   new Callback<TableColumn.CellDataFeatures<Tag, String>, ObservableValue<String>>() {
				  @Override
				  public ObservableValue<String> call(TableColumn.CellDataFeatures<Tag, String> tag) {
					 SimpleStringProperty property = new SimpleStringProperty();
					 DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					 property.setValue(dateFormat.format(tag.getValue().getLastUpdated()));
					 return property;
				  }
		   }
	     );
								
		this.tblTags.getColumns().addAll(nameCol, descriptionCol, createdCol, lastUpdatedCol);

	}
	
	@SuppressWarnings("rawtypes")
	private void setTagTableLayout(){
		for (Object o : this.tblTags.getColumns()){
			TableColumn tc = (TableColumn) o;
			tc.setPrefWidth((this.tblTags.getPrefWidth()*25)/100);
		}
	}
	
	private void createLinkContextMenu(){
		this.contLinks = new ContextMenu();
		
		// edit
		MenuItem miEdit = new MenuItem("Edit link");
		miEdit.setOnAction(actionEvent ->  {
			LinkViewDetailStage linkDetail = new LinkViewDetailStage(selectedLink);
			linkDetail.showAndWait();
			refreshLinkTable();
		});

		// open URL
		MenuItem miOpenURL = new MenuItem("Open link");
		miOpenURL.setOnAction(actionEvent ->  {
			try {
				BrowserLauncher.openURL(selectedLink.getURL());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});

		// delete
		MenuItem miDelete = new MenuItem("Delete link");
		miDelete.setOnAction(actionEvent ->  {
			try {
				Alert alert = new Alert(Alert.AlertType.WARNING, "The selected link will be deleted. Continue?", ButtonType.YES, ButtonType.NO);
				Optional<ButtonType> result = alert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.YES){
					linkHandler.deleteLink(selectedLink);
					refreshLinkTable();
				}
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		this.contLinks.getItems().addAll(miEdit, miOpenURL, miDelete);
	}

	private void createTagContextMenu(){
		this.contTags = new ContextMenu();

		MenuItem miDelete = new MenuItem("Delete tag");
		miDelete.setOnAction(actionEvent ->  {
			try {
				List<Link> links = linkHandler.getLinksWithTag(selectedTag.getId());
				if (links.size() > 0){
					Alert alert = new Alert(Alert.AlertType.WARNING, "This tag is used by " + links.size() + " links. Do you really want to delete this tag?", ButtonType.YES, ButtonType.NO);
					Optional<ButtonType> result = alert.showAndWait();

					if (result.isPresent() && result.get() == ButtonType.YES){
						TagHandler.deleteTag(selectedTag);
					}
				}else{
					TagHandler.deleteTag(selectedTag);
				}
				refreshTagTable(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		this.contTags.getItems().addAll(miDelete);
	}
	
	private void createCategoryContextMenu(){
		this.contCategories = new ContextMenu();

		MenuItem miMove = new MenuItem("Move to another category");
		miMove.setOnAction(actionEvent ->  {
			MoveCategoryStage mergeStage = new MoveCategoryStage(selectedCategory);
			mergeStage.showAndWait();
			refreshCategoryTable(null);
		});

		MenuItem miDelete = new MenuItem("Delete category");
		miDelete.setOnAction(actionEvent ->  {
			try {
				List<Link> links = linkHandler.getLinksByCategory(selectedCategory);
				if (links.size() > 0){
					Alert alert = new Alert(Alert.AlertType.WARNING, "This category is contains " + links.size() + " links. Do you really want to delete this category?", ButtonType.YES, ButtonType.NO);
					Optional<ButtonType> result = alert.showAndWait();

					if (result.isPresent() && result.get() == ButtonType.YES){
						categoryHandler.deleteCategory(selectedCategory);
					}
				}else{
					categoryHandler.deleteCategory(selectedCategory);
				}

				refreshCategoryTable(null);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		});

		this.contCategories.getItems().addAll(miMove, miDelete);
	}

	private void createStatusFlowPane(){
		this.flowStatus = new FlowPane();
		this.flowStatus.setOrientation(Orientation.HORIZONTAL);
		this.flowStatus.setHgap(10);
		this.flowGeneral.getChildren().add(this.flowStatus);
		FlowPane.setMargin(flowStatus, new Insets(5));
		this.createStatusLabels();
	}

	private void createStatusLabels(){
		this.lblStatusItemCountText = new Label(cmbItems.getValue());
		this.lblStatusItemCount = new Label();
		this.updateStatusBar(false);
		this.flowStatus.getChildren().add(this.lblStatusItemCountText);
		this.flowStatus.getChildren().add(this.lblStatusItemCount);
	}

	private TableView getSelectedTableView(){
		if (cmbItems.getValue().equalsIgnoreCase("Links")){
			return this.tblLinks;
		}else if (cmbItems.getValue().equalsIgnoreCase("Categories")){
			return this.tblCategories;
		}
		return this.tblTags;
	}

	private void updateStatusBar(boolean isSearch){
		this.lblStatusItemCountText.setText(cmbItems.getValue());
		int itemSize;
		if (isSearch){
			itemSize = getSelectedTableView().getItems().size();
		}else{
			itemSize = getSelectedTableView().getItems().size() - 1;
		}
		this.lblStatusItemCount.setText(Integer.toString(itemSize));
	}

	private boolean insertOrUpdateLink(Link link){
		boolean isCorrect = true;
		
		int id = link.getId(); 
		
		if (id == -1){
			try {
				Category category = cmbCategories.getValue();
				if (!category.getName().equalsIgnoreCase(ValueConstants.VALUE_ALL)
						&& !category.getName().equalsIgnoreCase(ValueConstants.VALUE_N_A)) {
					link.setCategory(category);
				}
				this.linkHandler.createLink(link);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
				alert.showAndWait();
				isCorrect = false;
			}
		}else{
			try {
				Category category = cmbCategories.getValue();
				if (category.getName().equalsIgnoreCase(ValueConstants.VALUE_N_A)) {
					link.setCategory(null);
				}
				linkHandler.updateLink(link);
			} catch (ParseException | SQLException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
				alert.showAndWait();
				isCorrect = false;
			}
		}
		return isCorrect;
	}
	
	private void refreshLinkTable(){

		if (isSearchPaneVisible() && isSearchTermGiven()) {
			runSearch();
		}else{
			tblLinks.setItems(FXCollections.observableList(this.linkHandler.getLinksByCategory(cmbCategories.getValue())));
			tblLinks.getItems().add(LinkHandler.createPseudoLink());
			this.updateStatusBar(false);
		}
	}

	private void refreshSearchResult(List<Link> links){
		tblLinks.setItems(FXCollections.observableList(links));
		this.updateStatusBar(true);
	}
	
	private void refreshCategoryTable(Category category){
		boolean isCorrect = true;
		int id = -1; 
		if (category != null){
			id = category.getId();
			
			if (id == -1){
				try {
					categoryHandler.createCategory(category);
				} catch (SQLException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "The category MUST be unique!", ButtonType.OK);
					alert.showAndWait();
		    		isCorrect = false;
				}
			}else{
				try {
					categoryHandler.updateCategory(category);
				} catch (ParseException | SQLException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "The category MUST be unique!", ButtonType.OK);
					alert.showAndWait();
		    		isCorrect = false;
				}
			}
		}
		
		if (isCorrect){
			tblCategories.setItems(FXCollections.observableList(categoryHandler.getCategories()));
	    	tblCategories.getItems().add(CategoryHandler.createPseudoCategory(ValueConstants.VALUE_NEW));
			this.updateStatusBar(false);
			this.loadCategoriesForFilter();
			this.loadCategoriesForMove();
		}
	}
	
	private void refreshTagTable(Tag tag){
		boolean isCorrect = true;
		if (tag!=null){
			int id = -1; 
			if (tag != null){
				id = tag.getId(); 
			}

			if (id == -1){
				try {
					TagHandler.createTag(tag);
				} catch (SQLException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "The tag MUST be unique!", ButtonType.OK);
					alert.showAndWait();
		    		isCorrect = false;
				}
			}else{
				try {
					TagHandler.updateTag(tag);
				} catch (ParseException | SQLException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "The tag MUST be unique!", ButtonType.OK);
					alert.showAndWait();
		    		isCorrect = false;
				}
			}
		}

		if (isCorrect){
			tblTags.setItems(FXCollections.observableList(TagHandler.getTags()));
	    	tblTags.getItems().add(TagHandler.createPseudoTag());
			this.updateStatusBar(false);
		}
	}
	
	private void filterCategories(){
		if (!cmbCategories.isDisabled() && cmbCategories.getValue() != null) {
			tblLinks.setItems(FXCollections.observableList(this.linkHandler.getLinksByCategory(cmbCategories.getValue())));
			tblLinks.getItems().add(LinkHandler.createPseudoLink());
			this.updateStatusBar(false);
		}
	}
	
	public void switchTableView(String item){
		if (tblLinks == null &&  tblCategories == null){
			return;
		}
		
		this.btnShowSearchPane.setText(this.getSearchPaneTitle());
		this.btnCreateItem.setDisable(true);
		this.btnDeleteLinks.setDisable(true);
		this.cmbMoveToCategory.setDisable(true);
		this.btnMoveToCategory.setDisable(true);
		this.btnGenerateTitle.setDisable(true);
		this.cmbCategories.setDisable(true);
		this.btnSelectAll.setDisable(true);
		this.btnDeselectAll.setDisable(true);
		this.updateStatusBar(false);
		if (item.equals("Links")){
			this.flowGeneral.getChildren().remove(this.tblCategories);
			this.flowGeneral.getChildren().remove(this.tblTags);
			if (this.isSearchPaneVisible()){
				this.flowGeneral.getChildren().add(4, this.tblLinks);
			}else{
				this.flowGeneral.getChildren().add(3, this.tblLinks);
			}

			tblLinks.setItems(FXCollections.observableList(this.linkHandler.getLinksByCategory(cmbCategories.getValue())));
			tblLinks.getItems().add(LinkHandler.createPseudoLink());
			this.btnShowSearchPane.setDisable(false);
			this.btnCreateItem.setDisable(false);
			this.btnDeleteLinks.setDisable(false);
			this.cmbMoveToCategory.setDisable(false);
			this.btnMoveToCategory.setDisable(false);
			this.btnGenerateTitle.setDisable(false);
			this.cmbCategories.setDisable(false);
			this.btnSelectAll.setDisable(false);
			this.btnDeselectAll.setDisable(false);
		}else if (item.equals("Categories")){
			this.removeSearchPane();
			this.flowGeneral.getChildren().remove(this.tblLinks);
			this.flowGeneral.getChildren().remove(this.tblTags);
			this.flowGeneral.getChildren().add(3, this.tblCategories);
			tblCategories.setItems(FXCollections.observableList(categoryHandler.getCategories()));
			tblCategories.getItems().add(CategoryHandler.createPseudoCategory(ValueConstants.VALUE_NEW));
			this.btnShowSearchPane.setText(this.getSearchPaneTitle());
			this.btnShowSearchPane.setDisable(true);
			this.btnCreateItem.setDisable(false);
		}else if (item.equals("Tags")){
			this.removeSearchPane();
			this.flowGeneral.getChildren().remove(this.tblLinks);
			this.flowGeneral.getChildren().remove(this.tblCategories);
			this.flowGeneral.getChildren().add(3, this.tblTags);
			tblTags.setItems(FXCollections.observableList(TagHandler.getTags()));
			tblTags.getItems().add(TagHandler.createPseudoTag());
			this.btnShowSearchPane.setText(this.getSearchPaneTitle());
			this.btnShowSearchPane.setDisable(true);
		}
	}
	
	private boolean isLinkInformationCorrect(String url){
		if ((url.length() == 0) || (url.equalsIgnoreCase(ValueConstants.VALUE_NEW) || (url.equalsIgnoreCase(ValueConstants.VALUE_N_A)))){
			Alert alert = new Alert(Alert.AlertType.ERROR, "The URL MUST be unique!", ButtonType.OK);
			alert.showAndWait();
    		return false;
		}
		return true;
	}
	
	private boolean isURLCorrect(String link){
		if (!URLValidator.isValidURL(link)){
			Alert alert = new Alert(Alert.AlertType.ERROR, "The URL is incorrect!", ButtonType.OK);
			alert.showAndWait();
    		return false;
		}
		return true;
	}	
	
	private boolean isCategoryInformationCorrect(Category category){
		if ((category.getName().length() == 0) || (category.getName().equalsIgnoreCase(ValueConstants.VALUE_NEW)) || (category.getName().equalsIgnoreCase(ValueConstants.VALUE_N_A))){
			Alert alert = new Alert(Alert.AlertType.ERROR, "The category MUST be unique!", ButtonType.OK);
			alert.showAndWait();
    		return false;
		}
		return true;
	}
	
	private boolean isCategoryNameCorrect(String url){
		if ((url.length() == 0) || (url.equalsIgnoreCase(ValueConstants.VALUE_NEW) || (url.equalsIgnoreCase(ValueConstants.VALUE_N_A)))){
			Alert alert = new Alert(Alert.AlertType.ERROR, "The category MUST be unique!", ButtonType.OK);
			alert.showAndWait();
    		return false;
		}
		return true;
	}
	
	private boolean isTagInformationCorrect(Tag tag){
		if ((tag.getName().length() == 0) || (tag.getName().equalsIgnoreCase(ValueConstants.VALUE_NEW)) || (tag.getName().equalsIgnoreCase(ValueConstants.VALUE_N_A))){
			Alert alert = new Alert(Alert.AlertType.ERROR, "The tag MUST be unique!", ButtonType.OK);
			alert.showAndWait();
    		return false;
		}
		return true;
	}
	
	private boolean isTagNameCorrect(String url){
		if ((url.length() == 0) || (url.equalsIgnoreCase(ValueConstants.VALUE_NEW) || (url.equalsIgnoreCase(ValueConstants.VALUE_N_A)))){
			Alert alert = new Alert(Alert.AlertType.ERROR, "The tag MUST be unique!", ButtonType.OK);
			alert.showAndWait();
    		return false;
		}
		return true;
	}
}