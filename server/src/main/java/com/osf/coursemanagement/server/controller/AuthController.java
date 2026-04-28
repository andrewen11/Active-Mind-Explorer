/**
 * Clasa (Controller) pentru Autentificare si User Management
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.controller;

import com.osf.coursemanagement.server.model.User;
import com.osf.coursemanagement.server.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
		String email = credentials.get("email");
		String password = credentials.get("password");

		if (email == null || password == null) {
			return ResponseEntity.badRequest().body("Email and password are required");
		}

		try {
			User user = authService.authenticate(email, password);
			return ResponseEntity.ok(user);
		} catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
		}
	}

	// conf register (Register generic)
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		try {
			return ResponseEntity.ok(authService.registerUser(user));
		} catch (RuntimeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	// update
	@PostMapping("/{id}") // Changed from PUT to POST per user request
	public ResponseEntity<User> updateUser(@PathVariable int id, @RequestBody User user) {
		user.setId(id); // Ensure ID is set
		return ResponseEntity.ok(authService.updateUser(user));
	}

	// del
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable int id) {
		try {
			authService.deleteUser(id);
			return ResponseEntity.ok("Deleted");
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Cannot delete user.");
		}
	}

	// sect endpoints - statistici
	@GetMapping("/users")
	public List<User> getAllUsers() {
		return authService.getAllUsers();
	}

	@GetMapping("/stats/top-students")
	public List<User> getTopStudents(@RequestParam(defaultValue = "2") long min) {
		return authService.getTopStudents(min);
	}

	@GetMapping("/stats/live-attendance")
	public List<User> getLiveAttendanceStats() {
		return authService.getStudentsWithLiveAttendance();
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable int id) {
		User user = authService.getUserById(id);
		if (user != null) {
			return ResponseEntity.ok(user);
		}
		return ResponseEntity.notFound().build();
	}

	@GetMapping("/{id}/stats/courses")
	public ResponseEntity<List<Map<String, Object>>> getUserCourseStats(@PathVariable int id) {
		return ResponseEntity.ok(authService.getUserCourseStats(id));
	}
}
