package com.example.l215404.freshlyanecommerceapp.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.l215404.freshlyanecommerceapp.models.Category;

import java.util.List;

@Dao
public interface CategoryDao {

    @Insert
    void insert(Category category);

    @Query("SELECT * FROM category WHERE name = :name LIMIT 1")
    Category getCategoryByName(String name);

    @Query("SELECT * FROM category")
    List<Category> getAllCategories();

    @Query("SELECT cid FROM category WHERE name=:name")
    int getCid(String name);
}
