/**
 * Clasa (Model) pentru Utilizator
 *
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
package com.osf.coursemanagement.server.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Users")

public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "UserID")
	private int id;

	@Column(name = "FirstName")
	private String firstName;

	@Column(name = "LastName")
	private String lastName;

	@Column(name = "Email")
	private String email;

	@Column(name = "Password")
	private String password;

	@Column(name = "RoleType")
	private String roleType;

	@Column(name = "TrainerID")
	private Integer trainerId; // Integer allows null

	@Column(name = "NumberOfBadges")
	private int numberOfBadges;

	// Constructor gol (obligatoriu pentru JPA)
	public User() {
	}

	// Constructor cu toti parametrii
	public User(int id, String firstName, String lastName, String email, String password, String roleType,
			Integer trainerId, int numberOfBadges) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.roleType = roleType;
		this.trainerId = trainerId;
		this.numberOfBadges = numberOfBadges;
	}

	// Getteri si Setteri (metodele clasice pe care le stii)
	public Integer getTrainerId() {
		return trainerId;
	}

	public void setTrainerId(Integer trainerId) {
		this.trainerId = trainerId;
	}

	public int getNumberOfBadges() {
		return numberOfBadges;
	}

	public void setNumberOfBadges(int numberOfBadges) {
		this.numberOfBadges = numberOfBadges;
	}

	// Getteri si Setteri (metodele clasice pe care le stii)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRoleType() {
		return roleType;
	}

	public void setRoleType(String roleType) {
		this.roleType = roleType;
	}

	// relatie 1 la n (un user -> mai multe inscrieri)
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@com.fasterxml.jackson.annotation.JsonIgnore
	private java.util.List<Enrolment> enrolments;

	public java.util.List<Enrolment> getEnrolments() {
		return enrolments;
	}

	public void setEnrolments(java.util.List<Enrolment> enrolments) {
		this.enrolments = enrolments;
	}

	@Override
	public String toString() {
		return "User{" +
				"email='" + email + '\'' +
				", roleType='" + roleType + '\'' +
				'}';
	}
}
