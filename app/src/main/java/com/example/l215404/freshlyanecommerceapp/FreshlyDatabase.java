package com.example.l215404.freshlyanecommerceapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.l215404.freshlyanecommerceapp.dao.CategoryDao;
import com.example.l215404.freshlyanecommerceapp.dao.CustomerDao;
import com.example.l215404.freshlyanecommerceapp.dao.ProductDao;
import com.example.l215404.freshlyanecommerceapp.dao.VendorDao;
import com.example.l215404.freshlyanecommerceapp.models.Category;
import com.example.l215404.freshlyanecommerceapp.models.Customer;
import com.example.l215404.freshlyanecommerceapp.models.Product;
import com.example.l215404.freshlyanecommerceapp.models.Vendor;

import java.util.concurrent.Executors;

@Database(entities = {Vendor.class, Customer.class, Product.class, Category.class}, version = 9)
public abstract class FreshlyDatabase extends RoomDatabase {
    public abstract VendorDao vendorDao();
    public abstract CustomerDao customerDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();

    private static volatile FreshlyDatabase INSTANCE;

    public static synchronized FreshlyDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    FreshlyDatabase.class, "freshly_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
