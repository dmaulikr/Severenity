package com.severenity.entity;

/**
 * Created by Novosad on 04.09.2016.
 */
public class ShopItem {

    private String title;
    private int imageURL;
    private String description;
    private double price;
    private double credits;

    public ShopItem(String title, int imageURL, String description, double credits, double price) {
        this.title = title;
        this.imageURL = imageURL;
        this.description = description;
        this.price = price;
        this.credits = credits;
    }

    public ShopItem(String title, int imageURL, String description, double credits) {
        this.title = title;
        this.imageURL = imageURL;
        this.description = description;
        this.credits = credits;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImageURL() {
        return imageURL;
    }

    public void setImageURL(int imageURL) {
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }
}
