package com.osf.coursemanagement.model;

/**
 * Clasa pentru gestionarea sesiunii utilizatorului (Singleton)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
public class UserSession {

	// instanta unica a clasei (singleton) - ca sa am acces la userul logat din
	// orice parte a aplicatiei si sa nu fiu nevoit sa il pasez constant intre
	// ferestre ca param
	private static UserSession instance;
	private User loggedInUser;
	private boolean isDarkMode = false;

	private UserSession() {
	}

	public static UserSession getInstance() {
		if (instance == null) {
			instance = new UserSession();
		}
		return instance;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	private String token;

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public String getAuthHeader() {
		if (loggedInUser != null && loggedInUser.getPassword() != null) {
			String auth = loggedInUser.getEmail() + ":" + loggedInUser.getPassword();
			return "Basic " + java.util.Base64.getEncoder().encodeToString(auth.getBytes());
		}
		return null;
	}

	public void cleanUserSession() {
		loggedInUser = null; // logout
		token = null;
		isDarkMode = false; // reset theme la logout
	}

	public boolean isDarkMode() {
		return isDarkMode;
	}

	public void setDarkMode(boolean darkMode) {
		isDarkMode = darkMode;
	}
}
