/**
 * Clasa (Model) pentru Sesiune Live
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class LiveSession {
	private int sessionId;
	private int courseId;
	private int trainerId;
	private String date; // folosesc string ca sa nu am probleme cu jackson jsr310
	// pt ca nu e suprascris (config ObjectMapper ul default)
	private int numberOfParticipants;
	private String sessionTheme;
	private String description;

	public LiveSession() {
	}

	public LiveSession(int sessionId, int courseId, int trainerId, String date, int numberOfParticipants,
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	// helper pt preluarea localdate din string
	@JsonIgnore
	public LocalDate getParsedDate() {
		try {
			return date != null ? LocalDate.parse(date) : null;
		} catch (Exception e) {
			return null;
		}
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

	@Override
	public String toString() {
		return sessionTheme;
	}
}
