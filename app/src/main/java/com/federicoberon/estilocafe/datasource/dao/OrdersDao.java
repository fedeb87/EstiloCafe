package com.federicoberon.estilocafe.datasource.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.federicoberon.estilocafe.model.OrderEntity;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface OrdersDao extends BaseDao<OrderEntity>{

    @Query("SELECT * FROM orders")
    Flowable<List<OrderEntity>> getAllOrders();

    @Query("SELECT q.* FROM orders q WHERE q.id = :id")
    Flowable<OrderEntity> getOrderById(long id);

    @Query("SELECT count(*) FROM orders")
    int getOrdersCount();

    /* 0 (false) and 1 (true). */
}