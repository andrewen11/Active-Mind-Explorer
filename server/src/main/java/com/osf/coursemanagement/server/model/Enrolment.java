/**
 * Clasa (Model) pentru Inscriere
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Enrolments")

public class Enrolment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "EnrolmentID")
	private int enrolmentId;

	@Column(name = "UserID")
	private int userId;

	@Column(name = "CourseID")
	private int courseId;

	@Column(name = "EnrolmentDate")
	private LocalDate enrolmentDate;

	public Enrolment() {
	}

	public Enrolment(int enrolmentId, int userId, int courseId, LocalDate enrolmentDate) {
		this.enrolmentId = enrolmentId;
		this.userId = userId;
		this.courseId = courseId;
		this.enrolmentDate = enrolmentDate;
	}

	// relatie n la 1 (multe inscrieri -> un user)
	// joincolumn leaga de coloana userid din tabelul inscrieri
	@ManyToOne
	@JoinColumn(name = "UserID", insertable = false, updatable = false)
	@com.fasterxml.jackson.annotation.JsonIgnore // stop bucla json
	private User user;

	// relatie n la 1 (multe inscrieri -> un curs)
	@ManyToOne
	@JoinColumn(name = "CourseID", insertable = false, updatable = false)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Course course;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public int getEnrolmentId() {
		return enrolmentId;
	}

	public void setEnrolmentId(int enrolmentId) {
		this.enrolmentId = enrolmentId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public LocalDate getEnrolmentDate() {
		return enrolmentDate;
	}

	public void setEnrolmentDate(LocalDate enrolmentDate) {
		this.enrolmentDate = enrolmentDate;
	}
}
