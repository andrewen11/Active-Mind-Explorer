package com.osf.coursemanagement.model;

/**
 * Clasa pentru modelul utilizatorului (User)
 * 
 * @author Bălălău Andrei-Valentin
 * @version 2 - 11 Ianuarie 2026
 */
public class User {
	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String roleType; // admin/ trainer/ trainee/ collaborator
	private Integer trainerId;
	private int numberOfBadges;

	public User() {
	}

	// constructor complet
	public User(int id, String firstName, String lastName, String email, String password, String roleType) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.roleType = roleType;
	}

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

	// verif acces la app / doar adminii au acces la tot
	public boolean isAdmin() {
		return "Admin".equalsIgnoreCase(this.roleType);
	}

	public boolean hasGlobalViewAccess() {
		return "Admin".equalsIgnoreCase(this.roleType) ||
				"Collaborator".equalsIgnoreCase(this.roleType);
	}

	@Override
	public String toString() {
		return firstName + " " + lastName + " (" + roleType + ")";
	}
}