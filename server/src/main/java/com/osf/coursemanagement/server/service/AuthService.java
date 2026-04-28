/**
 * Clasa (Service) pentru Autentificare
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.service;

import com.osf.coursemanagement.server.model.User;
import com.osf.coursemanagement.server.model.Course;
import com.osf.coursemanagement.server.repository.UserRepository;
import com.osf.coursemanagement.server.repository.EnrolmentRepository;
import com.osf.coursemanagement.server.repository.LiveAttendanceRepository;
import com.osf.coursemanagement.server.repository.SelfPacedProgressRepository;
import com.osf.coursemanagement.server.repository.ModuleRepository;
import com.osf.coursemanagement.server.repository.LiveSessionRepository;
import com.osf.coursemanagement.server.repository.CourseRepository;
import com.osf.coursemanagement.server.model.Enrolment;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuthService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SelfPacedProgressRepository progressRepository;

	@Autowired
	private ModuleRepository moduleRepository;

	@Autowired
	private LiveAttendanceRepository liveAttendanceRepository;

	@Autowired
	private LiveSessionRepository liveSessionRepository;

	@Autowired
	private CourseRepository courseRepository;

	// mecanism simplu de autentificare
	// (verific in bd daca exista user cu email si parola)
	public User authenticate(String email, String password) {
		// verific datele de input
		if (email == null || password == null) {
			return null;
		}

		// spring returneaza direct user sau null
		User userGasit = userRepository.findByEmailAndPassword(email, password);

		if (userGasit != null) {
			// verificare rol (backend-driven)
			String role = userGasit.getRoleType();
			if ("Admin".equals(role) || "Collaborator".equals(role)) {
				return userGasit;
			} else {
				throw new RuntimeException("Access denied. Only Admins/Staff can use this console.");
			}
		} else {
			// arunc eroare ca sa pot configura mesajul din backend
			throw new RuntimeException("Invalid email or password! Try again!");
		}
	}

	// insert user (save)
	public User registerUser(User user) {
		// validare email duplicat
		if (userRepository.existsByEmail(user.getEmail())) {
			throw new RuntimeException("Email already active: " + user.getEmail());
		}
		return userRepository.save(user);
	}

	// update
	public User updateUser(User user) {
		return userRepository.save(user); // jpa face update daca id exista
	}

	@Autowired
	private EnrolmentRepository enrolmentRepository;

	@Autowired
	private EnrolmentService enrolmentService;

	// delete
	public void deleteUser(int id) {
		// stergere in cascada (inscrieri -> progres/prezenta)
		List<Enrolment> enrolments = enrolmentRepository.findByUserId(id);
		for (Enrolment e : enrolments) {
			enrolmentService.unenrol(e.getEnrolmentId());
		}

		userRepository.deleteById(id);
	}

	// read all
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// read by id
	public User getUserById(int id) {
		return userRepository.findById(id).orElse(null);
	}

	// metode noi pt statistici sql nativ

	public List<User> getTopStudents(long minEnrolments) {
		return userRepository.findUsersWithMoreEnrolmentsThan(minEnrolments);
	}

	public List<User> getStudentsWithLiveAttendance() {
		return userRepository.findUsersWhoAttendedLiveSessions();
	}

	// statistici user (insigne si scoruri)
	public List<Map<String, Object>> getUserCourseStats(int userId) {
		List<Map<String, Object>> result = java.util.ArrayList.class.cast(new java.util.ArrayList<>());
		List<Course> courses = courseRepository.findCoursesByUserId(userId);

		for (Course c : courses) {
			Map<String, Object> stats = new HashMap<>();
			stats.put("courseId", c.getCourseId());
			stats.put("courseName", c.getName());

			// Badges
			Integer badges = progressRepository.countBadgesForUserAndCourse(userId, c.getCourseId());
			stats.put("badges", badges != null ? badges : 0);

			// progres al score ului prin modulele asincrone (punctajul total com asinc=
			// suma pct trailhead / nr total module din curs)
			Integer totalModules = moduleRepository.countModulesByCourseId(c.getCourseId());
			Double totalProgressPoints = progressRepository.findTotalScoreByUserIdAndCourseId(userId, c.getCourseId());

			if (totalModules == null)
				totalModules = 0;
			if (totalProgressPoints == null)
				totalProgressPoints = 0.0;

			double pScore = 0.0;
			if (totalModules > 0) {
				pScore = totalProgressPoints / totalModules;
			}
			stats.put("progressScore", pScore);

			// Live Score (Real Average: punctajul total com live= suma pct sesiune / nr
			// total sesiuni live din curs
			Integer totalSessions = liveSessionRepository.countSessionsByCourseId(c.getCourseId());
			Double totalLivePoints = liveAttendanceRepository.findTotalScoreByUserIdAndCourseId(userId,
					c.getCourseId());

			if (totalSessions == null)
				totalSessions = 0;
			if (totalLivePoints == null)
				totalLivePoints = 0.0;

			double lScore = 0.0;
			if (totalSessions > 0) {
				lScore = totalLivePoints / totalSessions;
			}
			stats.put("liveScore", lScore);

			// Total Score
			stats.put("totalScore", (pScore + lScore) / 2.0);

			result.add(stats);
		}
		return result;
	}
}
