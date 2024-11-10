package com.example.l215404.freshlyanecommerceapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.l215404.freshlyanecommerceapp.models.Vendor;

@Dao
public interface VendorDao {
    @Insert
    void insert(Vendor vendor);

    @Query("SELECT * FROM vendors WHERE username=:username")
    Vendor getVendorByUsername(String username);

    @Query("SELECT * FROM vendors WHERE id=:id")
    Vendor getVendorById(int id);

    @Query("SELECT * FROM vendors WHERE email = :email")
    Vendor findVendorByEmail(String email);

    @Insert
    void insertVendor(Vendor vendor);

    @Query("SELECT * FROM vendors WHERE email = :email AND password = :password")
    Vendor login(String email, String password);

    @Query("SELECT * FROM vendors WHERE email=:email")
    Vendor checkIfEmailExists(String email);
}
