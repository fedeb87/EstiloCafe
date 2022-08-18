package com.federicoberon.estilocafe;

import android.content.Context;

import com.federicoberon.estilocafe.di.ApplicationComponent;
import com.federicoberon.estilocafe.di.TestDatabaseModule;
import com.federicoberon.estilocafe.di.component.DaggerTestApplicationComponent;
import com.federicoberon.estilocafe.di.module.ApplicationModule;


public class EstiloCafeTestApplication extends EstiloCafeApplication{

    @Override
    public ApplicationComponent initializeComponent() {
        return DaggerTestApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .testDatabaseModule(new TestDatabaseModule(this))
                .build();
    }

    public static EstiloCafeTestApplication get(Context context) {
        return (EstiloCafeTestApplication) context.getApplicationContext();
    }

}
