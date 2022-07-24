package com.federicoberon.estilocafe.model;

public class ProductModel {
    private String productID;
    private String productName;
    private String rating;
    private float pricing;
    private int appImage;
    private String desc;

    public ProductModel(String productID, String productName, String rating, float pricing, int appImage, String desc) {
        this.productName = productName;
        this.rating = rating;
        this.pricing = pricing;
        this.appImage = appImage;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAppImage() {
        return appImage;
    }

    public void setAppImage(int appImage) {
        this.appImage = appImage;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public float getPricing() {
        return pricing;
    }

    public String getPricingString() {
        return "$"+pricing;
    }

    public void setPricing(float pricing) {
        this.pricing = pricing;
    }
}
