/**
 * Clasa pentru definirea modulelor aplicatiei
 * 
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
module com.osf.coursemanagement {
	// module pt javafx si controale
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

	// comunicare cu serverul
	requires java.net.http;
	requires com.fasterxml.jackson.databind;

	// deschidere pachete pt fxml loader si jackson (reflexie)
	opens com.osf.coursemanagement to javafx.fxml;
	opens com.osf.coursemanagement.model to com.fasterxml.jackson.databind, javafx.base;

	// exportez pachetele ca sa fie vizibile
	exports com.osf.coursemanagement;
	exports com.osf.coursemanagement.controller;

	opens com.osf.coursemanagement.controller to javafx.fxml;
}