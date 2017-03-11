package se.mbaeumer.fxlink.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ImportResultReportStage extends Stage {
	private Scene scene;
	private FlowPane flowGeneral;
	private FlowPane flowImportInfo;
	private Label lblImportFileText = new Label("Imported from");
	private Label lblImportFileName = new Label();
	private Label lblSuccessfulImportsText = new Label("Successful imports");
	private Label lblSuccessfulImportsValue = new Label();
	private Label lblFailedImportsText = new Label("Failed imports");
	private Label lblFailedImportsValue = new Label();
	private TabPane tabPane;
	private Tab tabSuccess;
	private Tab tabFailed;
	private TableView tvSuccessfulLinks;
	private TableView tvFailedLinks;
	private Button btnClose;
	private ImportResultReport importReport;
	
	public ImportResultReportStage(ImportResultReport report){
		super();
		this.initRootPane();
		this.initScene();
		
		this.importReport = report;
		this.flowGeneral.prefHeightProperty().bind(this.scene.heightProperty());
		this.flowGeneral.prefWidthProperty().bind(this.scene.widthProperty());
		this.initLayout();
	}
	
	private void initScene(){
		int width = 500;
		int height = 750;
		this.scene = new Scene(this.flowGeneral, width, height);
		this.scene.setFill(Color.WHITESMOKE);
		
		this.makeModal();
		
		this.setTitle("Import results");
		this.setScene(this.scene);
	}
	
	private void makeModal(){
		this.initModality(Modality.APPLICATION_MODAL);
		this.initStyle(StageStyle.UTILITY);
		this.setResizable(false);
	}
	
	private void initRootPane(){
		this.flowGeneral = new FlowPane();
		this.flowGeneral.setOrientation(Orientation.HORIZONTAL);
		this.flowGeneral.setHgap(10);
		this.flowGeneral.setVgap(10);
		this.flowGeneral.setPadding(new Insets(0, 10, 0, 10));
		this.flowGeneral.setVgap(10);
	}
	
	public void initLayout(){
		this.initPanes();
		this.initImportInfoLabels();
		this.initTabPane();
		this.initSuccessLinkTableView();
		this.initFailedLinksTableView();
		this.initCloseButton();
	}
	
	public void initPanes(){
		this.initImportInfoPane();
	}
	
	private void initImportInfoPane(){
		this.flowImportInfo = new FlowPane(Orientation.VERTICAL);
		this.flowImportInfo.setHgap(10);
		this.flowImportInfo.setVgap(5);
		this.flowImportInfo.setPadding(new Insets(0, 10, 0, 10));
	}
	
	private void initImportInfoLabels(){
		this.lblImportFileName.setText(this.importReport.getFilename());
		
		this.lblSuccessfulImportsValue.setText(new Integer(this.importReport.getSuccessfulLinks().size()).toString());
		this.lblFailedImportsValue.setText(new Integer(this.importReport.getFailedLinks().size()).toString());
		this.flowGeneral.getChildren().addAll(this.lblImportFileText, this.lblImportFileName, this.lblSuccessfulImportsText, 
				this.lblSuccessfulImportsValue, this.lblFailedImportsText, this.lblFailedImportsValue);
	}

	private void initTabPane(){
		this.createTabPane();
		this.createTabs();
		this.flowGeneral.getChildren().add(this.tabPane);
	}

	private void createTabPane(){
		this.tabPane = new TabPane();
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
		this.tvSuccessfulLinks.prefWidthProperty().bind(this.flowGeneral.prefWidthProperty());
		this.tabSuccess.setContent(this.tvSuccessfulLinks);
		this.initSuccessLinksTableLayout();
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

		// create the url column
		TableColumn urlCol = new TableColumn("Url");
		urlCol.setCellValueFactory(new PropertyValueFactory("url"));
		urlCol.setCellFactory(TextFieldTableCell.forTableColumn());
		urlCol.setEditable(false);
		/*
		urlCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<Link, String>>() {
					public void handle(TableColumn.CellEditEvent<Link, String> t) {
						if (isLinkInformationCorrect(t.getNewValue()) && isURLCorrect(t.getNewValue())){
							((Link) t.getTableView().getItems().get(
									t.getTablePosition().getRow())
							).setURL(t.getNewValue());
							if (insertOrUpdateLink(t.getRowValue())){
								System.out.println("Everything is ok...success!");
								refreshLinkTable();
							}
						}
					}
				}
		);
		*/

		// create title column
		TableColumn titleCol = new TableColumn("Title");
		titleCol.setCellValueFactory(new PropertyValueFactory("title"));
		titleCol.setCellFactory(TextFieldTableCell.forTableColumn());
		titleCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<Link, String>>() {
					public void handle(TableColumn.CellEditEvent<Link, String> t) {
						Link link = t.getRowValue();
						link.setTitle(t.getNewValue());
						/*
						if (isLinkInformationCorrect(link.getURL())){
							if (insertOrUpdateLink(link)){
								refreshLinkTable();
							}
						}
						*/
					}
				}
		);

		// create description column
		TableColumn descriptionCol = new TableColumn("Description");
		descriptionCol.setCellValueFactory(new PropertyValueFactory("description"));
		descriptionCol.setCellFactory(TextFieldTableCell.forTableColumn());
		descriptionCol.setOnEditCommit(
				new EventHandler<TableColumn.CellEditEvent<Link, String>>() {
					public void handle(TableColumn.CellEditEvent<Link, String> t) {
						Link link = t.getRowValue();
						link.setDescription(t.getNewValue());
						/*
						if (isLinkInformationCorrect(link.getURL())){
							if (insertOrUpdateLink(link)){
								refreshLinkTable();
							}
						}
						*/
					}
				}
		);

		// create the created column
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


		this.tvSuccessfulLinks.getColumns().addAll(selectedCol, titleCol, urlCol, descriptionCol, createdCol);
	}

	private void initSuccessLinksTableLayout(){
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
		this.tvFailedLinks.prefWidthProperty().bind(this.flowGeneral.prefWidthProperty());
		this.initFailedLinksTableLayout();
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
	private void initFailedLinksTableLayout(){
		for (Object o : this.tvFailedLinks.getColumns()){
			TableColumn tc = (TableColumn) o;
			tc.setPrefWidth((this.tvFailedLinks.getPrefWidth()*50)/100);
		}
	}
	
	private void initCloseButton(){
		this.btnClose = new Button("Close");
		
		this.btnClose.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent arg0) {
				close();
			}
		});
		
		this.flowGeneral.getChildren().add(this.btnClose);
	}
}
