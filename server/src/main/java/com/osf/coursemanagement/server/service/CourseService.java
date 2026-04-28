/**
 * Clasa (Service) pentru Curs
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.service;

import com.osf.coursemanagement.server.model.Course;
import com.osf.coursemanagement.server.repository.CourseRepository;
import com.osf.coursemanagement.server.repository.EnrolmentRepository;
import com.osf.coursemanagement.server.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.osf.coursemanagement.server.model.Enrolment;
import com.osf.coursemanagement.server.model.Module;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService {

	@Autowired
	private CourseRepository courseRepository;

	@Autowired
	private EnrolmentRepository enrolmentRepository;

	@Autowired
	private ModuleRepository moduleRepository;

	// gaseste toate cursurile
	public List<Course> getAllCourses() {
		return courseRepository.findAll();
	}

	public Map<String, Object> getCourseDetails(int courseId) {
		Map<String, Object> details = new HashMap<>();

		// date curs
		Course course = courseRepository.findById(courseId).orElse(null);
		details.put("course", course);

		if (course != null) {
			// numar inscrieri
			int count = enrolmentRepository.countByCourseId(courseId);
			details.put("enrolmentCount", count);

			// lista module
			List<Module> modules = moduleRepository.findByCourseId(courseId);
			details.put("modules", modules);
		}

		return details;
	}

	// cursurile studentului (join query)
	public List<Course> getEnrolledCourses(int userId) {
		return enrolmentRepository.findCoursesByUserId(userId);
	}

	// modifica/adauga curs
	public Course saveCourse(Course course) {
		return courseRepository.save(course);
	}

	@Autowired
	private EnrolmentService enrolmentService;

	@Autowired
	private ModuleService moduleService;

	public void deleteCourse(int courseId) {
		// PRIMA DATA !!! sterg inscrierile
		List<Enrolment> enrolments = enrolmentRepository.findByCourseId(courseId);
		for (Enrolment e : enrolments) {
			enrolmentService.unenrol(e.getEnrolmentId());
		}

		// apoi sterg modulele!!
		List<Module> modules = moduleRepository.findByCourseId(courseId);
		for (Module m : modules) {
			moduleService.deleteModule(m.getModuleId());
		}

		courseRepository.deleteById(courseId);
	}

	// participanti inscrisi (pt prezenta)
	public List<Map<String, Object>> getParticipants(int courseId) {
		List<Enrolment> enrolments = enrolmentRepository.findByCourseId(courseId);
		java.util.List<Map<String, Object>> participants = new java.util.ArrayList<>();

		for (Enrolment e : enrolments) {
			if (e.getUser() != null) {
				Map<String, Object> map = new HashMap<>();
				map.put("enrolmentId", e.getEnrolmentId());
				map.put("userId", e.getUserId());
				map.put("studentName", e.getUser().getFirstName() + " " + e.getUser().getLastName());
				participants.add(map);
			}
		}
		return participants;
	}

	// integrare cerinte db (sql nativ) - pt stats
	// caut curs dup tip modul (join)
	public List<Course> getCoursesByModuleType(String type) {
		return courseRepository.findCoursesByModuleType(type);
	}

	// cursuri abandonate (subquery)
	public List<Course> getAbandonedCourses() {
		return courseRepository.findCoursesWithNoEnrolments();
	}

	// cursuri mari (multe module) - subquery
	public List<Course> getCoursesWithManyModules(long minModules) {
		return courseRepository.findCoursesWithMinModules(minModules);
	}
}