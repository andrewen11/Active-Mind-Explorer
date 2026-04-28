module com.osf.coursemanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    opens com.osf.coursemanagement to javafx.fxml;
    exports com.osf.coursemanagement;
    exports com.osf.coursemanagement.controller;
    opens com.osf.coursemanagement.controller to javafx.fxml;
}