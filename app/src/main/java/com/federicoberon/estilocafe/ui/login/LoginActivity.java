package com.federicoberon.estilocafe.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.federicoberon.estilocafe.BaseActivity;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.databinding.ActivityLoginBinding;
import com.federicoberon.estilocafe.ui.home.HomeActivity;
import com.federicoberon.estilocafe.ui.login.completeProfile.CompleteProfileFragment;
import com.federicoberon.estilocafe.utils.DialogErrorHelper;

import java.util.Objects;

public class LoginActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private ActivityLoginBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((EstiloCafeApplication) getApplicationContext())
                .appComponent.inject(this);

        Log.w("<<< MIO >>>", "On create del LoginActivity");

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    public void onBackPressed() {
        if (Objects.equals(getForegroundFragment(), CompleteProfileFragment.class))
            DialogErrorHelper.getErrorDialog(this, getResources().getString(
                    R.string.error_complete_profile_title), getResources().getString(
                    R.string.error_complete_profile)).show();
        else
            super.onBackPressed();
    }

    private Fragment getForegroundFragment(){
        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        return navHostFragment == null ? null : navHostFragment.getChildFragmentManager().getFragments().get(0);
    }

    public ActivityLoginBinding getBinding() {
        return binding;
    }
}