//
//package se.mbaeumer.fxlink.gui.controls;
//
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.Event;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Tooltip;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.KeyEvent;
//import javafx.stage.Window;
//import se.mbaeumer.fxlink.models.Category;
//
//import java.util.stream.Stream;
//
///**
// * Created by martinbaumer on 2018-05-30.
// */
//public class ComboBoxAutoComplete extends ComboBox {
//
//    private ObservableList originalItems;
//    package se.mbaeumer.fxlink.gui.controls;
//
//    import javafx.beans.property.SimpleStringProperty;
//    import javafx.beans.property.StringProperty;
//    import javafx.collections.FXCollections;
//    import javafx.collections.ObservableList;
//    import javafx.event.Event;
//    import javafx.scene.control.ComboBox;
//    import javafx.scene.control.Tooltip;
//    import javafx.scene.input.KeyCode;
//    import javafx.scene.input.KeyEvent;
//    import javafx.stage.Window;
//    import se.mbaeumer.fxlink.models.Category;
//
//    import java.util.stream.Stream;
//
//    /**
//     * Created by martinbaumer on 2018-05-30.
//     */
//    public class ComboBoxAutoComplete extends ComboBox {
//
//        private ObservableList originalItems;
//        private StringProperty filter = new SimpleStringProperty(StringUtils.EMPTY);
//        private AutoCompleteComparator comparator = (typedText, objectToCompare) -> false;
//
//        public interface AutoCompleteComparator {
//            boolean matches(String typedText, T objectToCompare);
//        }
//
//        public void initialize(AutoCompleteComparator comparator) {
//            this.comparator = comparator;
//            this.originalItems = FXCollections.observableArrayList(getItems());
//
//            setTooltip(new Tooltip());
//            getTooltip().textProperty().bind(filter);
//
//            filter.addListener((observable, oldValue, newValue) -> handleFilterChanged(newValue));
//
//            setOnKeyPressed(this::handleOnKeyPressed);
//            setOnHidden(this::handleOnHiding);
//        }
//
//        private void handleOnKeyPressed(KeyEvent keyEvent) {
//            KeyCode code = keyEvent.getCode();
//            String filterValue = filter.get();
//            if (code.isLetterKey()) {
//                filterValue += keyEvent.getText();
//            } else if (code == KeyCode.BACK_SPACE && filterValue.length() > 0) {
//                filterValue = filterValue.substring(0, filterValue.length() - 1);
//            } else if (code == KeyCode.ESCAPE) {
//                filterValue = StringUtils.EMPTY;
//            } else if (code == KeyCode.DOWN || code == KeyCode.UP) {
//                show();
//            }
//            filter.setValue(filterValue);
//        }
//
//        private void handleFilterChanged(String newValue) {
//            if (StringUtils.isNoneBlank(newValue)) {
//                show();
//                if (StringUtils.isBlank(filter.get())) {
//                    restoreOriginalItems();
//                } else {
//                    showTooltip();
//                    getItems().setAll(filterItems());
//                }
//            } else {
//                getTooltip().hide();
//            }
//        }
//
//        private void showTooltip() {
//            if (!getTooltip().isShowing()) {
//                Window stage = getScene().getWindow();
//                double posX = stage.getX() + localToScene(getBoundsInLocal()).getMinX() + 4;
//                double posY = stage.getY() + localToScene(getBoundsInLocal()).getMinY() - 29;
//                getTooltip().show(stage, posX, posY);
//            }
//        }
//
//        private ObservableList filterItems() {
//            List filteredList = originalItems.stream().filter(el -> comparator.matches(filter.get().toLowerCase(), el)).collect(Collectors.toList());
//            return FXCollections.observableArrayList(filteredList);
//        }
//
//        private void handleOnHiding(Event e) {
//            filter.setValue(StringUtils.EMPTY);
//            getTooltip().hide();
//            restoreOriginalItems();
//        }
//
//        private void restoreOriginalItems() {
//            T s = getSelectionModel().getSelectedItem();
//            getItems().setAll(originalItems);
//            getSelectionModel().select(s);
//        }
//
//    }
//    private StringProperty filter = new SimpleStringProperty(StringUtils.EMPTY);
//    private AutoCompleteComparator comparator = (typedText, objectToCompare) -> false;
//
//    public interface AutoCompleteComparator {
//        boolean matches(String typedText, T objectToCompare);
//    }
//
//    public void initialize(AutoCompleteComparator comparator) {
//        this.comparator = comparator;
//        this.originalItems = FXCollections.observableArrayList(getItems());
//
//        setTooltip(new Tooltip());
//        getTooltip().textProperty().bind(filter);
//
//        filter.addListener((observable, oldValue, newValue) -> handleFilterChanged(newValue));
//
//        setOnKeyPressed(this::handleOnKeyPressed);
//        setOnHidden(this::handleOnHiding);
//    }
//
//    private void handleOnKeyPressed(KeyEvent keyEvent) {
//        KeyCode code = keyEvent.getCode();
//        String filterValue = filter.get();
//        if (code.isLetterKey()) {
//            filterValue += keyEvent.getText();
//        } else if (code == KeyCode.BACK_SPACE && filterValue.length() > 0) {
//            filterValue = filterValue.substring(0, filterValue.length() - 1);
//        } else if (code == KeyCode.ESCAPE) {
//            filterValue = StringUtils.EMPTY;
//        } else if (code == KeyCode.DOWN || code == KeyCode.UP) {
//            show();
//        }
//        filter.setValue(filterValue);
//    }
//
//    private void handleFilterChanged(String newValue) {
//        if (StringUtils.isNoneBlank(newValue)) {
//            show();
//            if (StringUtils.isBlank(filter.get())) {
//                restoreOriginalItems();
//            } else {
//                showTooltip();
//                getItems().setAll(filterItems());
//            }
//        } else {
//            getTooltip().hide();
//        }
//    }
//
//    private void showTooltip() {
//        if (!getTooltip().isShowing()) {
//            Window stage = getScene().getWindow();
//            double posX = stage.getX() + localToScene(getBoundsInLocal()).getMinX() + 4;
//            double posY = stage.getY() + localToScene(getBoundsInLocal()).getMinY() - 29;
//            getTooltip().show(stage, posX, posY);
//        }
//    }
//
//    private ObservableList filterItems() {
//        List filteredList = originalItems.stream().filter(el -> comparator.matches(filter.get().toLowerCase(), el)).collect(Collectors.toList());
//        return FXCollections.observableArrayList(filteredList);
//    }
//
//    private void handleOnHiding(Event e) {
//        filter.setValue(StringUtils.EMPTY);
//        getTooltip().hide();
//        restoreOriginalItems();
//    }
//
//    private void restoreOriginalItems() {
//        T s = getSelectionModel().getSelectedItem();
//        getItems().setAll(originalItems);
//        getSelectionModel().select(s);
//    }
//
//}