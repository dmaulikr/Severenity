package com.severenity.entity;

import android.net.Uri;

import java.net.URI;

/**
 * Created by Odinn on 04.09.2016.
 */
public class ShopItem {

    private String title;
    private int imageURL;
    private String description;
    private String price;



    public ShopItem(String title, int imageURL, String description, String price) {
        this.title = title;
        this.imageURL = imageURL;
        this.description = description;
        this.price = price;
    }

    public ShopItem(String title, int imageURL, String description) {
        this.title = title;
        this.imageURL = imageURL;
        this.description = description;
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
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }



}
