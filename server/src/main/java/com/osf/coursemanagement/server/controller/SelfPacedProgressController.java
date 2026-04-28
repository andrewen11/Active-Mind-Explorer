/**
 * Clasa (Controller) pentru Progres Self-Paced
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.controller;

import com.osf.coursemanagement.server.model.SelfPacedProgress;
import com.osf.coursemanagement.server.repository.EnrolmentRepository;
import com.osf.coursemanagement.server.repository.SelfPacedProgressRepository;
import com.osf.coursemanagement.server.repository.UserRepository;
import com.osf.coursemanagement.server.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class SelfPacedProgressController {

	@Autowired
	private SelfPacedProgressRepository progressRepository;

	@Autowired
	private EnrolmentRepository enrolmentRepository; // sa gasesc UserID din EnrolmentID

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/enrolment/{enrolmentId}")
	public ResponseEntity<List<SelfPacedProgress>> getProgressByEnrolment(@PathVariable int enrolmentId) {
		// returnez lista de progres pt inscrierea asta (module lucrate)
		return ResponseEntity.ok(progressRepository.findByEnrolmentId(enrolmentId));
	}

	@PostMapping("/save")
	public ResponseEntity<String> saveProgress(@RequestBody List<SelfPacedProgress> progressList) {
		if (progressList == null || progressList.isEmpty()) {
			return ResponseEntity.ok("No progress to save.");
		}

		Integer userId = null;

		for (SelfPacedProgress p : progressList) {
			// verific daca exista deja
			if (p.getProgressId() > 0) {
				// update
				progressRepository.updateProgressNative(p.getProgressId(), LocalDate.now(), p.getNumberOfPoints(),
						p.getScore());
			} else {
				// insert
				progressRepository.insertProgressNative(p.getEnrolmentId(), p.getModuleId(), LocalDate.now(),
						p.getNumberOfPoints(), p.getScore());
			}

			// iau userid pt recalculare insigne (fac doar o data pe batch)
			if (userId == null) {
				var enrolment = enrolmentRepository.findById(p.getEnrolmentId());
				if (enrolment.isPresent()) {
					userId = enrolment.get().getUserId();
				}
			}
		}

		// recalc insigne
		if (userId != null) {
			Integer totalBadges = progressRepository.countBadgesForUser(userId);
			if (totalBadges == null)
				totalBadges = 0;
			userRepository.updateBadgeCount(userId, totalBadges);
		}

		return ResponseEntity.ok("Progress saved and badges updated.");
	}
	// CRUD SIMPLU (asemanator cu Modulul) - sql/BD

	@PostMapping
	public ResponseEntity<String> saveProgressItem(@RequestBody SelfPacedProgress p) {
		if (p.getProgressId() > 0) {
			progressRepository.updateProgressNative(p.getProgressId(), LocalDate.now(), p.getNumberOfPoints(),
					p.getScore());
			updateBadges(p.getEnrolmentId());
			return ResponseEntity.ok("Progress updated.");
		} else {
			progressRepository.insertProgressNative(p.getEnrolmentId(), p.getModuleId(), LocalDate.now(),
					p.getNumberOfPoints(), p.getScore());
			updateBadges(p.getEnrolmentId());
			return ResponseEntity.ok("Progress created.");
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteProgress(@PathVariable int id) {
		// am nevoie de enrolmentID sa recalc badge urile dupa stergere
		// intai il caut, apoi sterg
		var existing = progressRepository.findById(id);
		if (existing.isPresent()) {
			int enrolmentId = existing.get().getEnrolmentId();
			progressRepository.deleteProgressNative(id);
			updateBadges(enrolmentId);
			return ResponseEntity.ok("Progress deleted.");
		}
		return ResponseEntity.notFound().build();
	}

	private void updateBadges(int enrolmentId) {
		var enrolment = enrolmentRepository.findById(enrolmentId);
		if (enrolment.isPresent()) {
			int userId = enrolment.get().getUserId();
			Integer totalBadges = progressRepository.countBadgesForUser(userId);
			if (totalBadges == null)
				totalBadges = 0;
			userRepository.updateBadgeCount(userId, totalBadges);
		}
	}
}
