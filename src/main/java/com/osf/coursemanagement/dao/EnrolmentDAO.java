/**
 * Clasa pentru accesul la datele inscrierilor (Client DAO)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osf.coursemanagement.model.UserSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class EnrolmentDAO {
	private static final String API_URL = "http://localhost:8080/api/enrolments";
	// folosim un singur client pt toata clasa
	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();

	public boolean enrol(int userId, int courseId) {
		try {
			Map<String, Integer> payload = new HashMap<>();
			payload.put("userId", userId);
			payload.put("courseId", courseId);
			String json = mapper.writeValueAsString(payload);

			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(API_URL))
					// iau token ul mereu gata din UserSession la fiecare request
					// ->asigura ca nu folosesc unul expirat sau de la userul anterior!!
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean unenrol(int enrolmentId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(API_URL + "/" + enrolmentId))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.DELETE()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
