/**
 * Clasa (Controller) pentru Sesiune Live
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.controller;

import com.osf.coursemanagement.server.model.LiveSession;
import com.osf.coursemanagement.server.repository.LiveSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livesessions")
public class LiveSessionController {

	@Autowired // marcare dependenta
	private LiveSessionRepository liveSessionRepository;

	@GetMapping("/course/{courseId}") // endpoint
	public ResponseEntity<List<LiveSession>> getSessionsByCourse(@PathVariable int courseId) {
		List<LiveSession> sessions = liveSessionRepository.findByCourseId(courseId);
		return ResponseEntity.ok(sessions);
	}

	@PostMapping
	public ResponseEntity<LiveSession> createSession(@RequestBody LiveSession session) {
		LiveSession created = liveSessionRepository.save(session);
		return ResponseEntity.ok(created);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteSession(@PathVariable int id) {
		if (!liveSessionRepository.existsById(id)) {
			return ResponseEntity.notFound().build();
		}
		liveSessionRepository.deleteById(id);
		return ResponseEntity.ok().build();
	}
}
