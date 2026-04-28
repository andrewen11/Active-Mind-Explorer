/**
 * Interfata (View) pentru proiectie Prezenta
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.repository;

// interfata pt proiectie (iau doar ce campuri imi trebuie din join)

public interface AttendanceView {
	Integer getEnrolmentId();

	Integer getAttendanceId();

	String getFirstName();

	String getLastName();

	Integer getScore();

	String getNotes();
}
