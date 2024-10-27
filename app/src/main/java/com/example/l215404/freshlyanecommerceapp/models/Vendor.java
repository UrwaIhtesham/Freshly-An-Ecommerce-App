package com.example.l215404.freshlyanecommerceapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vendors")
public class Vendor {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String username;
    private String email;
    private String password;
    private String ProfilePicture;
    private String Address;
    private String phone;

    public Vendor(int id, String username, String email, String password, String profilePicture, String address, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        ProfilePicture = profilePicture;
        Address = address;
        this.phone = phone;
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

    public String getEmail(){
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
        return ProfilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        ProfilePicture = profilePicture;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
