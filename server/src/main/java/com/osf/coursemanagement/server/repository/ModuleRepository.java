/**
 * Clasa (Repository) pentru Modul
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.repository;

import com.osf.coursemanagement.server.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {
	// gaseste toate modulele unui curs
	List<Module> findByCourseId(int courseId);

	@org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM Modules_Trailhead WHERE CourseID = :courseId", nativeQuery = true)
	Integer countModulesByCourseId(@org.springframework.data.repository.query.Param("courseId") int courseId);

	// aplicare sql nativ (insert, update, delete) / crud - SQL nativ
	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@org.springframework.data.jpa.repository.Query(value = "INSERT INTO Modules_Trailhead (CourseID, Name, Description, CompletionCriteria, ModuleType, NumberOfPoints) VALUES (:courseId, :name, :description, :completionCriteria, :moduleType, :numberOfPoints)", nativeQuery = true)
	void insertModuleNative(
			@org.springframework.data.repository.query.Param("courseId") int courseId,
			@org.springframework.data.repository.query.Param("name") String name,
			@org.springframework.data.repository.query.Param("description") String description,
			@org.springframework.data.repository.query.Param("completionCriteria") String completionCriteria,
			@org.springframework.data.repository.query.Param("moduleType") String moduleType,
			@org.springframework.data.repository.query.Param("numberOfPoints") int numberOfPoints);

	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@org.springframework.data.jpa.repository.Query(value = "UPDATE Modules_Trailhead SET CourseID = :courseId, Name = :name, Description = :description, CompletionCriteria = :completionCriteria, ModuleType = :moduleType, NumberOfPoints = :numberOfPoints WHERE ModuleID = :moduleId", nativeQuery = true)
	void updateModuleNative(
			@org.springframework.data.repository.query.Param("moduleId") int moduleId,
			@org.springframework.data.repository.query.Param("courseId") int courseId,
			@org.springframework.data.repository.query.Param("name") String name,
			@org.springframework.data.repository.query.Param("description") String description,
			@org.springframework.data.repository.query.Param("completionCriteria") String completionCriteria,
			@org.springframework.data.repository.query.Param("moduleType") String moduleType,
			@org.springframework.data.repository.query.Param("numberOfPoints") int numberOfPoints);

	@org.springframework.data.jpa.repository.Modifying
	@org.springframework.transaction.annotation.Transactional
	@org.springframework.data.jpa.repository.Query(value = "DELETE FROM Modules_Trailhead WHERE ModuleID = :moduleId", nativeQuery = true)
	void deleteModuleNative(@org.springframework.data.repository.query.Param("moduleId") int moduleId);
}
