package se.mbaeumer.fxlink.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import se.mbaeumer.fxlink.api.CategoryHandler;
import se.mbaeumer.fxlink.api.LinkHandler;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.FailedLink;
import se.mbaeumer.fxlink.models.ImportResultReport;
import se.mbaeumer.fxlink.models.Link;
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
	private Label lblImportFileText = new Label("Imported from");
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
	private Button btnSelectAll;
	private Button btnDeselectAll;
	
	public ImportResultReportStage(ImportResultReport report){
		super();
		this.initRootPane();
		this.initScene();
		
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
		this.initCloseButton();
	}

	private void initSizes(){
		this.tabPane.setPrefWidth(this.flowGeneral.widthProperty().doubleValue()-20);
		this.tvSuccessfulLinks.prefWidthProperty().bind(this.tabPane.prefWidthProperty());
		this.tvFailedLinks.prefWidthProperty().bind(this.tabPane.prefWidthProperty());
		this.lblImportFileName.prefWidthProperty().bind(this.flowGeneral.widthProperty());
		this.flowSelection.prefWidthProperty().bind(this.flowGeneral.widthProperty());
	}
	
	private void initImportInfoLabels(){
		this.lblImportFileName.setText("Imported from " + this.importReport.getFilename());
		
		this.lblSuccessfulImportsValue.setText(new Integer(this.importReport.getSuccessfulLinks().size()).toString());
		this.lblFailedImportsValue.setText(new Integer(this.importReport.getFailedLinks().size()).toString());
		this.flowGeneral.getChildren().addAll(this.lblImportFileName, this.lblSuccessfulImportsText,
				this.lblSuccessfulImportsValue, this.lblFailedImportsText, this.lblFailedImportsValue);
	}

	private void createMoveToCategoryComboBox(){
		this.cmbMoveToCategory = new ComboBox<>();

		ObservableList<Category> categoryList =
				FXCollections.observableArrayList(CategoryHandler.getCategories());
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

		this.btnMoveToCategory.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				Category category = cmbMoveToCategory.getValue();
				for (Link link : getSelectedLinks()){
					link.setCategory(category);
					try {
						LinkHandler.updateLink(link);
					}catch(SQLException | ParseException pe){
						Alert alert = new Alert(Alert.AlertType.ERROR, "The link could not be updated", ButtonType.OK);
						alert.showAndWait();
					}
				}

			}
		});
		this.flowGeneral.getChildren().add(this.btnMoveToCategory);
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
		this.btnSelectAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				for (Link link : importReport.getSuccessfulLinks()){
					link.setSelected(true);
				}
			}
		});

		this.flowSelection.getChildren().add(this.btnSelectAll);
	}

	private void initDeselectAllButton(){
		this.btnDeselectAll = new Button("Deselect all");
		this.btnDeselectAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				for (Link link : importReport.getSuccessfulLinks()){
					link.setSelected(false);
				}
			}
		});

		this.flowSelection.getChildren().add(this.btnDeselectAll);
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

		TableColumn urlCol = new TableColumn("Url");
		urlCol.setCellValueFactory(new PropertyValueFactory("url"));
		urlCol.setCellFactory(TextFieldTableCell.forTableColumn());
		urlCol.setEditable(false);

		// create title column
		TableColumn titleCol = new TableColumn("Title");
		titleCol.setCellValueFactory(new PropertyValueFactory("title"));
		titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
		titleCol.setEditable(false);

		TableColumn descriptionCol = new TableColumn("Description");
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

		TableColumn createdCol = new TableColumn("Created");
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
			LinkHandler.updateLink(link);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void setSuccessLinksTableLayout(){
		((TableColumn)this.tvSuccessfulLinks.getColumns().get(0)).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*10)/100);
		((TableColumn)this.tvSuccessfulLinks.getColumns().get(1)).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*20)/100);
		((TableColumn)this.tvSuccessfulLinks.getColumns().get(2)).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*40)/100);
		((TableColumn)this.tvSuccessfulLinks.getColumns().get(3)).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*20)/100);
		((TableColumn)this.tvSuccessfulLinks.getColumns().get(4)).setPrefWidth((this.tvSuccessfulLinks.getPrefWidth()*10)/100);
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
	
	private void initCloseButton(){
		this.btnClose = new Button("Close");
		this.btnClose.setOnAction((event) -> {close();});
		this.flowGeneral.getChildren().add(this.btnClose);
	}
}
