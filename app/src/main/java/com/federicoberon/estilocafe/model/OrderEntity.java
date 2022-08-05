package com.federicoberon.estilocafe.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Date;

/**
 * Immutable model class for a order
 */
@Entity(tableName = "orders")
public class OrderEntity implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String productsList;
    private String productsCant;
    private Date date;
    private float total;
    private String title;

    @Ignore
    public OrderEntity() {
    }

    public OrderEntity(long id, String productsList, String productsCant, Date date, float total, String title) {
        this.id = id;
        this.productsList = productsList;
        this.productsCant = productsCant;
        this.date = date;
        this.total = total;
        this.title = title;
    }

    public String getProductsCant() {
        return productsCant;
    }

    public void setProductsCant(String productsCant) {
        this.productsCant = productsCant;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProductsList() {
        return productsList;
    }

    public void setProductsList(String productsList) {
        this.productsList = productsList;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
