package com.federicoberon.estilocafe.di.module;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


import com.federicoberon.estilocafe.datasource.AppDatabase;
import com.federicoberon.estilocafe.datasource.dao.OrdersDao;
import com.federicoberon.estilocafe.di.ApplicationContext;
import com.federicoberon.estilocafe.di.DatabaseInfo;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    private static volatile AppDatabase INSTANCE;

    @ApplicationContext
    private final Context mContext;

    @DatabaseInfo
    private final String mDBName = "quiz.db";

    public DatabaseModule(@ApplicationContext Context context) {
        mContext = context;
    }

    @SuppressLint("CheckResult")
    @Singleton
    @Provides
    AppDatabase provideDatabase () {

        if(INSTANCE == null)
            INSTANCE = Room.databaseBuilder(
                    mContext,
                    AppDatabase.class,
                    mDBName)
                    .addCallback(getRDC())
                    .fallbackToDestructiveMigration()
                    .build();

        return INSTANCE;
    }

    /**
     * load all system ringtone info into the itnernal database
     * @return
     */
    private RoomDatabase.Callback getRDC() {

        return new RoomDatabase.Callback() {
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                /*ContentValues contentValues = new ContentValues();
                    contentValues.put("id",toneIdList.get(position));
                    contentValues.put("title",toneNameList.get(position));
                    contentValues.put("uri", toneUriList.get(position).toString());
                    db.insert("melodies", OnConflictStrategy.REPLACE, contentValues);*/

            }
        };
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() { return mDBName; }

    @Singleton
    @Provides
    OrdersDao provideQuestionsDao(AppDatabase db) { return INSTANCE.ordersDao(); }
}
