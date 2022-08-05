package com.federicoberon.estilocafe.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "products", indices = {@Index(value = {"idFirebase"},
        unique = true)})
public class ProductEntity {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    @NonNull
    private String idFirebase;
    private String name;
    private String rating;
    private float price;
    private String images;
    private String description;
    private String category;
    private String offer;

    @Ignore
    public ProductEntity() {
    }

    public ProductEntity(@NonNull Long id, String idFirebase, String name, String rating, float price, String images, String description, String category, String offer) {
        this.id = id;
        this.idFirebase = idFirebase;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.images = images;
        this.description = description;
        this.category = category;
        this.offer = offer;
    }

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getIdFirebase() {
        return idFirebase;
    }

    public void setIdFirebase(String idFirebase) {
        this.idFirebase = idFirebase;
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

    public void setPrice(float price) {
        this.price = price;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    @Ignore
    public String getPricingString() {
        return "$".concat(String.valueOf(price));
    }
}
