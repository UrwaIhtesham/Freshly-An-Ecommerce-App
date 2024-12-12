package com.example.l215404.freshlyanecommerceapp.Activities.HomePage;

public class Product {
    private int id;
    private String name;
    private String description;
    private int vendorId;
    private int price;
    private String imageSource;

    public Product(int id, String name, String description, int vendorid, int price, String imageSource) {
        this.id=id;
        this.name = name;
        this.description = description;
        this.vendorId = vendorid;
        this.price = price;
        this.imageSource = imageSource;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVendorId() {
        return vendorId;
    }

    public void setVendorId(int vendorId) {
        this.vendorId = vendorId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }
}
