/**
 * Clasa (Controller) pentru Curs
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.controller;

import com.osf.coursemanagement.server.model.Course;
import com.osf.coursemanagement.server.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

	@Autowired
	private CourseService courseService;

	// GET endpoint - toate cursurile
	@GetMapping
	public List<Course> getAllCourses() {
		return courseService.getAllCourses();
	}

	// GET endpoint pt cursurile unui student
	@GetMapping("/enrolled/{userId}")
	public List<Course> getMyCourses(@PathVariable int userId) {
		return courseService.getEnrolledCourses(userId);
	}

	// GET endpoint detalii complete curs (inscrieri, module)
	@GetMapping("/{id}/details")
	public ResponseEntity<Map<String, Object>> getCourseDetails(@PathVariable int id) {
		return ResponseEntity.ok(courseService.getCourseDetails(id));
	}

	// GET endpoint pt participanti
	@GetMapping("/{id}/participants")
	public ResponseEntity<List<Map<String, Object>>> getParticipants(@PathVariable int id) {
		return ResponseEntity.ok(courseService.getParticipants(id));
	}

	// POST endpoint - adaug / modific curs (prin body)
	@PostMapping
	public ResponseEntity<Course> saveCourse(@RequestBody Course course) {
		return ResponseEntity.ok(courseService.saveCourse(course));
	}

	// DELETE endpoint - sterg cursul specificat
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteCourse(@PathVariable int id) {
		try {
			courseService.deleteCourse(id);
			return ResponseEntity.ok("Deleted");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Cannot delete course. It might have active enrolments!");
		}
	}

	// --- ENDPOINTS STATISTICI DB (pt admin) ---

	@GetMapping("/search")
	public List<Course> searchByModule(@RequestParam String type) {
		return courseService.getCoursesByModuleType(type);
	}

	@GetMapping("/stats/abandoned")
	public List<Course> getAbandonedCourses() {
		return courseService.getAbandonedCourses();
	}

	@GetMapping("/stats/complex")
	public List<Course> getBigCourses(@RequestParam(defaultValue = "5") long minModules) {
		return courseService.getCoursesWithManyModules(minModules);
	}
}
