package com.federicoberon.estilocafe.ui.splash;

import static com.federicoberon.estilocafe.utils.Constants.DEFAULT_ENABLE_LOGS;
import static com.federicoberon.estilocafe.utils.Constants.ENABLE_LOGS;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import com.federicoberon.estilocafe.BaseActivity;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.databinding.ActivitySplashBinding;
import com.federicoberon.estilocafe.ui.login.LoginActivity;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    private static final String LOG_TAG = "Splash Activity";
    private ActivitySplashBinding binding;

    @Inject


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((EstiloCafeApplication) getApplicationContext())
                .appComponent.inject(this);

        super.onCreate(savedInstanceState);

        // deleted, only for testing propose
        //restartMaxScores();

        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());

        //This is additional feature, used to run a progress bar
        playProgress();

        if (!sharedPref.contains(ENABLE_LOGS)){
            // this is the first time that app runs
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(ENABLE_LOGS, DEFAULT_ENABLE_LOGS);
            editor.apply();
        }

        FirebaseUser firebaseUser = mViewModel.getUserSession();
        if (firebaseUser != null) {
            mViewModel.saveUserInfo(SplashActivity.this, mViewModel.getUid());
            checkGoHome(firebaseUser.getUid(), binding.getRoot());
        }else{
            // prevent to back to Splash screen
            finish();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

    }

    //Method to run progress bar for 4 seconds
    private void playProgress() {
        ObjectAnimator.ofInt(binding.splashProgress, "progress", 100)
                .setDuration(6000)
                .start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        binding = null;

    }
}