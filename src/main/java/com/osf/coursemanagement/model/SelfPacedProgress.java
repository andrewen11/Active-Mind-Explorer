/**
 * Clasa (Model) pentru Progres Self-Paced
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SelfPacedProgress {
	private int progressId;
	private int enrolmentId;
	private int moduleId;
	private String completionDate; // schimbat in string pt compatibilitate
	private int numberOfPoints; // pct max / snapshot la finalizare
	private int score; // scor obtinut

	public SelfPacedProgress() {
	}

	public SelfPacedProgress(int enrolmentId, int moduleId, int score, int maxPoints, String completionDate) {
		this.enrolmentId = enrolmentId;
		this.moduleId = moduleId;
		this.score = score;
		this.numberOfPoints = maxPoints;
		this.completionDate = completionDate;
	}

	public int getProgressId() {
		return progressId;
	}

	public void setProgressId(int progressId) {
		this.progressId = progressId;
	}

	public int getEnrolmentId() {
		return enrolmentId;
	}

	public void setEnrolmentId(int enrolmentId) {
		this.enrolmentId = enrolmentId;
	}

	public int getModuleId() {
		return moduleId;
	}

	public void setModuleId(int moduleId) {
		this.moduleId = moduleId;
	}

	public String getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(String completionDate) {
		this.completionDate = completionDate;
	}

	@com.fasterxml.jackson.annotation.JsonIgnore
	public LocalDate getParsedDate() {
		try {
			return completionDate != null ? LocalDate.parse(completionDate) : null;
		} catch (Exception e) {
			return null;
		}
	}

	public int getNumberOfPoints() {
		return numberOfPoints;
	}

	public void setNumberOfPoints(int numberOfPoints) {
		this.numberOfPoints = numberOfPoints;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
