package com.example.l215404.freshlyanecommerceapp.models;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "customer")
public class Customer {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String email;
    private String password;
    private String profilePicture;
    private String gender;
    private boolean isCustomer;

    public Customer(String username, String email, String password, String profilePicture, String gender, boolean isCustomer) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.profilePicture = profilePicture;
        this.gender = gender;
        this.isCustomer = isCustomer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        gender = gender;
    }

    public boolean getIsCustomer() {
        return isCustomer;
    }

    public void setIsCustomer(boolean isCustomer) {
        isCustomer = isCustomer;
    }
}
