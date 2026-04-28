/**
 * Clasa (Service) pentru Inscriere
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.service;

import com.osf.coursemanagement.server.model.Course;
import com.osf.coursemanagement.server.model.Enrolment;
import com.osf.coursemanagement.server.model.User;
import com.osf.coursemanagement.server.repository.EnrolmentRepository;
import com.osf.coursemanagement.server.repository.LiveSessionRepository;
import com.osf.coursemanagement.server.repository.LiveAttendanceRepository;
import com.osf.coursemanagement.server.repository.SelfPacedProgressRepository;
import com.osf.coursemanagement.server.model.SelfPacedProgress;
import com.osf.coursemanagement.server.model.LiveAttendance;
import com.osf.coursemanagement.server.model.LiveSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnrolmentService {

	@Autowired
	private EnrolmentRepository enrolmentRepository;

	public Enrolment enrol(int userId, int courseId) {
		// verific daca e deja inscris (sa nu dublez)
		List<Enrolment> existing = enrolmentRepository.findByUserId(userId);
		for (Enrolment e : existing) {
			if (e.getCourseId() == courseId) {
				return e; // deja inscris
			}
		}

		Enrolment enrolment = new Enrolment();
		enrolment.setUserId(userId);
		enrolment.setCourseId(courseId);
		enrolment.setCourseId(courseId);

		return enrolmentRepository.save(enrolment);
	}

	@Autowired
	private LiveSessionRepository liveSessionRepository;

	@Autowired
	private LiveAttendanceRepository liveAttendanceRepository;

	@Autowired
	private SelfPacedProgressRepository progressRepository;

	// eliminarea de la curs - in etape!
	public void unenrol(int enrolmentId) {
		// iau inscrierea, imi trebuie courseid
		Enrolment enrolment = enrolmentRepository.findById(enrolmentId).orElse(null);
		if (enrolment == null) {
			return; // nu exista
		}
		// sterg progres
		List<SelfPacedProgress> progressList = progressRepository
				.findByEnrolmentId(enrolmentId);
		progressRepository.deleteAll(progressList);

		// sterg prezenta si actualizez contor sesiuni
		List<LiveAttendance> attendanceList = liveAttendanceRepository
				.findByEnrolmentId(enrolmentId);

		for (LiveAttendance attendance : attendanceList) {
			// scad nr participanti
			LiveSession session = liveSessionRepository
					.findById(attendance.getSessionId()).orElse(null);

			if (session != null) {
				int current = session.getNumberOfParticipants();
				if (current > 0) {
					session.setNumberOfParticipants(current - 1);
					liveSessionRepository.save(session);
				}
			}
		}

		liveAttendanceRepository.deleteAll(attendanceList);

		// sterg inscrierea propriu zisa abia la final pt evitarea coruperii datelelor
		enrolmentRepository.deleteById(enrolmentId);
	}

	public List<Enrolment> getEnrolmentsByCourse(int courseId) {
		return enrolmentRepository.findByCourseId(courseId);
	}
}
