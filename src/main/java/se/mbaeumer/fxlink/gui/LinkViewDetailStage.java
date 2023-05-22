package se.mbaeumer.fxlink.gui;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import se.mbaeumer.fxlink.api.*;
import se.mbaeumer.fxlink.handlers.*;
import se.mbaeumer.fxlink.models.*;
import se.mbaeumer.fxlink.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

	private final Group root = new Group();
	private Scene scene;
	private FlowPane flowGeneral;
	private GridPane gridData;
	private final Label lblTitle = new Label("Title");
	private TextField txtTitle;
	private final Label lblURL = new Label("URL");
	private TextField txtURL;
	private final Label lblDescription = new Label("Description");
	private TextArea taDescription;
	private final Label lblCategories = new Label("Categories");
	private ComboBox<Category> cmbCategories;
	private final Label lblRank = new Label("Rank");
	private NumberSpinner ntRank;
	private final Label lblSuggestions = new Label("Suggestions");
	private FlowPane flowSuggestions;
	private final Label lblCreated = new Label("Created");
	private Label lblCreatedDate;
	private final Label lblLastUpdated = new Label("Last updated");
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

	private final LinkHandler linkHandler;
	private final CategoryHandler categoryHandler;
	private final TitleHandler titleHandler;
	private final NaiveBayesClassifier naiveBayesClassifier;
	private final FollowUpRankHandler followUpRankHandler;

	private List<Probability> suggestions;

	public LinkViewDetailStage(Link link){
		super();
		this.link = link;

		this.linkHandler = new LinkHandler(new LinkReadDBHandler(), new LinkTagReadDBHandler(),
				new LinkCreationDBHandler(), new LinkUpdateDBHandler(), new LinkDeletionDBHandler(), new FollowUpStatusReadDBHandler());
		this.categoryHandler= new CategoryHandler(new CategoryReadDBHandler(),
				new CategoryCreationDBHandler(), new CategoryUpdateDBHandler(),
				new CategoryDeletionDBHandler(), new LinkUpdateDBHandler());
		this.titleHandler = new TitleHandler(new LinkTitleUtilImpl(), new YoutubeCrawler());
		this.naiveBayesClassifier = new NaiveBayesClassifier(new LinkSplitter(new URLHelper()), new LinkReadDBHandler(),
				this.linkHandler, new StopWordHandler());
		this.followUpRankHandler = new FollowUpRankHandler(new LinkReadDBHandler(), new LinkUpdateDBHandler(), this.link.getFollowUpRank());
		
		this.initScene();
		this.makeModal();
		
		this.initLayout();
		this.initTagging();
		this.bindSizes();
		this.initCommandGrid();

		this.setOnCloseRequest(this::handleCloseRequest);
	}

	private void handleCloseRequest(WindowEvent windowEvent){
		if (isValidationError){
			windowEvent.consume();
		}
	}
	
	private void initScene(){
		int width = 600;
		int height = 850;
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
		this.initRank();
		this.initSuggestions();
		this.initCreationDate();
		this.initLastUpdated();
	}

	private void bindSizes(){
		this.flowGeneral.prefWidthProperty().bind(this.scene.widthProperty());
		this.gridData.prefWidthProperty().set(this.flowGeneral.prefWidthProperty().getValue() - 10);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setPercentWidth(20);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPercentWidth(80);
		this.gridData.getColumnConstraints().addAll(col1, col2);
		this.listAllSelectableTags.prefWidthProperty().set(this.gridData.prefWidthProperty().getValue());
		this.listAllSelectableTags.setPrefHeight(300);
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
		this.cmbCategories = new ComboBox<>();

		ObservableList<Category> categoryList =
	            FXCollections.observableArrayList(categoryHandler.getCategories());
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

		this.cmbCategories.setButtonCell(new ListCell<>() {
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

	private void initRank(){
		this.gridData.add(this.lblRank, 0, 4);

		this.ntRank = new NumberSpinner(this.followUpRankHandler.getHighestRank(), this.followUpRankHandler.getLowestRank());
		this.ntRank.setNumber(BigDecimal.valueOf(this.link.getFollowUpRank()));
		this.gridData.add(this.ntRank, 1,4);
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
		}
		this.cmbCategories.getSelectionModel().select(index);
	}

	private void initSuggestions(){
		this.gridData.add(this.lblSuggestions, 0, 5);

		this.flowSuggestions = new FlowPane(Orientation.HORIZONTAL);
		this.flowSuggestions.setPadding(new Insets(5, 5, 0, 5));

		suggestions = initSuggestionDataWithProbabilities();

		for (Probability suggestion : suggestions){
			Button button = new Button(suggestion.toString());
			button.setOnAction(this::setSuggestedCategory);
			this.flowSuggestions.getChildren().add(button);
		}

		this.gridData.add(this.flowSuggestions, 1, 5);
	}

	private void setSuggestedCategory(ActionEvent actionEvent){
		try {
			String buttonText = ((Button)actionEvent.getSource()).getText();
			Optional<Probability> probability = suggestions
					.stream()
					.filter(s -> s.toString().contains(buttonText))
					.findFirst();
			String categoryName = null;
			if (probability.isPresent()){
				categoryName = probability.get().getCategoryName();
			}
			Category category = categoryHandler.getCategoryByName(categoryName);
			link.setCategory(category);
			linkHandler.updateLink(link);
			setCategory();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	private List<Suggestion> initSuggestionData(){
		URLHelper urlHelper = new URLHelper();
		LinkSplitter linkSplitter = new LinkSplitter(urlHelper);
		SuggestionDataHandler suggestionDataHandler = new SuggestionDataHandler(linkSplitter, new StopWordHandler());
		LinkReadDBHandler linkReadDBHandler = new LinkReadDBHandler();
		SuggestionHandler suggestionHandler = new SuggestionHandler(suggestionDataHandler, linkSplitter, linkReadDBHandler, new FollowUpStatusReadDBHandler());
		return suggestionHandler.getSuggestions(this.link);
	}

	private List<Probability> initSuggestionDataWithProbabilities(){
		List<Probability> originalList = this.naiveBayesClassifier.classify(this.link);
		int max = originalList.size() < 10 ? originalList.size() : 10;
		return originalList.subList(0, max);
	}
	
	private void initCreationDate(){
		this.gridData.add(this.lblCreated, 0, 6);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		this.lblCreatedDate = new Label(df.format(this.link.getCreated()));
		this.gridData.add(this.lblCreatedDate, 1, 6);
	}
	
	private void initLastUpdated(){
		this.gridData.add(this.lblLastUpdated, 0, 7);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		this.lblLastUpdatedDate = new Label(df.format(this.link.getLastUpdated()));
		this.gridData.add(this.lblLastUpdatedDate, 1, 7);
	}
	
	private void initTagging(){
		this.populateDataLists();
		this.initTaggingListView();
	}
	
	private void populateDataLists(){
		this.allTags = TagHandler.getTags();
		this.existingTagsForLink = new ArrayList();
		this.selectableTags = new ArrayList();
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
		this.listAllSelectableTags = new ListView<>();
		
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

		
		this.listAllSelectableTags.setCellFactory(CheckBoxListCell.forListView(SelectableTag::selectedProperty, new StringConverter<SelectableTag>() {

			@Override
			public SelectableTag fromString(String arg0) {
				return null;
			}

			@Override
			public String toString(SelectableTag arg0) {
				return arg0.getName();
			}}
			)
		);

		this.listAllSelectableTags.setItems(this.observableSelectableTags);

		this.flowGeneral.getChildren().add(this.listAllSelectableTags);
	}
	
	private void initCommandGrid(){
		this.gridCommands = new GridPane();
		gridCommands.setHgap(10);
	    gridCommands.setVgap(10);
	    gridCommands.setPadding(new Insets(5));
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
		this.btnSave.setOnAction(this::saveAndClose);
		this.gridCommands.add(this.btnSave, 0, 0);
	}

	private void saveAndClose(ActionEvent actionEvent){
		try {
			isValidationError = false;
			if (processInput()){
				close();
			}
		} catch (SQLException e) {
			showErrorMessage(e);
			isValidationError = true;
		}
	}

	private void showErrorMessage(final Exception ex){
		Alert alert = new Alert(Alert.AlertType.ERROR, ex.getMessage(), ButtonType.OK);
		alert.initOwner(this);
		alert.showAndWait();
	}
	
	private void initCancelButton(){
		this.btnCancel = new Button("Cancel");
		this.btnCancel.setOnAction(this::cancelAndClose);
		this.gridCommands.add(this.btnCancel, 1, 0);
	}

	private void cancelAndClose(ActionEvent actionEvent){
		isValidationError = false;
		close();
	}

	private void initGenerateTitleButton(){
		this.btnGenerateTitle = new Button("Generate title");
		this.btnGenerateTitle.setOnAction(this::generateTitle);
		this.gridCommands.add(this.btnGenerateTitle, 2, 0);
	}

	private void generateTitle(ActionEvent actionEvent){
		link.setTitle(this.titleHandler.generateTitle(link));
		txtTitle.setText(link.getTitle());
	}
	
	private boolean processInput() throws SQLException{
		if (!URLValidator.isValidURL(this.txtURL.getText())){
			showAlert("The URL is incorrect!");
			isValidationError = true;
			return false;
		}

		if (this.ntRank.getNumber().intValue() == 0
		|| this.ntRank.getNumber().intValue() < -1){
			showAlert("The rank is incorrect!");
			isValidationError = true;
			return false;
		}

		Link updatedLink = new Link(this.txtTitle.getText(), this.txtURL.getText(), this.taDescription.getText());
		updatedLink.setCreated(this.link.getCreated());		
		updatedLink.setId(this.link.getId());
		updatedLink.setFollowUpRank(this.ntRank.getNumber().intValue());

		setCategory(updatedLink);
		if (this.link.getId() <= 0) {
			//updatedLink.setFollowUpRank(-1);
			linkHandler.createLink(updatedLink);
			followUpRankHandler.updateRanks(updatedLink, -1);
		}else {
			linkHandler.updateLink(updatedLink);
			followUpRankHandler.updateRanks(updatedLink, link.getFollowUpRank());

		}
		return true;
	}

	private void showAlert(String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
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