/**
 * Clasa (Repository) pentru Sesiune Live
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.repository;

import com.osf.coursemanagement.server.model.LiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LiveSessionRepository extends JpaRepository<LiveSession, Integer> {
	List<LiveSession> findByCourseId(int courseId); // aplicare jpa / orm

	// aplicare sql nativ (select)
	@org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM LiveSessions WHERE CourseID = :courseId", nativeQuery = true)
	Integer countSessionsByCourseId(@org.springframework.data.repository.query.Param("courseId") int courseId);
}
