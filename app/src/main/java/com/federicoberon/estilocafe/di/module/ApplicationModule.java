package com.federicoberon.estilocafe.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.di.ApplicationContext;
import com.google.firebase.firestore.CollectionReference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    private final EstiloCafeApplication mApplication;

    public ApplicationModule(Application app) {
        mApplication = (EstiloCafeApplication)app;
    }

    @Singleton
    @Provides
    @ApplicationContext
    public Context provideContext() {
        return mApplication;
    }

    @Singleton
    @Provides
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(mApplication);
    }

    @Provides
    @Singleton
    public CollectionReference provideProductsCollection() {
        return mApplication.productCollection;
    }
}

