/**
 * Clasa (Repository) pentru Progres Self-Paced
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.repository;

import com.osf.coursemanagement.server.model.SelfPacedProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SelfPacedProgressRepository extends JpaRepository<SelfPacedProgress, Integer> {

	// aplicare sql nativ (insert, update, delete) / crud

	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@org.springframework.data.jpa.repository.Query(value = "INSERT INTO SelfPacedProgress (EnrolmentID, ModuleID, CompletionDate, NumberOfPoints, Score) VALUES (:enrolmentId, :moduleId, :completionDate, :numberOfPoints, :score)", nativeQuery = true) // insert
	void insertProgressNative(
			@org.springframework.data.repository.query.Param("enrolmentId") int enrolmentId,
			@org.springframework.data.repository.query.Param("moduleId") int moduleId,
			@org.springframework.data.repository.query.Param("completionDate") java.time.LocalDate completionDate,
			@org.springframework.data.repository.query.Param("numberOfPoints") int numberOfPoints,
			@org.springframework.data.repository.query.Param("score") int score);

	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@org.springframework.data.jpa.repository.Query(value = "UPDATE SelfPacedProgress SET CompletionDate = :completionDate, NumberOfPoints = :numberOfPoints, Score = :score WHERE ProgressID = :progressId", nativeQuery = true)
	void updateProgressNative(
			@org.springframework.data.repository.query.Param("progressId") int progressId,
			@org.springframework.data.repository.query.Param("completionDate") java.time.LocalDate completionDate,
			@org.springframework.data.repository.query.Param("numberOfPoints") int numberOfPoints,
			@org.springframework.data.repository.query.Param("score") int score);

	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@org.springframework.data.jpa.repository.Query(value = "DELETE FROM SelfPacedProgress WHERE ProgressID = :progressId", nativeQuery = true)
	void deleteProgressNative(@org.springframework.data.repository.query.Param("progressId") int progressId);

	@org.springframework.data.jpa.repository.Query(value = "SELECT SUM(sp.Score) FROM SelfPacedProgress sp JOIN Enrolments e ON sp.EnrolmentID = e.EnrolmentID WHERE e.UserID = :userId AND e.CourseID = :courseId", nativeQuery = true)
	Double findTotalScoreByUserIdAndCourseId(@org.springframework.data.repository.query.Param("userId") int userId,
			@org.springframework.data.repository.query.Param("courseId") int courseId);

	java.util.List<SelfPacedProgress> findByEnrolmentId(int enrolmentId);

	java.util.List<SelfPacedProgress> findByModuleId(int moduleId);

	// nr badges pt user - pt superbadge e 3 la un scor >= 50%,
	// respectiv 1 badge pt < 50 %, pt badge e 1 indif de punctaj
	@org.springframework.data.jpa.repository.Query(value = "SELECT SUM(CASE WHEN TRIM(UPPER(m.ModuleType)) = 'SUPERBADGE' THEN (CASE WHEN sp.Score >= 50 THEN 3 WHEN sp.Score > 0 THEN 1 ELSE 0 END) ELSE (CASE WHEN sp.Score > 0 THEN 1 ELSE 0 END) END) FROM SelfPacedProgress sp JOIN Enrolments e ON sp.EnrolmentID = e.EnrolmentID JOIN Modules_Trailhead m ON sp.ModuleID = m.ModuleID WHERE e.UserID = :userId", nativeQuery = true)
	Integer countBadgesForUser(@org.springframework.data.repository.query.Param("userId") int userId);

	@org.springframework.data.jpa.repository.Query(value = "SELECT SUM(CASE WHEN TRIM(UPPER(m.ModuleType)) = 'SUPERBADGE' THEN (CASE WHEN sp.Score >= 50 THEN 3 WHEN sp.Score > 0 THEN 1 ELSE 0 END) ELSE (CASE WHEN sp.Score > 0 THEN 1 ELSE 0 END) END) FROM SelfPacedProgress sp JOIN Enrolments e ON sp.EnrolmentID = e.EnrolmentID JOIN Modules_Trailhead m ON sp.ModuleID = m.ModuleID WHERE e.UserID = :userId AND e.CourseID = :courseId", nativeQuery = true)
	Integer countBadgesForUserAndCourse(@org.springframework.data.repository.query.Param("userId") int userId,
			@org.springframework.data.repository.query.Param("courseId") int courseId);
}
