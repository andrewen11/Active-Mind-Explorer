/**
 * Clasa (Model) pentru Prezenta Live
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "LiveAttendance")

public class LiveAttendance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AttendanceID")
	private int attendanceId;

	@Column(name = "EnrolmentID")
	private int enrolmentId;

	@Column(name = "SessionID")
	private int sessionId;

	@Column(name = "Notes")
	private String notes;

	@Column(name = "Score")
	private int score;

	public LiveAttendance() {
	}

	public LiveAttendance(int attendanceId, int enrolmentId, int sessionId, String notes, int score) {
		this.attendanceId = attendanceId;
		this.enrolmentId = enrolmentId;
		this.sessionId = sessionId;
		this.notes = notes;
		this.score = score;
	}

	public int getAttendanceId() {
		return attendanceId;
	}

	public void setAttendanceId(int attendanceId) {
		this.attendanceId = attendanceId;
	}

	public int getEnrolmentId() {
		return enrolmentId;
	}

	public void setEnrolmentId(int enrolmentId) {
		this.enrolmentId = enrolmentId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
