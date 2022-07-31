package com.federicoberon.estilocafe.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

public class ProductModel {
    private String productID;
    private String name;
    private String rating;
    private float price;
    private List<String> images;
    private String description;
    private String category;
    private String offer;


    public ProductModel(String productID, String name, String rating, float price, List<String> images, String description, String category, String offer) {
        this.productID = productID;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.images = images;
        this.description = description;
        this.category = category;
        this.offer = offer;
    }

    @Ignore
    public ProductModel() {
    }

    @Ignore
    public ProductModel(String productID, String name, String rating, float price, List<String> images, String description) {
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.images = images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public String getImagesAsString() {
        String result = images.get(0);
        for(int i=1; i<images.size(); i++)
            result.concat(",").concat(images.get(i));
        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public float getPrice() {
        return price;
    }

    public String getPricingString() {
        return "$"+ price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
