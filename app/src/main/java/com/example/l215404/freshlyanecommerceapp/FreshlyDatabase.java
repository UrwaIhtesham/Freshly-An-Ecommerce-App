package com.example.l215404.freshlyanecommerceapp;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.l215404.freshlyanecommerceapp.dao.VendorDao;
import com.example.l215404.freshlyanecommerceapp.models.Category;
import com.example.l215404.freshlyanecommerceapp.models.Customer;
import com.example.l215404.freshlyanecommerceapp.models.Product;
import com.example.l215404.freshlyanecommerceapp.models.Vendor;

@Database(entities = {Vendor.class, Customer.class, Product.class, Category.class}, version = 1)
public abstract class FreshlyDatabase extends RoomDatabase {
    public abstract VendorDao vendorDao();

    private static volatile FreshlyDatabase INSTANCE;

    public static FreshlyDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (FreshlyDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            FreshlyDatabase.class, "freshly_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
