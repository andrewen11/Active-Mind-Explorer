/**
 * Clasa (Controller) pentru Modul
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.controller;

import com.osf.coursemanagement.server.model.Module;
import com.osf.coursemanagement.server.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

	@Autowired
	private ModuleRepository moduleRepository;

	// GET endpoint pt a lua modulele unui curs
	@GetMapping("/course/{courseId}")
	public List<Module> getModulesByCourse(@PathVariable int courseId) {
		return moduleRepository.findByCourseId(courseId);
	}

	// GET endpoint pt toate modulele
	@GetMapping
	public List<Module> getAllModules() {
		return moduleRepository.findAll();
	}

	// POST endpoint (Upsert nativ - insert sau update)
	@PostMapping
	public ResponseEntity<String> saveModule(@RequestBody Module module) {
		try {
			if (module.getModuleId() > 0) {
				// updatez modul existent
				moduleRepository.updateModuleNative(
						module.getModuleId(),
						module.getCourseId(),
						module.getName(),
						module.getDescription(),
						module.getCompletionCriteria(),
						module.getModuleType(),
						module.getNumberOfPoints());
				return ResponseEntity.ok("Module updated successfully");
			} else {
				// inserez modul nou
				moduleRepository.insertModuleNative(
						module.getCourseId(),
						module.getName(),
						module.getDescription(),
						module.getCompletionCriteria(),
						module.getModuleType(),
						module.getNumberOfPoints());
				return ResponseEntity.ok("Module created successfully");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error saving module: " + e.getMessage());
		}
	}

	// DELETE endpoint (stergere nativa)
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteModule(@PathVariable int id) {
		try {
			moduleRepository.deleteModuleNative(id);
			return ResponseEntity.ok("Module deleted successfully");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error deleting module: " + e.getMessage());
		}
	}
}
