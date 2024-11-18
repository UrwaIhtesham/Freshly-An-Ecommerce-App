package com.example.l215404.freshlyanecommerceapp.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "product",
 foreignKeys = {
         @ForeignKey(entity = Vendor.class,
                 parentColumns = "id",
                 childColumns = "vendor_id",
                 onDelete = ForeignKey.CASCADE),
         @ForeignKey(entity = Category.class,
                 parentColumns = "cid",
                 childColumns = "category_id",
                 onDelete = ForeignKey.CASCADE)
 })
public class Product {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private String image;
    private int price;
    private int category_id;
    private int vendor_id;

    public Product(String title, String description, String image, int price, int category_id, int vendor_id) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.price = price;
        this.category_id = category_id;
        this.vendor_id = vendor_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(int vendor_id) {
        this.vendor_id = vendor_id;
    }
}
