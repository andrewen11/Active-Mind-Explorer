package com.osf.coursemanagement.model;

public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String roleType; // 'Admin', 'Trainer', 'Trainee', 'Collaborator'

    public User() {}

    // Constructor complet
    public User(int id, String firstName, String lastName, String email, String password, String roleType) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roleType = roleType;
    }

    public int getId()
    {
        return id;
    }
    public void setId(int id)
    {
        this.id = id;
    }

    public String getFirstName()
    {
        return firstName;
    }
    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }
    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getRoleType()
    {
        return roleType;
    }
    public void setRoleType(String roleType)
    {
        this.roleType = roleType;
    }

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