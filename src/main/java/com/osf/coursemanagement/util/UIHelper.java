/**
 * Clasa (Helper) pentru Interfata Grafica
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.util;

import com.osf.coursemanagement.model.UserSession;
import javafx.scene.control.DialogPane;

public class UIHelper {

	private static final String CSS_PATH = "/com/osf/coursemanagement/style.css";

	// metoda statica pt a aplica stilul consistent pe toate dialogurile din
	// aplicatie (reutilizare cod, sa nu scriu aceleasi linii in fiecare
	// controller)!!!!
	public static void styleDialog(DialogPane dialogPane) {
		try {
			if (dialogPane.getScene() == null) {
			}
			if (!dialogPane.getStylesheets().contains(UIHelper.class.getResource(CSS_PATH).toExternalForm())) {
				dialogPane.getStylesheets().add(UIHelper.class.getResource(CSS_PATH).toExternalForm());
			}

			// aplic Dark Mode
			if (UserSession.getInstance().isDarkMode()) {
				if (!dialogPane.getStyleClass().contains("dark-theme")) {
					dialogPane.getStyleClass().add("dark-theme");
				}
			} else {
				dialogPane.getStyleClass().remove("dark-theme");
			}
		} catch (Exception e) {
			System.err.println("Warning: Could not load CSS from " + CSS_PATH);
			e.printStackTrace();
		}
	}
}
