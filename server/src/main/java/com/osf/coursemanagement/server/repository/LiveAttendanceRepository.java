/**
 * Clasa (Repository) pentru Prezenta Live
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.repository;

import com.osf.coursemanagement.server.model.LiveAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface LiveAttendanceRepository extends JpaRepository<LiveAttendance, Integer> {

	// calculez media scorului pt un user (prin inscrieri)
	// sql nativ cu functie AVG
	@Query(value = "SELECT AVG(la.Score) FROM LiveAttendance la JOIN Enrolments e ON la.EnrolmentID = e.EnrolmentID WHERE e.UserID = :userId", nativeQuery = true)
	Double findAverageScoreByUserId(@Param("userId") int userId);

	// calculez scor total pt user la un curs
	// sql nativ cu SUM si joinuri
	@Query(value = "SELECT SUM(la.Score) FROM LiveAttendance la JOIN Enrolments e ON la.EnrolmentID = e.EnrolmentID WHERE e.UserID = :userId AND e.CourseID = :courseId", nativeQuery = true)
	Double findTotalScoreByUserIdAndCourseId(@Param("userId") int userId, @Param("courseId") int courseId);

	// preiau prezenta cu nume student pt o sesiune
	// proiectie spring data (interfata view) pt type safety
	// query complex cu multiple joinuri (liveattendance -> enrolments -> users)
	@Query(value = "SELECT la.EnrolmentID as enrolmentId, la.AttendanceID as attendanceId, u.FirstName as firstName, u.LastName as lastName, la.Score as score, la.Notes as notes "
			+
			"FROM LiveAttendance la " +
			"JOIN Enrolments e ON la.EnrolmentID = e.EnrolmentID " +
			"JOIN Users u ON e.UserID = u.UserID " +
			"WHERE la.SessionID = :sessionId", nativeQuery = true)
	List<AttendanceView> findBySessionIdWithStudentName(@Param("sessionId") int sessionId);

	// gaseste date prin intermediul SessionId + EnrolmentId (pt operatii de tip
	// update)
	List<LiveAttendance> findBySessionIdAndEnrolmentId(int sessionId, int enrolmentId);

	// gaseste toate datele pentru o sesiune (pt operatii de tip bulk)
	List<LiveAttendance> findBySessionId(int sessionId);

	// gaseste toate datele pentru o inscriere (pt cascade delete)
	List<LiveAttendance> findByEnrolmentId(int enrolmentId);
}
