/**
 * Clasa (Service) pentru Modul
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.service;

import com.osf.coursemanagement.server.model.Module;
import com.osf.coursemanagement.server.repository.ModuleRepository;
import com.osf.coursemanagement.server.repository.SelfPacedProgressRepository;
import com.osf.coursemanagement.server.model.SelfPacedProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModuleService {

	@Autowired
	private ModuleRepository moduleRepository;

	public List<Module> getModulesByCourse(int courseId) {
		return moduleRepository.findByCourseId(courseId);
	}

	// crud sql nativ
	public void addModule(Module module) {
		moduleRepository.insertModuleNative(
				module.getCourseId(),
				module.getName(),
				module.getDescription(),
				module.getCompletionCriteria(),
				module.getModuleType(),
				module.getNumberOfPoints());
	}

	public void updateModule(Module module) {
		moduleRepository.updateModuleNative(
				module.getModuleId(),
				module.getCourseId(),
				module.getName(),
				module.getDescription(),
				module.getCompletionCriteria(),
				module.getModuleType(),
				module.getNumberOfPoints());
	}

	@Autowired
	private SelfPacedProgressRepository progressRepository;

	public void deleteModule(int moduleId) {
		// cascada: sterg progres - aplicare
		List<SelfPacedProgress> progressList = progressRepository
				.findByModuleId(moduleId);
		progressRepository.deleteAll(progressList);

		moduleRepository.deleteModuleNative(moduleId);
	}
}
