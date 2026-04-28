/**
 * Clasa (Data Access Object) pentru Progres
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osf.coursemanagement.model.SelfPacedProgress;
import com.osf.coursemanagement.model.UserSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class ProgressDAO {
	private static final String BASE_URL = "http://localhost:8080/api/progress";
	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();

	public ProgressDAO() {
	}

	public List<SelfPacedProgress> getProgressByEnrolmentId(int enrolmentId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/enrolment/" + enrolmentId))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<SelfPacedProgress>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public boolean saveProgress(List<SelfPacedProgress> progressList) {
		try {
			String json = mapper.writeValueAsString(progressList);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/save"))
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

	public boolean createProgress(SelfPacedProgress progress) {
		try {
			String json = mapper.writeValueAsString(progress);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200 || response.statusCode() == 201;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateProgress(SelfPacedProgress progress) {
		try {
			String json = mapper.writeValueAsString(progress);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
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

	public boolean deleteProgress(int progressId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + progressId))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.DELETE()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200 || response.statusCode() == 204;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
