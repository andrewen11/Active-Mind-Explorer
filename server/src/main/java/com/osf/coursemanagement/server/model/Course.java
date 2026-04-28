/**
 * Clasa (Model) pentru Curs
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Courses")

public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CourseID")
	private int courseId;

	@Column(name = "Name")
	private String name;

	@Column(name = "Description")
	private String description;

	@Column(name = "CourseDuration")
	private String courseDuration;

	public Course() {
	}

	public Course(int courseId, String name, String description, String courseDuration) {
		this.courseId = courseId;
		this.name = name;
		this.description = description;
		this.courseDuration = courseDuration;
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

	public String getCourseDuration() {
		return courseDuration;
	}

	public void setCourseDuration(String courseDuration) {
		this.courseDuration = courseDuration;
	}

	// relatie 1 la n (un curs -> mai multe module)
	// cascade.all - daca sterg cursul, se sterg si modulele automat
	// fetch.lazy - nu incarc modulele decat cand am nevoie (performanta)
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@com.fasterxml.jackson.annotation.JsonIgnore // nu serializam lista inapoi ca face bucla infinita
	private java.util.List<Module> modules;

	// relatie 1 la n (un curs -> mai multe inscrieri)
	@OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private java.util.List<Enrolment> enrolments;

	public java.util.List<Module> getModules() {
		return modules;
	}

	public void setModules(java.util.List<Module> modules) {
		this.modules = modules;
	}

	public java.util.List<Enrolment> getEnrolments() {
		return enrolments;
	}

	public void setEnrolments(java.util.List<Enrolment> enrolments) {
		this.enrolments = enrolments;
	}

	@Override
	public String toString() {
		return name;
	}
}
