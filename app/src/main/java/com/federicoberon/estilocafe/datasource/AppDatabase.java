package com.federicoberon.estilocafe.datasource;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.federicoberon.estilocafe.datasource.dao.OrdersDao;
import com.federicoberon.estilocafe.model.OrderEntity;

/**
 * The Room database that contains the Questions table
 */
@Database(entities = {OrderEntity.class}, version = 1)
@TypeConverters({DateConverterUtils.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract OrdersDao ordersDao();
}
