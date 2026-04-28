/**
 * Clasa (Service) pentru Progres
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.service;

import com.osf.coursemanagement.server.model.SelfPacedProgress;
import com.osf.coursemanagement.server.repository.SelfPacedProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProgressService {

	@Autowired
	private SelfPacedProgressRepository progressRepository;

	public List<SelfPacedProgress> getAllProgress() {
		return progressRepository.findAll();
	}

	// crud sql nativ

	public void addProgress(SelfPacedProgress progress) {
		progressRepository.insertProgressNative(
				progress.getEnrolmentId(),
				progress.getModuleId(),
				progress.getCompletionDate(),
				progress.getNumberOfPoints(),
				progress.getScore());
	}

	public void updateProgress(SelfPacedProgress progress) {
		progressRepository.updateProgressNative(
				progress.getProgressId(),
				progress.getCompletionDate(),
				progress.getNumberOfPoints(),
				progress.getScore());
	}

	public void deleteProgress(int progressId) {
		progressRepository.deleteProgressNative(progressId);
	}
}
