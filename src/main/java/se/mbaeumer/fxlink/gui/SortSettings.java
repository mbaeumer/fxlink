package se.mbaeumer.fxlink.gui;

import javafx.scene.control.TableColumn;
import se.mbaeumer.fxlink.models.Link;

import java.util.List;

public class SortSettings {
    private List<TableColumn<Link, ?>> sortedColumns;

    private TableColumn.SortType sortType;

    public SortSettings(List<TableColumn<Link, ?>> sortedColumns, TableColumn.SortType sortType) {
        this.sortedColumns = sortedColumns;
        this.sortType = sortType;
    }

    public List<TableColumn<Link, ?>> getSortedColumns() {
        return sortedColumns;
    }

    public TableColumn.SortType getSortType() {
        return sortType;
    }
}
