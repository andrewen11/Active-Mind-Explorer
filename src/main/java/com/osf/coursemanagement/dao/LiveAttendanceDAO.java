/**
 * Clasa pentru accesul la datele prezentei live (Client DAO)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osf.coursemanagement.model.UserSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LiveAttendanceDAO {

	private final String baseUrl = "http://localhost:8080/api/liveattendance";
	private final ObjectMapper mapper;

	public LiveAttendanceDAO() {
		this.mapper = new ObjectMapper();
	}

	public List<Map<String, Object>> getAttendanceBySessionId(int sessionId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(baseUrl + "/session/" + sessionId))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request,
					HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<Map<String, Object>>>() {
				});
			} else {
				System.err.println("Error fetching attendance: " + response.statusCode());
				return Collections.emptyList();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public boolean saveAttendance(int sessionId, List<Map<String, Object>> data) {
		try {
			String json = mapper.writeValueAsString(data);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(baseUrl + "/session/" + sessionId + "/save"))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request,
					HttpResponse.BodyHandlers.ofString());

			return response.statusCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
