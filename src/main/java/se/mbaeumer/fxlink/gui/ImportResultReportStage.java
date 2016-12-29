package se.mbaeumer.fxlink.gui;

import se.mbaeumer.fxlink.models.FailedLink;
import se.mbaeumer.fxlink.models.ImportResultReport;
import se.mbaeumer.fxlink.models.Link;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

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
	private TableView tvLinks;
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
		int height = 600;
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
		this.initTableView();
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
		//this.flowGeneral.getChildren().add(this.flowImportInfo);
	}
	
	private void initImportInfoLabels(){
		this.lblImportFileName.setText(this.importReport.getFilename());
		
		this.lblSuccessfulImportsValue.setText(new Integer(this.importReport.getSuccessfulLinks().size()).toString());
		this.lblFailedImportsValue.setText(new Integer(this.importReport.getFailedLinks().size()).toString());
		this.flowGeneral.getChildren().addAll(this.lblImportFileText, this.lblImportFileName, this.lblSuccessfulImportsText, 
				this.lblSuccessfulImportsValue, this.lblFailedImportsText, this.lblFailedImportsValue);
	}
	
	private void initTableView(){
		this.createTableView();
		this.createTableViewColumns();
		this.flowGeneral.getChildren().add(this.tvLinks);
		this.tvLinks.prefWidthProperty().bind(this.flowGeneral.prefWidthProperty());
		this.initTableLayout();
	}
	
	@SuppressWarnings({ "unchecked" })
	private void createTableView(){
		this.tvLinks = new TableView<FailedLink>();
		this.tvLinks.setItems(FXCollections.observableList(this.importReport.getFailedLinks()));
		this.tvLinks.setEditable(false);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createTableViewColumns(){
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
		
		this.tvLinks.getColumns().addAll(urlCol, causeCol);
	}
	
	@SuppressWarnings("rawtypes")
	private void initTableLayout(){
		for (Object o : this.tvLinks.getColumns()){
			TableColumn tc = (TableColumn) o;
			tc.setPrefWidth((this.tvLinks.getPrefWidth()*50)/100);
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
