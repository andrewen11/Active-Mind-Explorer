/**
 * Clasa pentru accesul la datele sesiunilor live (Client DAO)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osf.coursemanagement.model.UserSession;
import com.osf.coursemanagement.model.LiveSession;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class LiveSessionDAO {

	private static final String BASE_URL = "http://localhost:8080/api/livesessions";
	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();

	public LiveSessionDAO() {
	}

	public List<LiveSession> getSessionsByCourseId(int courseId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/course/" + courseId))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<LiveSession>>() {
				});
			} else {
				System.err.println("Error fetching sessions: " + response.statusCode());
				return Collections.emptyList();
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	public LiveSession createSession(LiveSession session) {
		try {
			String json = mapper.writeValueAsString(session);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200 || response.statusCode() == 201) {
				return mapper.readValue(response.body(), LiveSession.class);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public LiveSession updateSession(LiveSession session) {
		try {
			String json = mapper.writeValueAsString(session);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), LiveSession.class);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteSession(int sessionId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + sessionId))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.DELETE()
					.build();

			client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
