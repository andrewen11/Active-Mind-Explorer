/**
 * Clasa (Repository) pentru Utilizator
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.repository;

import com.osf.coursemanagement.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query; // Import added for @Query

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	User findByEmailAndPassword(String email, String password);

	boolean existsByEmail(String email);

	// aplicare sql nativ (joinuri)

	// gasesc userii inscrisi la un curs anume
	// folosesc join intre users si enrolments
	@Query(value = "SELECT u.* FROM Users u JOIN Enrolments e ON u.UserID = e.UserID WHERE e.CourseID = :courseId", nativeQuery = true)
	java.util.List<User> findUsersByCourseId(@org.springframework.data.repository.query.Param("courseId") int courseId);

	// gasesc userii care au fost la sesiuni live
	// join multiplu: users -> enrolments -> liveattendance
	@Query(value = "SELECT DISTINCT u.* FROM Users u JOIN Enrolments e ON u.UserID = e.UserID JOIN LiveAttendance la ON e.EnrolmentID = la.EnrolmentID", nativeQuery = true)
	java.util.List<User> findUsersWhoAttendedLiveSessions();

	// useri care NU s-au inscris nicaieri
	// deci folosesc subquery in clauza WHERE ... NOT IN (...)
	@Query(value = "SELECT * FROM Users u WHERE u.UserID NOT IN (SELECT e.UserID FROM Enrolments e)", nativeQuery = true)
	java.util.List<User> findUsersWithNoEnrolments();

	// useri cu mai mult de X inscrieri
	// deci subquery corelat in WHERE
	@Query(value = "SELECT * FROM Users u WHERE (SELECT COUNT(*) FROM Enrolments e WHERE e.UserID = u.UserID) > :minEnrolments", nativeQuery = true)
	java.util.List<User> findUsersWithMoreEnrolmentsThan(
			@org.springframework.data.repository.query.Param("minEnrolments") long minEnrolments);

	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@org.springframework.data.jpa.repository.Query(value = "UPDATE Users SET NumberOfBadges = :count WHERE UserID = :userId", nativeQuery = true)
	void updateBadgeCount(@org.springframework.data.repository.query.Param("userId") int userId,
			@org.springframework.data.repository.query.Param("count") int count);
}
