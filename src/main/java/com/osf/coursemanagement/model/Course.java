/**
 * Clasa (Model) pentru Curs
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty; // adnotatii pt serializare/deserializare

public class Course {

	@JsonProperty("courseId")
	private int courseId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("courseDuration")
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

	@Override
	public String toString() {
		return name;
	}

	public int getId() {
		return courseId;
	}

	public void setId(int id) {
		this.courseId = id;
	}
}
