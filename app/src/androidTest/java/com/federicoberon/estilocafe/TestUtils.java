package com.federicoberon.estilocafe;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.test.core.app.ApplicationProvider;

import com.federicoberon.estilocafe.datasource.AppDatabase;

public class TestUtils {

    public static AppDatabase initDb(){

        RoomDatabase.Callback rdc = new RoomDatabase.Callback(){
            public void onCreate (@NonNull SupportSQLiteDatabase db){
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", TestDataHelper.PRODUCT_1.getId());
                contentValues.put("idFirebase", TestDataHelper.PRODUCT_1.getIdFirebase());
                contentValues.put("name", TestDataHelper.PRODUCT_1.getName());
                contentValues.put("rating", TestDataHelper.PRODUCT_1.getRating());
                contentValues.put("price", TestDataHelper.PRODUCT_1.getPrice());
                contentValues.put("images", TestDataHelper.PRODUCT_1.getImages());
                contentValues.put("description", TestDataHelper.PRODUCT_1.getDescription());
                contentValues.put("category", TestDataHelper.PRODUCT_1.getCategory());
                contentValues.put("offer", TestDataHelper.PRODUCT_1.getOffer());
                db.insert("products", OnConflictStrategy.IGNORE, contentValues);
                contentValues.put("id", TestDataHelper.PRODUCT_2.getId());
                contentValues.put("idFirebase", TestDataHelper.PRODUCT_2.getIdFirebase());
                contentValues.put("name", TestDataHelper.PRODUCT_2.getName());
                contentValues.put("rating", TestDataHelper.PRODUCT_2.getRating());
                contentValues.put("price", TestDataHelper.PRODUCT_2.getPrice());
                contentValues.put("images", TestDataHelper.PRODUCT_2.getImages());
                contentValues.put("description", TestDataHelper.PRODUCT_2.getDescription());
                contentValues.put("category", TestDataHelper.PRODUCT_2.getCategory());
                contentValues.put("offer", TestDataHelper.PRODUCT_2.getOffer());
                db.insert("products", OnConflictStrategy.IGNORE, contentValues);
                contentValues.put("id", TestDataHelper.PRODUCT_3.getId());
                contentValues.put("idFirebase", TestDataHelper.PRODUCT_3.getIdFirebase());
                contentValues.put("name", TestDataHelper.PRODUCT_3.getName());
                contentValues.put("rating", TestDataHelper.PRODUCT_3.getRating());
                contentValues.put("price", TestDataHelper.PRODUCT_3.getPrice());
                contentValues.put("images", TestDataHelper.PRODUCT_3.getImages());
                contentValues.put("description", TestDataHelper.PRODUCT_3.getDescription());
                contentValues.put("category", TestDataHelper.PRODUCT_3.getCategory());
                contentValues.put("offer", TestDataHelper.PRODUCT_3.getOffer());
                db.insert("products", OnConflictStrategy.IGNORE, contentValues);
                contentValues.put("id", TestDataHelper.PRODUCT_4.getId());
                contentValues.put("idFirebase", TestDataHelper.PRODUCT_4.getIdFirebase());
                contentValues.put("name", TestDataHelper.PRODUCT_4.getName());
                contentValues.put("rating", TestDataHelper.PRODUCT_4.getRating());
                contentValues.put("price", TestDataHelper.PRODUCT_4.getPrice());
                contentValues.put("images", TestDataHelper.PRODUCT_4.getImages());
                contentValues.put("description", TestDataHelper.PRODUCT_4.getDescription());
                contentValues.put("category", TestDataHelper.PRODUCT_4.getCategory());
                contentValues.put("offer", TestDataHelper.PRODUCT_4.getOffer());
                db.insert("products", OnConflictStrategy.IGNORE, contentValues);
            }
        };

        return Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
                AppDatabase.class)
                .addCallback(rdc)
                // allowing main thread queries, just for testing
                .allowMainThreadQueries()
                .build();
    }
}
