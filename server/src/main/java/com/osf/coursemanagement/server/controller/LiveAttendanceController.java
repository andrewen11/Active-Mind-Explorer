/**
 * Clasa (Controller) pentru Prezenta Live
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.controller;

import com.osf.coursemanagement.server.repository.LiveAttendanceRepository;
import com.osf.coursemanagement.server.repository.AttendanceView;
import com.osf.coursemanagement.server.model.LiveAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/liveattendance")
public class LiveAttendanceController {

	@Autowired
	private LiveAttendanceRepository liveAttendanceRepository;

	@GetMapping("/session/{sessionId}")
	public ResponseEntity<List<Map<String, Object>>> getAttendanceBySession(@PathVariable int sessionId) {
		List<AttendanceView> results = liveAttendanceRepository.findBySessionIdWithStudentName(sessionId);
		List<Map<String, Object>> attendanceList = new ArrayList<>();

		for (AttendanceView row : results) {
			Map<String, Object> map = new HashMap<>();
			map.put("enrolmentId", row.getEnrolmentId());
			map.put("attendanceId", row.getAttendanceId());
			
			String fName = row.getFirstName() != null ? row.getFirstName().trim() : "";
			String lName = row.getLastName() != null ? row.getLastName().trim() : "";
			map.put("studentName", fName + " " + lName);
			
			map.put("score", row.getScore());
			map.put("notes", row.getNotes());
			attendanceList.add(map);
		}

		return ResponseEntity.ok(attendanceList);
	}

	@PostMapping("/session/{sessionId}/save")
	@org.springframework.transaction.annotation.Transactional
	public ResponseEntity<?> saveAttendance(@PathVariable int sessionId,
			@RequestBody List<Map<String, Object>> attendanceData) {
		try {
			// 1. Fetch all existing attendance for this session
			List<LiveAttendance> allExisting = liveAttendanceRepository.findBySessionId(sessionId);

			// 2. Identify incoming Enrolment IDs
			List<Integer> incomingEnrolmentIds = new ArrayList<>();
			for (Map<String, Object> data : attendanceData) {
				incomingEnrolmentIds.add(Integer.parseInt(data.get("enrolmentId").toString()));
			}

			// 3. Delete records not in the incoming list
			for (LiveAttendance existing : allExisting) {
				if (!incomingEnrolmentIds.contains(existing.getEnrolmentId())) {
					liveAttendanceRepository.delete(existing);
				}
			}

			// 4. Upsert (Update or Insert)
			for (Map<String, Object> data : attendanceData) {
				int enrolmentId = Integer.parseInt(data.get("enrolmentId").toString());
				int score = Integer.parseInt(data.get("score").toString());
				String notes = (String) data.getOrDefault("notes", "");

				LiveAttendance existing = allExisting.stream()
						.filter(a -> a.getEnrolmentId() == enrolmentId)
						.findFirst()
						.orElse(null);

				if (existing != null) {
					existing.setScore(score);
					existing.setNotes(notes);
					liveAttendanceRepository.save(existing);
				} else {
					LiveAttendance newRecord = new LiveAttendance();
					newRecord.setSessionId(sessionId);
					newRecord.setEnrolmentId(enrolmentId);
					newRecord.setScore(score);
					newRecord.setNotes(notes);
					liveAttendanceRepository.save(newRecord);
				}
			}
			return ResponseEntity.ok("Saved");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body("Error saving attendance");
		}
	}
}
