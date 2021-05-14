package se.mbaeumer.fxlink.gui;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.*;
import se.mbaeumer.fxlink.api.*;
import se.mbaeumer.fxlink.models.*;
import se.mbaeumer.fxlink.util.*;
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
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class LinkViewDetailStage extends Stage {

	private Group root = new Group();
	private Scene scene;
	private FlowPane flowGeneral;
	private GridPane gridData;
	private Label lblTitle = new Label("Title");	
	private TextField txtTitle;
	private Label lblURL = new Label("URL");	
	private TextField txtURL;
	private Label lblDescription = new Label("Description");
	private TextArea taDescription;
	private Label lblCategories = new Label("Categories");
	private ComboBox<Category> cmbCategories;
	private Label lblSuggestions = new Label("Suggestions");
	private FlowPane flowSuggestions;
	private Label lblCreated = new Label("Created");
	private Label lblCreatedDate;
	private Label lblLastUpdated = new Label("Last updated");
	private Label lblLastUpdatedDate;
	private ListView<SelectableTag> listAllSelectableTags;
	private List<Tag> allTags;
	private List<Tag> existingTagsForLink;
	private List<SelectableTag> selectableTags;
	private ObservableList<SelectableTag> observableSelectableTags;
	private GridPane gridCommands;
	private Button btnSave;
	private Button btnCancel;
	private Button btnGenerateTitle;
	private Link link;
	private boolean isValidationError = false;
	
	public LinkViewDetailStage(Link link){
		super();
		this.link = link;
		
		this.initScene();
		this.makeModal();
		
		this.initLayout();
		this.initTagging();
		this.bindSizes();
		this.initCommandGrid();
		
		this.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent event) {
		    	if (isValidationError){
		    		event.consume();
		    	}
		    }
		});
	}
	
	private void initScene(){
		int width = 600;
		int height = 700;
		this.scene = new Scene(this.root, width, height, Color.WHITESMOKE);
		this.setTitle("Link details");
		this.setScene(this.scene);
	}
	
	private void makeModal(){
		this.initModality(Modality.APPLICATION_MODAL);
		this.initStyle(StageStyle.UTILITY);
		this.setResizable(false);
	}

	private void initLayout(){
		this.initPanes();
		this.initFormContent();
	}
	
	private void initPanes(){
		this.initGeneralFlowPane();
		this.intGridPane();
	}	
	
	private void initGeneralFlowPane(){
		this.flowGeneral = new FlowPane(Orientation.HORIZONTAL);
		this.flowGeneral.setHgap(10);
		this.flowGeneral.setVgap(10);
		this.flowGeneral.setPadding(new Insets(5, 5, 0, 5));
		this.root.getChildren().add(this.flowGeneral);
	}
	
	private void intGridPane(){
		this.gridData = new GridPane();
		gridData.setHgap(10);
	    gridData.setVgap(10);
	    this.flowGeneral.getChildren().add(this.gridData);
	}
	
	private void initFormContent(){
		this.initURL();
		this.initTitle();
		this.initDescription();
		this.initCategory();
		this.initSuggestions();
		this.initCreationDate();
		this.initLastUpdated();
	}

	private void bindSizes(){
		this.flowGeneral.prefWidthProperty().bind(this.scene.widthProperty());
		this.listAllSelectableTags.prefWidthProperty().bind(this.flowGeneral.widthProperty());
	}
	
	private void initURL(){
		this.gridData.add(this.lblURL, 0, 0);		
		this.txtURL = new TextField(this.link.getURL());
		this.gridData.add(this.txtURL, 1, 0);
	}
	
	private void initTitle(){
		this.gridData.add(this.lblTitle, 0, 1);		
		this.txtTitle = new TextField(this.link.getTitle());
		this.gridData.add(this.txtTitle, 1, 1);
	}
	
	private void initDescription(){
		this.gridData.add(this.lblDescription, 0, 2);
		this.taDescription = new TextArea(this.link.getDescription());
		this.taDescription.setPrefRowCount(3);
		this.gridData.add(this.taDescription, 1, 2);
	}
	
	private void initCategory(){
		this.gridData.add(this.lblCategories, 0, 3);
		this.cmbCategories = new ComboBox<Category>();

		ObservableList<Category> categoryList =
	            FXCollections.observableArrayList(CategoryHandler.getCategories());
		categoryList.add(0, CategoryHandler.createPseudoCategory(ValueConstants.VALUE_N_A));
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
		setCategory();

		this.gridData.add(this.cmbCategories, 1,3);		
	}

	private void setCategory() {
		int index = 0;
		if (this.link.getCategory() != null){
			for (Category c : this.cmbCategories.getItems()){
				if (c.getName().equalsIgnoreCase(this.link.getCategory().getName())){
					break;
				}
				index++;
			}
		}else{
			index = 0;
		}
		this.cmbCategories.getSelectionModel().select(index);
	}

	private void initSuggestions(){
		this.gridData.add(this.lblSuggestions, 0, 4);

		this.flowSuggestions = new FlowPane(Orientation.HORIZONTAL);
		this.flowSuggestions.setPadding(new Insets(5, 5, 0, 5));

		List<Suggestion> suggestions = initSuggestionData();


		for (Suggestion suggestion : suggestions){
			Button button = new Button(suggestion.getCategory());
			button.setOnAction(actionEvent -> doSth(actionEvent));
			this.flowSuggestions.getChildren().add(button);

		}

		this.gridData.add(this.flowSuggestions, 1, 4);
	}

	private void doSth(ActionEvent actionEvent){
		try {
			Category category = CategoryHandler.getCategoryByName(((Button)actionEvent.getSource()).getText());
			link.setCategory(category);
			LinkHandler.updateLink(link);
			setCategory();
		} catch (SQLException | ParseException throwables) {
			throwables.printStackTrace();
		}
	}

	private List<Suggestion> initSuggestionData(){
		URLHelper urlHelper = new URLHelper();
		SuggestionDataHandler suggestionDataHandler = new SuggestionDataHandler(urlHelper);
		SuggestionHandler suggestionHandler = new SuggestionHandler(suggestionDataHandler, urlHelper);
		return suggestionHandler.getSuggestions(this.link);
	}
	
	private void initCreationDate(){
		this.gridData.add(this.lblCreated, 0, 5);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		this.lblCreatedDate = new Label(df.format(this.link.getCreated()));
		this.gridData.add(this.lblCreatedDate, 1, 5);
	}
	
	private void initLastUpdated(){
		this.gridData.add(this.lblLastUpdated, 0, 6);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.lblLastUpdatedDate = new Label(df.format(this.link.getLastUpdated()));
		this.gridData.add(this.lblLastUpdatedDate, 1, 6);
	}
	
	private void initTagging(){
		this.populateDataLists();
		this.initTaggingListView();
	}
	
	private void populateDataLists(){
		this.allTags = TagHandler.getTags();
		this.existingTagsForLink = new ArrayList<Tag>();
		this.selectableTags = new ArrayList<SelectableTag>();
		try {
			this.existingTagsForLink = TagHandler.getAllTagsForLink(this.link);
		} catch (SQLException e) {
			// TODO Add alert here
			e.printStackTrace();
		}
		
		for (Tag tagFromAllTags : allTags){
			SelectableTag selectableTag = new SelectableTag();
			selectableTag.setId(tagFromAllTags.getId());
			selectableTag.setSelected(false);
			for (Tag tagFromExistingTags : this.existingTagsForLink){
				if (tagFromAllTags.getId() == tagFromExistingTags.getId()){
					selectableTag.setSelected(true);					
					break;
				}
			}
			selectableTag.setName(tagFromAllTags.getName());
			this.selectableTags.add(selectableTag);
		}
		
		this.observableSelectableTags = FXCollections.observableArrayList(this.selectableTags);
	}
	
	private void initTaggingListView(){
		this.listAllSelectableTags = new ListView<SelectableTag>();
		
		for (final SelectableTag t : this.observableSelectableTags){
			t.selectedProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> arg0,
						Boolean wasSelected, Boolean isSelected) {
					Tag tag = new Tag();
					tag.setId(t.getId());
					if (isSelected){
						try {
							TagHandler.addTagToLink(tag, link);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}else{
						try {
							TagHandler.removeTagToLink(tag, link);
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}

		
		this.listAllSelectableTags.setCellFactory(CheckBoxListCell.forListView(new Callback<SelectableTag, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(SelectableTag item) {
                return item.selectedProperty();
            }
        }, new StringConverter<SelectableTag>() {

			@Override
			public SelectableTag fromString(String arg0) {
				return null;
			}

			@Override
			public String toString(SelectableTag arg0) {
				return arg0.getName();
			}}));

		this.listAllSelectableTags.setItems(this.observableSelectableTags);

		this.flowGeneral.getChildren().add(this.listAllSelectableTags);
	}
	
	private void initCommandGrid(){
		this.gridCommands = new GridPane();
		gridCommands.setHgap(10);
	    gridCommands.setVgap(10);
	    gridCommands.setPadding(new Insets(0, 10, 0, 10));
	    this.flowGeneral.getChildren().add(this.gridCommands);
	    this.initButtons();
	}
	
	private void initButtons(){
		this.initSaveButton();
		this.initCancelButton();
		this.initGenerateTitleButton();
	}
	
	private void initSaveButton(){
		this.btnSave = new Button("Save");
		this.btnSave.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				try {
					isValidationError = false;
					if (processInput()){
					 	close();
					}
				} catch (ParseException | SQLException e) {
					showErrorMessage(e);
					isValidationError = true;
				}
			}
			
		});
		this.gridCommands.add(this.btnSave, 0, 0);
	}

	private void showErrorMessage(final Exception ex){
		Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
		alert.initOwner(this);
		alert.showAndWait();
	}
	
	private void initCancelButton(){
		this.btnCancel = new Button("Cancel");
		this.btnCancel.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				isValidationError = false;
				close();
			}
			
		});
		this.gridCommands.add(this.btnCancel, 1, 0);
	}

	private void initGenerateTitleButton(){
		this.btnGenerateTitle = new Button("Generate title");
		this.btnGenerateTitle.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent arg0) {
				LinkTitleUtil linkTitleUtil = new LinkTitleUtilImpl();
				link.setTitle(linkTitleUtil.generateTitle(link));
				txtTitle.setText(link.getTitle());
			}

		});
		this.gridCommands.add(this.btnGenerateTitle, 2, 0);

	}
	
	private boolean processInput() throws ParseException, SQLException{
		if (!URLValidator.isValidURL(this.txtURL.getText())){
			showAlert();
			isValidationError = true;
			return false;
		}

		Link updatedLink = new Link(this.txtTitle.getText(), this.txtURL.getText(), this.taDescription.getText());
		updatedLink.setCreated(this.link.getCreated());		
		updatedLink.setId(this.link.getId());

		setCategory(updatedLink);
		if (this.link.getId() <= 0) {
			LinkHandler.createLink(updatedLink);
		}else {
			LinkHandler.updateLink(updatedLink);
		}
		return true;
	}

	private void showAlert() {
		Alert alert = new Alert(Alert.AlertType.ERROR, "The URL is incorrect!", ButtonType.OK);
		alert.initOwner(this);
		alert.showAndWait();
	}

	private void setCategory(Link updatedLink) {
		if (this.cmbCategories.getSelectionModel().getSelectedIndex() == 0){
			updatedLink.setCategory(null);
		}else{
			updatedLink.setCategory(this.cmbCategories.getValue());
		}
	}

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}
}