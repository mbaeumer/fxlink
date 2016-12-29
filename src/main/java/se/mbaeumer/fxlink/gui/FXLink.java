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
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.models.ImportResultReport;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.models.Tag;
import se.mbaeumer.fxlink.util.BrowserLauncher;
import se.mbaeumer.fxlink.util.URLValidator;
import se.mbaeumer.fxlink.util.ValueConstants;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
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
	private Group root = new Group();
	private Scene scene;
	private FlowPane flowGeneral;
	private FlowPane flowFilter;
	private Label lblItems;
	private ComboBox<String> cmbItems;
	private Label lblCategories;
	private ComboBox<Category> cmbCategories;
	private Button btnResetFilter;
	private Button btnImportTextFile;
	private Button btnShowSearchPane;
	
	private FlowPane flowSearch;
	private Button btnSearch;
	private Label lblSearchTerm;
	private TextField tfSearchTerm;
	private CheckBox chkSearchURL;
	private CheckBox chkSearchTitle;
	private CheckBox chkSearchDescription;
	private ComboBox<Category> cmbSearchCategory;
	
	private TableView<Link> tblLinks;
	private TableView<Category> tblCategories;
	private TableView<Tag> tblTags;
	
	private Button btnWriteBackup;
	
	private Button btnReadBackup;
	
	private ContextMenu contLinks;
	private Link selectedLink = null;
	
	private ContextMenu contTags;
	private Tag selectedTag = null;
	
	private ContextMenu contCategories;
	private Category selectedCategory = null;
	
	public void start(Stage stage) {
		this.scene = new Scene(this.root, 1000, 700, Color.WHITESMOKE);

		// set the stage
		stage.setTitle("FX Link");
		stage.setScene(this.scene);
		stage.show();
		this.initLayout();
		
		
		
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	public void initLayout() {
		// create the general flow pane
		this.createGeneralFlowPane();
		//this.createMenu();
		this.createFilterFlowPane();
		
		// create the table views
		this.createLinkTableView();
		this.createCategoryTableView();
		this.createTagTableView();
	}
	
	public void createGeneralFlowPane() {
		this.flowGeneral = new FlowPane();
		this.flowGeneral.setOrientation(Orientation.VERTICAL);
		this.flowGeneral.setPrefWrapLength(700);
		this.flowGeneral.setVgap(10);
		this.root.getChildren().add(this.flowGeneral);
	}
	
	public void createFilterFlowPane(){
		this.flowFilter = new FlowPane();
		this.flowFilter.setOrientation(Orientation.HORIZONTAL);
		this.flowFilter.setHgap(10);
		this.flowGeneral.getChildren().add(this.flowFilter);
		FlowPane.setMargin(flowFilter, new Insets(5));
		this.createItemLabel();
		this.createItemComboBox();
		this.createCategoryLabel();
		this.createCategoryComboBox();
		this.createResetButton();
		this.createImportButton();
		this.createWriteBackupButton();
		this.createReadBackupButton();
		this.createShowSearchPaneButton();
	}
	
	public void createItemLabel(){
		this.lblItems = new Label("Items");
		this.flowFilter.getChildren().add(this.lblItems);
	}
	
	private void createItemComboBox(){
		this.cmbItems = new ComboBox<String>();
		List<String> items = new ArrayList<String>();
		items = ManagedItemHandler.getManagedItems();		
		this.cmbItems.setItems(FXCollections.observableArrayList(items));
		this.cmbItems.valueProperty().addListener(
				new ChangeListener<String>(){
					@SuppressWarnings("rawtypes")
					@Override
					public void changed(ObservableValue ov, String s1, String s2){
						switchTableView(cmbItems.getValue());
						//cmbCategories.setDisable(true);
						//btnResetFilter.setDisable(true);
						
						/*
						 * cmbContexts.setDisable(true);
						cmbTaskViews.setDisable(true);
						cmbStatuses.setDisable(true);
						cmbPriorities.setDisable(true);
						btnResetFilter.setDisable(true);
						if (cmbItems.getValue().equals("Task")){
							cmbContexts.setDisable(false);
							cmbTaskViews.setDisable(false);
							cmbStatuses.setDisable(false);
							cmbPriorities.setDisable(false);
							btnResetFilter.setDisable(false);
							resetTaskSorting(tblLinks);
						}else{
							resetTaskSorting(tblContexts);
						}
						switchTableView(cmbItems.getValue());
						*/
					}
				});
		
		this.cmbItems.getSelectionModel().selectFirst();

		this.flowFilter.getChildren().add(this.cmbItems);

	}
	
	private void createCategoryLabel(){
		this.lblCategories = new Label("Categories");
		this.flowFilter.getChildren().add(this.lblCategories);
	}
	
	private void createCategoryComboBox(){
		this.cmbCategories = new ComboBox<Category>();
		
		// get the categories
		ObservableList<Category> categoryList =
	            FXCollections.observableArrayList(CategoryHandler.getCategories());
		categoryList.add(0, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_ALL));
		categoryList.add(1, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_N_A));
		
		this.cmbCategories.setItems(categoryList);
		
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
		this.cmbCategories.valueProperty().addListener(
				new ChangeListener<Category>(){
					@SuppressWarnings("rawtypes")
					@Override
					public void changed(ObservableValue ov, Category c1, Category c2){
						Category category = cmbCategories.getValue();
                        filterCategories(category);             
					}
				});
		 
		this.flowFilter.getChildren().add(this.cmbCategories);
	}
	
	private void createResetButton(){
		btnResetFilter = new Button("Reset");
		this.btnResetFilter.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				cmbCategories.getSelectionModel().selectFirst();
				/*
				cmbContexts.setValue(ContextConstants.CONTEXT_ALL);
				cmbTaskViews.setValue(FilterConstants.FILTER_NONE);
				cmbStatuses.setValue(FilterConstants.FILTER_NONE);
				cmbPriorities.setValue(FilterConstants.FILTER_NONE);
				tblLinks.setItems(FXCollections.observableList(fxtodoMain.getAllTasks()));
				tblLinks.getItems().add(fxtodoMain.getDummyTask());
				*/
			}
		
		});
		this.flowFilter.getChildren().add(this.btnResetFilter);
	}

	private void createImportButton(){
		btnImportTextFile = new Button("Import text file");
		this.btnImportTextFile.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().add(
						new ExtensionFilter("txt", "*.txt"));
				File importFile= fc.showOpenDialog(null);
				if (importFile == null){
					arg0.consume();
					return;
				}

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
		});
		this.flowFilter.getChildren().add(this.btnImportTextFile);
	}
	
	private void createWriteBackupButton(){
		btnWriteBackup = new Button("Save backup");
		this.btnWriteBackup.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
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
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (XMLStreamException e1) {
					e1.printStackTrace();
				} catch (SQLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		this.flowFilter.getChildren().add(this.btnWriteBackup);

	}
	
	private void createReadBackupButton(){
		this.btnReadBackup = new Button("Import backup");
		this.btnReadBackup.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				FileChooser fc = new FileChooser();
				fc.getExtensionFilters().add(
						new ExtensionFilter("XML", "*.xml"));
				File importFile = fc.showOpenDialog(null);
				
				if (importFile == null){
					arg0.consume();
					return;
				}
				
				// ask the use to confirm that the database content 
				// will be overwritten now
				Alert alert = new Alert(Alert.AlertType.WARNING, "The current content will be overwritten. Continue?", ButtonType.YES, ButtonType.NO);
				Optional<ButtonType> result = alert.showAndWait();

				XMLImportHandler xmlImportHandler = new XMLImportHandler();
				if (result.isPresent() && result.get() == ButtonType.YES) {
					try {
						xmlImportHandler.readData(importFile.getCanonicalPath());
						xmlImportHandler.truncateDatabase();
						xmlImportHandler.importData();
						cmbItems.setValue("Links");
						refreshLinkTable();
					} catch (FileNotFoundException | XMLStreamException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}else{
					arg0.consume();
					return;
				}
			}
		});
		this.flowFilter.getChildren().add(this.btnReadBackup);
	}
	
	private void createShowSearchPaneButton(){
		this.btnShowSearchPane = new Button(getSearchPaneTitle());
		
		this.btnShowSearchPane.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (isSearchPaneVisible()){
					removeSearchPane();
					refreshLinkTable();
				}else{
					createSearchPane();
				}
				btnShowSearchPane.setText(getSearchPaneTitle());
				
			}
		});
		this.flowFilter.getChildren().add(this.btnShowSearchPane);
		
	}
	
	private String getSearchPaneTitle(){
		String title = "Show search";
		if (this.isSearchPaneVisible()){
			title = "Hide search";
		}
		return title;
	}
	
	private void createSearchPane(){
		this.flowSearch = new FlowPane();
		this.flowSearch.setOrientation(Orientation.HORIZONTAL);
		this.flowSearch.setHgap(10);
		
		this.showSearchPane();
		FlowPane.setMargin(flowSearch, new Insets(5));
		
		this.createSearchTermLabel();
		this.createSearchTermTextField();
		this.createURLSearchCheckBox();
		this.createTitleSearchCheckBox();
		this.createDescriptionSearchCheckBox();
		this.createSearchButton();
	}
	
	private void createSearchTermLabel(){
		this.lblSearchTerm = new Label("Search term");
		this.flowSearch.getChildren().add(this.lblSearchTerm);
	}
	
	private void createSearchTermTextField(){
		this.tfSearchTerm = new TextField();
		this.flowSearch.getChildren().add(this.tfSearchTerm);
	}
	
	private void createURLSearchCheckBox(){
		this.chkSearchURL = new CheckBox("URL");
		this.chkSearchURL.setSelected(true);
		// TODO: add event handler
		this.flowSearch.getChildren().add(this.chkSearchURL);
	}
	
	private void createTitleSearchCheckBox(){
		this.chkSearchTitle= new CheckBox("title");
		// TODO: add event handler
		this.flowSearch.getChildren().add(this.chkSearchTitle);
	}

	private void createDescriptionSearchCheckBox(){
		this.chkSearchDescription = new CheckBox("description");
		// TODO: add event handler
		this.flowSearch.getChildren().add(this.chkSearchDescription);
	}
	
	private void createSearchButton(){
		this.btnSearch = new Button("Search");
		// TODO: add event handler
		this.btnSearch.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				if (isSearchTermGiven() && isCriteriaSelected()){
					try {
						List<Link> links = SearchHandler.findLinks(tfSearchTerm.getText(), chkSearchURL.isSelected(),
								chkSearchTitle.isSelected(), chkSearchDescription.isSelected());
						refreshSearchResult(links);
					} catch (SQLException e) {
						Alert alert = new Alert(Alert.AlertType.ERROR, "Database error occurred", ButtonType.OK);
						alert.showAndWait();
					}
				}else{
					Alert alert = new Alert(Alert.AlertType.ERROR, "Please write a search term and select at least one criteria", ButtonType.OK);
					alert.showAndWait();
				}
			}
		});
		this.flowSearch.getChildren().add(this.btnSearch);
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
		this.flowGeneral.getChildren().add(1, this.flowSearch);
	}
	
	private void removeSearchPane(){
		if (this.isSearchPaneVisible()){
			this.flowGeneral.getChildren().remove(1);
		}
	}
	
	private boolean isSearchPaneVisible(){
		return this.flowGeneral.getChildren().contains(this.flowSearch);
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createLinkTableView(){
		// create the table view itself
		this.tblLinks = new TableView();
	
		this.tblLinks.prefWidthProperty().bind(this.scene.widthProperty());
		this.tblLinks.prefWidthProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue ov, Object oldValue, Object newValue){
				setLinkTableLayout();
			}
		});
		// bind the tasks to the table view
		this.tblLinks.setItems(FXCollections.observableList(LinkHandler.getLinks()));
		this.tblLinks.getItems().add(LinkHandler.createPseudoLink());
		// create the columns
		this.createLinkTableColumns();
		this.tblLinks.setEditable(true);
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
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createLinkTableColumns(){
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
							System.out.println("Everything is ok...success!");
							refreshLinkTable();
						}
					}
		        }
		    }
		);
		
		// create title column
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
		
		TableColumn lastUpdatedCol = new TableColumn("Last modified");
		lastUpdatedCol.setCellValueFactory(new PropertyValueFactory("lastUpdated"));
		lastUpdatedCol.setCellValueFactory(
				   new Callback<TableColumn.CellDataFeatures<Link, String>, ObservableValue<String>>() {
					      @Override
					      public ObservableValue<String> call(TableColumn.CellDataFeatures<Link, String> film) {
					         SimpleStringProperty property = new SimpleStringProperty();
					         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					         property.setValue(dateFormat.format(film.getValue().getLastUpdated()));
					         return property;
					      }
					   });

		
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
	                        //Label categoryLabel = new Label(item.getName());
	                        //setGraphic(categoryLabel);
	                    }else{
	                    	setText(null);
	                    }
	                }                    
	            };               
	            return categoryCell;                
	        }
	    });

		// add all columns to the table view
		this.tblLinks.getColumns().addAll(urlCol, titleCol, descriptionCol, createdCol, lastUpdatedCol, categoryCol);
	}
	
	@SuppressWarnings("rawtypes")
	private void setLinkTableLayout(){
		for (Object o : this.tblLinks.getColumns()){
			TableColumn tc = (TableColumn) o;
			tc.setPrefWidth((this.tblLinks.getPrefWidth()*17)/100);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createCategoryTableView(){
		// create the table view itself
		this.tblCategories = new TableView<Category>();
	
		this.tblCategories.prefWidthProperty().bind(this.scene.widthProperty());
		this.tblCategories.prefWidthProperty().addListener(new ChangeListener() {
			@Override
			public void changed(ObservableValue ov, Object oldValue, Object newValue){
				setCategoryTableLayout();
			}
		});
		// bind the tasks to the table view
		this.tblCategories.setItems(FXCollections.observableList(CategoryHandler.getCategories()));
		this.tblCategories.getItems().add(CategoryHandler.createPseudoCategory(ValueConstants.VALUE_NEW));
		// create the columns
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
					      public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> film) {
					         SimpleStringProperty property = new SimpleStringProperty();
					         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					         property.setValue(dateFormat.format(film.getValue().getCreated()));
					         return property;
					      }
					   });

		TableColumn<Category, String> lastUpdatedCol = new TableColumn<Category, String>("Last modified");
		lastUpdatedCol.setCellValueFactory(new PropertyValueFactory<Category, String>("lastUpdated"));
		lastUpdatedCol.setCellValueFactory(
				   new Callback<TableColumn.CellDataFeatures<Category, String>, ObservableValue<String>>() {
					      @Override
					      public ObservableValue<String> call(TableColumn.CellDataFeatures<Category, String> film) {
					         SimpleStringProperty property = new SimpleStringProperty();
					         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					         property.setValue(dateFormat.format(film.getValue().getLastUpdated()));
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
			@SuppressWarnings("rawtypes")
			@Override
			public void changed(ObservableValue ov, Object oldValue, Object newValue){
				setTagTableLayout();
			}
		});
		// bind the tasks to the table view
		this.tblTags.setItems(FXCollections.observableList(TagHandler.getTags()));
		this.tblTags.getItems().add(TagHandler.createPseudoTag());
		// create the columns
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
					      public ObservableValue<String> call(TableColumn.CellDataFeatures<Tag, String> film) {
					         SimpleStringProperty property = new SimpleStringProperty();
					         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					         property.setValue(dateFormat.format(film.getValue().getCreated()));
					         return property;
					      }
					   });

		TableColumn<Tag, String> lastUpdatedCol = new TableColumn<Tag, String>("Last modified");
		lastUpdatedCol.setCellValueFactory(new PropertyValueFactory<Tag, String>("lastUpdated"));
		lastUpdatedCol.setCellValueFactory(
				   new Callback<TableColumn.CellDataFeatures<Tag, String>, ObservableValue<String>>() {
					      @Override
					      public ObservableValue<String> call(TableColumn.CellDataFeatures<Tag, String> film) {
					         SimpleStringProperty property = new SimpleStringProperty();
					         DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					         property.setValue(dateFormat.format(film.getValue().getLastUpdated()));
					         return property;
					      }
					   });
								
		// add all columns to the table view
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
		miEdit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				LinkViewDetailStage linkDetail = new LinkViewDetailStage(selectedLink);
				linkDetail.showAndWait();			
				refreshLinkTable();
			}
		});

		// open URL
		MenuItem miOpenURL = new MenuItem("Open link");
		miOpenURL.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				try {					
					BrowserLauncher.openURL(selectedLink.getURL());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		

		// delete
		MenuItem miDelete = new MenuItem("Delete link");
		miDelete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				try {
					Alert alert = new Alert(Alert.AlertType.WARNING, "The selected link will be deleted. Continue?", ButtonType.YES, ButtonType.NO);
					Optional<ButtonType> result = alert.showAndWait();

					if (result.isPresent() && result.get() == ButtonType.YES){
						LinkHandler.deleteLink(selectedLink);
						refreshLinkTable();
					}

				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		
		this.contLinks.getItems().addAll(miEdit, miOpenURL, miDelete);
	}

	private void createTagContextMenu(){
		this.contTags = new ContextMenu();
		
		// edit
		MenuItem miEdit = new MenuItem("Edit link");
		miEdit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				LinkViewDetailStage linkDetail = new LinkViewDetailStage(selectedLink);
				linkDetail.showAndWait();			
				//refreshLinkTable(null);
				refreshLinkTable();
			}
		});

		// open URL
		MenuItem miOpenURL = new MenuItem("Open link");
		miOpenURL.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				try {					
					BrowserLauncher.openURL(selectedLink.getURL());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		

		// delete
		MenuItem miDelete = new MenuItem("Delete tag");
		miDelete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				try {
					List<Link> links = LinkHandler.getLinksWithTag(selectedTag.getId());
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
			}
		});

		
		this.contTags.getItems().addAll(miDelete);
	}
	
	private void createCategoryContextMenu(){
		this.contCategories = new ContextMenu();

		// move
		MenuItem miMove = new MenuItem("Move to another category");
		miMove.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				MoveCategoryStage mergeStage = new MoveCategoryStage(selectedCategory);
				mergeStage.showAndWait();
				refreshCategoryTable(null);
			}
		});

		// delete
		MenuItem miDelete = new MenuItem("Delete category");
		miDelete.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				try {
					List<Link> links = LinkHandler.getLinksByCategory(selectedCategory);
					if (links.size() > 0){
						Alert alert = new Alert(Alert.AlertType.WARNING, "This category is contains " + links.size() + " links. Do you really want to delete this category?", ButtonType.YES, ButtonType.NO);
						Optional<ButtonType> result = alert.showAndWait();

						if (result.isPresent() && result.get() == ButtonType.YES){
							CategoryHandler.deleteCategory(selectedCategory);
						}
					}else{
						CategoryHandler.deleteCategory(selectedCategory);
					}
					
					refreshCategoryTable(null);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});

		this.contCategories.getItems().addAll(miMove, miDelete);
	}
	
	private boolean insertOrUpdateLink(Link link){
		boolean isCorrect = true;
		
		int id = link.getId(); 
		
		if (id == -1){
			try {
				LinkHandler.createLink(link);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				Alert alert = new Alert(Alert.AlertType.ERROR, "The URL MUST be unique!", ButtonType.OK);
				alert.showAndWait();
				isCorrect = false;
			}
		}else{
			try {
				LinkHandler.updateLink(link);
			} catch (ParseException | SQLException e) {
				Alert alert = new Alert(Alert.AlertType.ERROR, "The URL MUST be unique!", ButtonType.OK);
				alert.showAndWait();
				isCorrect = false;
			}
		}
		return isCorrect;
	}
	
	private void refreshLinkTable(){
		tblLinks.setItems(FXCollections.observableList(LinkHandler.getLinks()));
    	tblLinks.getItems().add(LinkHandler.createPseudoLink());        
	}


	private void refreshLinkTable(Link link){
		boolean isCorrect = true;
		if (link != null){
			int id = link.getId(); 

			if (id == -1){
				try {
					LinkHandler.createLink(link);
				} catch (SQLException e) {
					System.out.println(e.getMessage());
					Alert alert = new Alert(Alert.AlertType.ERROR, "The URL MUST be unique!", ButtonType.OK);
					alert.showAndWait();
					isCorrect = false;
				}
			}else{
				try {
					LinkHandler.updateLink(link);
				} catch (ParseException | SQLException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "The URL MUST be unique!", ButtonType.OK);
					alert.showAndWait();
					isCorrect = false;
				}
			}
		}
		
		if (isCorrect){
			tblLinks.setItems(FXCollections.observableList(LinkHandler.getLinks()));
	    	tblLinks.getItems().add(LinkHandler.createPseudoLink());        
		}        
	}
	
	private void refreshSearchResult(List<Link> links){
		tblLinks.setItems(FXCollections.observableList(links));
	}
	
	private void refreshCategoryTable(Category category){
		boolean isCorrect = true;
		int id = -1; 
		if (category != null){
			id = category.getId();
			
			if (id == -1){
				try {
					CategoryHandler.createCategory(category);
				} catch (SQLException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "The category MUST be unique!", ButtonType.OK);
					alert.showAndWait();
		    		isCorrect = false;
				}
			}else{
				try {
					CategoryHandler.updateCategory(category);
				} catch (ParseException | SQLException e) {
					Alert alert = new Alert(Alert.AlertType.ERROR, "The category MUST be unique!", ButtonType.OK);
					alert.showAndWait();
		    		isCorrect = false;
				}
			}
		}
		
		if (isCorrect){
			tblCategories.setItems(FXCollections.observableList(CategoryHandler.getCategories()));
	    	tblCategories.getItems().add(CategoryHandler.createPseudoCategory(ValueConstants.VALUE_NEW));
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
		}
	}
	
	private void filterCategories(Category category){
		tblLinks.setItems(FXCollections.observableList(LinkHandler.getLinksByCategory(category)));
    	tblLinks.getItems().add(LinkHandler.createPseudoLink());
	}
	
	public void switchTableView(String item){
		if (tblLinks == null &&  tblCategories == null){
			return;
		}
		
		this.btnShowSearchPane.setText(this.getSearchPaneTitle());
		if (item.equals("Links")){
			this.flowGeneral.getChildren().remove(this.tblCategories);
			this.flowGeneral.getChildren().remove(this.tblTags);
			this.flowGeneral.getChildren().add(this.tblLinks);
			tblLinks.setItems(FXCollections.observableList(LinkHandler.getLinks()));
			tblLinks.getItems().add(LinkHandler.createPseudoLink());
			this.btnShowSearchPane.setDisable(false);
		}else if (item.equals("Categories")){
			this.flowGeneral.getChildren().remove(this.tblLinks);
			this.flowGeneral.getChildren().remove(this.tblTags);
			this.flowGeneral.getChildren().add(this.tblCategories);
			tblCategories.setItems(FXCollections.observableList(CategoryHandler.getCategories()));
			tblCategories.getItems().add(CategoryHandler.createPseudoCategory(ValueConstants.VALUE_NEW));
			this.removeSearchPane();
			this.btnShowSearchPane.setText(this.getSearchPaneTitle());
			this.btnShowSearchPane.setDisable(true);
		}else if (item.equals("Tags")){
			this.flowGeneral.getChildren().remove(this.tblLinks);
			this.flowGeneral.getChildren().remove(this.tblCategories);
			this.flowGeneral.getChildren().add(this.tblTags);
			tblTags.setItems(FXCollections.observableList(TagHandler.getTags()));
			tblTags.getItems().add(TagHandler.createPseudoTag());
			this.removeSearchPane();
			this.btnShowSearchPane.setText(this.getSearchPaneTitle());
			this.btnShowSearchPane.setDisable(true);
		}
	}
	
	private boolean isLinkInformationCorrect(String url){
		System.out.println("Base check for url...");
		System.out.println(url);
		if ((url.length() == 0) || (url.equalsIgnoreCase(ValueConstants.VALUE_NEW) || (url.equalsIgnoreCase(ValueConstants.VALUE_N_A)))){
			Alert alert = new Alert(Alert.AlertType.ERROR, "The URL MUST be unique!", ButtonType.OK);
			alert.showAndWait();
    		return false;
		}
		System.out.println("url is ok...");
		return true;
	}
	
	private boolean isURLCorrect(String link){
		System.out.println("Checking url...");
		System.out.println(link);
		if (!URLValidator.isValidURL(link)){
			Alert alert = new Alert(Alert.AlertType.ERROR, "The URL is incorrect!", ButtonType.OK);
			alert.showAndWait();
    		return false;
		}
		System.out.println("Url is correct");
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
