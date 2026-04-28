package com.osf.coursemanagement.server.repository;

import com.osf.coursemanagement.server.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query; // Import added for @Query

/**
 * Clasa (Repository) pentru Curs
 * 
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

	// sql nativ aplicat (nativequery = true) - Joinuri

	// gasesc toate cursurile unde e inscris un user +
	// join simplu intre courses si enrolments
	@Query(value = "SELECT c.* FROM Courses c JOIN Enrolments e ON c.CourseID = e.CourseID WHERE e.UserID = :userId", nativeQuery = true)
	java.util.List<Course> findCoursesByUserId(@org.springframework.data.repository.query.Param("userId") int userId);

	// courses cu cel putin un modul de un anumit tip (ex: 'Salesforce Core Dev')
	// + join module
	@Query(value = "SELECT DISTINCT c.* FROM Courses c JOIN Modules_Trailhead m ON c.CourseID = m.CourseID WHERE m.ModuleType = :moduleType", nativeQuery = true)
	java.util.List<Course> findCoursesByModuleType(
			@org.springframework.data.repository.query.Param("moduleType") String moduleType);

	// subcereri - cursuri FARA inscrieri
	// cursrui fara inrolati
	@Query(value = "SELECT * FROM Courses c WHERE c.CourseID NOT IN (SELECT e.CourseID FROM Enrolments e)", nativeQuery = true)
	java.util.List<Course> findCoursesWithNoEnrolments();

	// subcerere - cursuri cu mai mult de X module (implicit are cel putin un modul)
	// corelat (COUNT > X)
	@Query(value = "SELECT * FROM Courses c WHERE (SELECT COUNT(*) FROM Modules_Trailhead m WHERE m.CourseID = c.CourseID) > :minModules", nativeQuery = true)
	java.util.List<Course> findCoursesWithMinModules(
			@org.springframework.data.repository.query.Param("minModules") long minModules);
}
