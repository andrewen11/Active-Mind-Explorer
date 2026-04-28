/**
 * Clasa pentru accesul la datele modulelor (Client DAO)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osf.coursemanagement.model.Module;
import com.osf.coursemanagement.model.UserSession;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

public class ModuleDAO {
	private static final String BASE_URL = "http://localhost:8080/api/modules"; // adresa API
	private final HttpClient client = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper(); // convertorul JSON <-> Java

	public ModuleDAO() {
	}

	public List<Module> getModulesByCourseId(int courseId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/course/" + courseId))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<Module>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	// metoda pt afisarea tuturor modulurilor
	public List<Module> getAllModules() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<Module>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	public boolean createModule(Module module) {
		try {
			String json = mapper.writeValueAsString(module);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.header("Content-Type", "application/json")
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200 || response.statusCode() == 201;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean updateModule(Module module) {
		try {
			String json = mapper.writeValueAsString(module);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.header("Content-Type", "application/json")
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteModule(int moduleId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + moduleId))
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
