/**
 * Clasa (Controller) pentru Inscriere
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.controller;

import com.osf.coursemanagement.server.model.Enrolment;
import com.osf.coursemanagement.server.service.EnrolmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/enrolments")
public class EnrolmentController {

	@Autowired
	private EnrolmentService enrolmentService;

	// POST /api/enrolments?userId=5&courseId=2
	@PostMapping
	public ResponseEntity<?> enrol(@RequestBody Map<String, Integer> payload) {
		if (!payload.containsKey("userId") || !payload.containsKey("courseId")) {
			return ResponseEntity.badRequest().body("Missing userId or courseId");
		}
		int userId = payload.get("userId");
		int courseId = payload.get("courseId");

		Enrolment e = enrolmentService.enrol(userId, courseId);
		return ResponseEntity.ok(e);
	}

	// DELETE /api/enrolments/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<?> unenrol(@PathVariable int id) {
		try {
			enrolmentService.unenrol(id);
			return ResponseEntity.ok("Unenrolled");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Failed to unenrol: " + e.getMessage());
		}
	}
}
