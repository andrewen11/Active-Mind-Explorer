/**
 * Clasa (Model) pentru Modul
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Modules_Trailhead")

public class Module {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ModuleID")
	private int moduleId;

	@Column(name = "CourseID")
	private int courseId;

	@Column(name = "Name")
	private String name;

	@Column(name = "Description")
	private String description;

	@Column(name = "CompletionCriteria")
	private String completionCriteria;

	@Column(name = "ModuleType")
	private String moduleType;

	@Column(name = "NumberOfPoints")
	private int numberOfPoints;

	public Module() {
	}

	public Module(int moduleId, int courseId, String name, String description, String completionCriteria,
			String moduleType, int numberOfPoints) {
		this.moduleId = moduleId;
		this.courseId = courseId;
		this.name = name;
		this.description = description;
		this.completionCriteria = completionCriteria;
		this.moduleType = moduleType;
		this.numberOfPoints = numberOfPoints;
	}

	@ManyToOne
	@JoinColumn(name = "CourseID", insertable = false, updatable = false)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Course course;

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCompletionCriteria() {
		return completionCriteria;
	}

	public void setCompletionCriteria(String completionCriteria) {
		this.completionCriteria = completionCriteria;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public void setNumberOfPoints(int numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}
}
