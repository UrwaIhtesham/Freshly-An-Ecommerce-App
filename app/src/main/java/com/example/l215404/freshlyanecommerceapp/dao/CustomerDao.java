package com.example.l215404.freshlyanecommerceapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.l215404.freshlyanecommerceapp.models.Customer;

@Dao
public interface CustomerDao {
    @Insert
    void insertCustomer(Customer customer);

    @Query("SELECT * FROM customer WHERE email= :email AND password = :password")
    Customer login(String email, String password);

    @Query("SELECT * FROM customer WHERE email= :email")
    Customer checkIFEmailExists(String email);
}
