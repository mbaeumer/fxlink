module fxlink8 {
    requires javafx.controls;
    requires java.sql;
    requires java.desktop;
    requires commons.validator;
    opens se.mbaeumer.fxlink.gui to javafx.graphics;
    opens se.mbaeumer.fxlink.models to javafx.base;
}

