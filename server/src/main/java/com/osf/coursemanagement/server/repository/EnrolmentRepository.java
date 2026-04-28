package com.osf.coursemanagement.server.repository;

import com.osf.coursemanagement.server.model.Enrolment;
import com.osf.coursemanagement.server.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Clasa (Repository) pentru Inscriere
 * 
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
@Repository
public interface EnrolmentRepository extends JpaRepository<Enrolment, Integer> {
	// "gasesc cursurile studentului X" cu subquery
	@Query(value = "SELECT c.* FROM Courses c JOIN Enrolments e ON c.CourseID = e.CourseID WHERE e.UserID = :userId", nativeQuery = true)
	List<Course> findCoursesByUserId(@Param("userId") int userId);

	// + inrolarile unui student
	List<Enrolment> findByUserId(int userId);

	/*
	 * interog simple - la cursuri care au cuvantul cheie in descriere, dar
	 * neutilizata in etapa finala
	 *
	 * @Query(value =
	 * "SELECT e.* FROM Enrolments e JOIN Courses c ON e.CourseID = c.CourseID WHERE c.Description LIKE %:keyword%"
	 * , nativeQuery = true)
	 * List<Enrolment> findEnrolmentsByCourseDescription(@Param("keyword") String
	 * keyword);
	 *
	 * @Query(value =
	 * "SELECT e.* FROM Enrolments e JOIN Users u ON e.UserID = u.UserID WHERE u.LastName = :lastName"
	 * , nativeQuery = true)
	 * List<Enrolment> findEnrolmentsByUserLastName(@Param("lastName") String
	 * lastName);
	 */

	// numar cati sunt inscrisi la un curs
	@Query(value = "SELECT COUNT(*) FROM Enrolments WHERE CourseID = :courseId", nativeQuery = true)
	int countByCourseId(@Param("courseId") int courseId);

	// iau toate inscrierile pt un curs (pt lista participanti)
	@Query(value = "SELECT * FROM Enrolments WHERE CourseID = :courseId", nativeQuery = true)
	List<Enrolment> findByCourseId(@Param("courseId") int courseId);
}
