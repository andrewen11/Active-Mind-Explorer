/**
 * Clasa (Model) pentru Sesiune Live
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "LiveSessions")

public class LiveSession {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SessionID")
	private int sessionId;

	@Column(name = "CourseID")
	private int courseId;

	@Column(name = "TrainerID")
	private int trainerId;

	@Column(name = "Date")
	private LocalDate date;

	@Column(name = "NumberOfParticipants")
	private int numberOfParticipants;

	@Column(name = "SessionTheme")
	private String sessionTheme;

	@Column(name = "Description")
	private String description;

	public LiveSession() {
	}

	public LiveSession(int sessionId, int courseId, int trainerId, LocalDate date, int numberOfParticipants,
			String sessionTheme, String description) {
		this.sessionId = sessionId;
		this.courseId = courseId;
		this.trainerId = trainerId;
		this.date = date;
		this.numberOfParticipants = numberOfParticipants;
		this.sessionTheme = sessionTheme;
		this.description = description;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public int getCourseId() {
		return courseId;
	}

	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}

	public int getTrainerId() {
		return trainerId;
	}

	public void setTrainerId(int trainerId) {
		this.trainerId = trainerId;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public int getNumberOfParticipants() {
		return numberOfParticipants;
	}

	public void setNumberOfParticipants(int numberOfParticipants) {
		this.numberOfParticipants = numberOfParticipants;
	}

	public String getSessionTheme() {
		return sessionTheme;
	}

	public void setSessionTheme(String sessionTheme) {
		this.sessionTheme = sessionTheme;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
