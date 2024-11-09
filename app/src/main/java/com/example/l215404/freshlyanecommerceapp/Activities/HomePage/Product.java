package com.example.l215404.freshlyanecommerceapp.Activities.HomePage;

public class Product {
    private String name;
    private String description;
    private int price;
    private int imageSource;

    public Product(String name, String description, int price, int imageSource) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageSource = imageSource;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImageSource() {
        return imageSource;
    }

    public void setImageSource(int imageSource) {
        this.imageSource = imageSource;
    }
}
