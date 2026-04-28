/**
 * Clasa (Model) pentru Progres Self-Paced
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "SelfPacedProgress")

public class SelfPacedProgress {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ProgressID")
	private int progressId;

	@Column(name = "EnrolmentID")
	private int enrolmentId;

	@Column(name = "ModuleID")
	private int moduleId;

	@Column(name = "CompletionDate")
	private LocalDate completionDate;

	@Column(name = "NumberOfPoints")
	private int numberOfPoints;

	@Column(name = "Score")
	private int score;

	public SelfPacedProgress() {
	}

	public SelfPacedProgress(int progressId, int enrolmentId, int moduleId, LocalDate completionDate,
			int numberOfPoints, int score) {
		this.progressId = progressId;
		this.enrolmentId = enrolmentId;
		this.moduleId = moduleId;
		this.completionDate = completionDate;
		this.numberOfPoints = numberOfPoints;
		this.score = score;
	}

	// Getteri si Setteri
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

	public LocalDate getCompletionDate() {
		return completionDate;
	}

	public void setCompletionDate(LocalDate completionDate) {
		this.completionDate = completionDate;
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
