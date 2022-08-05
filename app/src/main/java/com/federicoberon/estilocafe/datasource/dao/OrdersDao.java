package com.federicoberon.estilocafe.datasource.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.federicoberon.estilocafe.model.OrderEntity;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface OrdersDao extends BaseDao<OrderEntity>{

    /* 0 (false) and 1 (true). */
}