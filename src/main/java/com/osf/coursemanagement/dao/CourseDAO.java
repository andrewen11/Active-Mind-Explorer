/**
 * Clasa pentru accesul la datele cursurilor (Client DAO)
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osf.coursemanagement.model.Course;
import com.osf.coursemanagement.model.UserSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
	private static final String BASE_URL = "http://localhost:8080/api/courses";
	// client http refolosit pt a nu crea conexiuni noi la fiecare request
	// (eficienta)
	private final HttpClient client = HttpClient.newHttpClient();
	// mapper pt conversie json <-> obiecte java
	private final ObjectMapper mapper = new ObjectMapper();

	public CourseDAO() {
	}

	/*
	 * obtine toate cursurile (admin)
	 * returneaza lista completa de cursuri disponibile in sistem fiind necesar
	 * pentru panoul de administrare
	 */
	public List<Course> getAllCourses() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<Course>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/*
	 * obtine cursurile mele in calitatet de student/ trainee
	 * returneaza doar cursurile la care utilizatorul specificat este inscris
	 * folosit in dashboard ul studentului
	 */
	public List<Course> getEnrolledCourses(int userId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/enrolled/" + userId))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) { // succes?
				return mapper.readValue(response.body(), new TypeReference<List<Course>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	/*
	 * detalii Curs - partea de statistici (cu date din baza de date) + agregate
	 * ->returneaza un map cu statistici despre cursul specificat
	 */
	public java.util.Map<String, Object> getCourseDetails(int courseId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + courseId + "/details"))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<java.util.Map<String, Object>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// creare curs - trimite un request POST pentru a crea un curs nou in baza de
	// date
	public boolean createCourse(Course course) {
		try {
			String json = mapper.writeValueAsString(course);
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.header("Content-Type", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(json))
					.build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			return response.statusCode() == 200 || response.statusCode() == 201; // return true daca operatia a reusit,
																					// false altfel
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// modifica detaliile unui curs existent - se bazeaza pe id ul cursului din
	// obiect
	public boolean updateCourse(Course course) {
		try {
			String json = mapper.writeValueAsString(course);
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

	// elim definitiv un curs din sistem pe baza id ului
	public boolean deleteCourse(int courseId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + courseId))
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

	// part2 - metode noi pt funct sql native
	// cautare curs/uri dupa modul
	// permite filtrarea cursurilor care contin un anumit tip de continut
	public List<Course> searchCourses(String type) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/search?type=" + type))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<Course>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	// obtine cursuri abandonate (ref la stats) - identific cursurile care nu au
	// activitate recenta sau inscrieri
	public List<Course> getAbandonedCourses() {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/stats/abandoned")) // endpoint pt statistici
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<Course>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	// obtine cursuri mari (ref la stats) - return cursurile care depasesc un nr min
	// de module specificat
	public List<Course> getBigCourses(long min) {
		try {
			if (min < 0)
				min = 0;
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/stats/complex?minModules=" + min))
					.header("Authorization", UserSession.getInstance().getAuthHeader()) // autorizare (header)
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<Course>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	// obtine participantii cursului -> return lista cu studentii inscrisi la un
	// anumit curs
	// map-ul returnat contine: "studentName" (String), "enrolmentId" (Integer),
	// "userId" (Integer)
	// structura asta e vitala pt controller ca sa stie ce chei sa acceseze
	public List<java.util.Map<String, Object>> getParticipants(int courseId) {
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(BASE_URL + "/" + courseId + "/participants"))
					.header("Authorization", UserSession.getInstance().getAuthHeader())
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return mapper.readValue(response.body(), new TypeReference<List<java.util.Map<String, Object>>>() {
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
}
