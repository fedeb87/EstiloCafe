package com.federicoberon.estilocafe.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable model class for a order
 */
@Entity(tableName = "orders")
public class OrderEntity implements Serializable {

    @PrimaryKey
    private long id;
    private String response;
    private int difficulty;
    private int category;

    public OrderEntity(long id, String response, int category, int difficulty) {
        this.id = id;
        this.response = response;
        this.difficulty = difficulty;
        this.category = category;
    }

    @Ignore
    public OrderEntity(String response, int category, int difficulty) {
        this.response = response;
        this.difficulty = difficulty;
        this.category = category;
    }

    @Ignore
    public OrderEntity() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity alarmEntity = (OrderEntity) o;
        return response.equals(alarmEntity.response) && id == id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(response);
    }

}
