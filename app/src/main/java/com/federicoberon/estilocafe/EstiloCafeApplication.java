package com.federicoberon.estilocafe;

import static com.federicoberon.estilocafe.utils.Constants.CHANNEL_ID;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.federicoberon.estilocafe.di.ApplicationComponent;
import com.federicoberon.estilocafe.di.DaggerApplicationComponent;
import com.federicoberon.estilocafe.di.module.ApplicationModule;
import com.federicoberon.estilocafe.di.module.DatabaseModule;
import com.federicoberon.estilocafe.utils.NetworkMonitoringUtil;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class EstiloCafeApplication extends Application {
    private Scheduler defaultSubscribeScheduler;

    public NetworkMonitoringUtil mNetworkMonitoringUtil;

    // Reference to the application graph that is used across the whole app
    public ApplicationComponent appComponent = initializeComponent();


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannnel();

        mNetworkMonitoringUtil = new NetworkMonitoringUtil(getApplicationContext());
        mNetworkMonitoringUtil.checkNetworkState();
        mNetworkMonitoringUtil.registerNetworkCallbackEvents();
    }

    public ApplicationComponent initializeComponent() {
        return DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .databaseModule(new DatabaseModule(this))
                .build();
    }

    public static EstiloCafeApplication get(Context context) {
        return (EstiloCafeApplication) context.getApplicationContext();
    }

    private void createNotificationChannnel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Alarm Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            serviceChannel.enableLights(true);
            serviceChannel.enableVibration(true);
            serviceChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    public void setDefaultSubscribeScheduler(Scheduler scheduler) {
        this.defaultSubscribeScheduler = scheduler;
    }
}
