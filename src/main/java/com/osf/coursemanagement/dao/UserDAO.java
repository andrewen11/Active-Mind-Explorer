/**
 * Clasa pentru accesul la datele utilizatorilor (Client DAO)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.dao;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osf.coursemanagement.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {

	private static final String BASE_URL = "http://localhost:8080/api/auth";
	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public UserDAO() {
	}

	// autentificare user in sistem
	// trimit credentiale la server si ret obiectul user daca e ok (login
	// admin/colaborator gaseste userul in bd)
	public User authenticate(String email, String password) throws IOException, InterruptedException {
		Map<String, String> credentials = new HashMap<>();
		credentials.put("email", email);
		credentials.put("password", password);
		String requestBody = mapper.writeValueAsString(credentials);

		// nu trebuie header de auth la login pt ca face sesiunea!!!
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(BASE_URL + "/login"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		if (response.statusCode() == 200) {
			return mapper.readValue(response.body(), User.class);
		} else {
			// arunc eroare cu mesajul de la server
			throw new RuntimeException(response.body());
		}
	}

	public User getUserById(int id) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + id))
					.header("Authorization", com.osf.coursemanagement.model.UserSession.getInstance().getAuthHeader())
					.GET().build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), User.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// pe baza crud - iau toti userii din sistem ptr admin
	public java.util.List<User> getAllUsers() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/users"))
					.header("Authorization", com.osf.coursemanagement.model.UserSession.getInstance().getAuthHeader())
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(),
						new com.fasterxml.jackson.core.type.TypeReference<java.util.List<User>>() {
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	// verificare inscriere user(simulare backend)
	private void validateUser(User user) {
		if (user.getFirstName() == null || user.getFirstName().trim().isEmpty() ||
				user.getLastName() == null || user.getLastName().trim().isEmpty() ||
				user.getEmail() == null || user.getEmail().trim().isEmpty() ||
				user.getPassword() == null || user.getPassword().trim().isEmpty()) {
			throw new RuntimeException("All fields are required!");
		}

		String mail = user.getEmail().trim();
		if (!mail.contains("@") || !(mail.endsWith(".ro") || mail.endsWith(".com") || mail.endsWith(".demo")
				|| mail.endsWith(".org"))) {
			throw new RuntimeException("Email must contain '@' and end with .ro, .com, .demo, or .org!"); // mesaj
																											// eroare
		}

		String pass = user.getPassword();
		if (pass.length() < 8 || !pass.matches(".*\\d.*")) {
			throw new RuntimeException("Password must be at least 8 characters long and contain a digit!");
		}
	}

	// creare user nou la inregistrare - retin userul creat sau preiau / intorc
	// eroare dac e cazul
	public User createUser(User user) {
		validateUser(user); // validare inainte de trimitere
		try {
			String body = mapper.writeValueAsString(user);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/register"))
					.header("Authorization", com.osf.coursemanagement.model.UserSession.getInstance().getAuthHeader())
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(body))
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), User.class);
			} else {
				throw new RuntimeException(response.body());
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Connection error: " + e.getMessage(), e);
		}
	}

	// update date user existent - pt a schimba parola, rolul, assign ul, etc
	public User updateUser(User user) {
		validateUser(user); // validare
		try {
			String body = mapper.writeValueAsString(user);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + user.getId()))
					.header("Authorization", com.osf.coursemanagement.model.UserSession.getInstance().getAuthHeader())
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(body))
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), User.class);
			} else {
				throw new RuntimeException(response.body());
			}
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException("Connection error: " + e.getMessage(), e);
		}
	}

	public boolean deleteUser(int id) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + id))
					.header("Authorization", com.osf.coursemanagement.model.UserSession.getInstance().getAuthHeader())
					.DELETE()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	// met noi pt stats sql nativetop studenti dupa progres
	public java.util.List<User> getTopStudents() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/stats/top-students?min=1"))
					.header("Authorization", com.osf.coursemanagement.model.UserSession.getInstance().getAuthHeader())
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(),
						new com.fasterxml.jackson.core.type.TypeReference<java.util.List<User>>() {
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new java.util.ArrayList<>();
	}

	// studenti cu prezenta la sesiuni live
	public java.util.List<User> getStudentsWithLiveAttendance() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/stats/live-attendance"))
					.header("Authorization", com.osf.coursemanagement.model.UserSession.getInstance().getAuthHeader())
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(),
						new com.fasterxml.jackson.core.type.TypeReference<java.util.List<User>>() {
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new java.util.ArrayList<>();
	}

	// stats detaliate ptr cursurile unui user
	public java.util.List<Map<String, Object>> getUserCourseStats(int userId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + userId + "/stats/courses"))
					.header("Authorization", com.osf.coursemanagement.model.UserSession.getInstance().getAuthHeader())
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(),
						new com.fasterxml.jackson.core.type.TypeReference<java.util.List<Map<String, Object>>>() {
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new java.util.ArrayList<>();
	}
}
