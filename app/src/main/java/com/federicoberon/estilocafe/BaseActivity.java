package com.federicoberon.estilocafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;

import com.federicoberon.estilocafe.ui.home.HomeActivity;
import com.federicoberon.estilocafe.ui.login.LoginActivity;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class BaseActivity extends AppCompatActivity {

    public final CompositeDisposable mDisposable = new CompositeDisposable();

    @Inject
    protected SharedPreferences sharedPref;

    @Inject
    protected BaseViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public BaseViewModel getViewModel() {
        return mViewModel;
    }

    public void goHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void checkGoHome(String uid, View root) {
        mViewModel.getUser(uid).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists())
                goHome();
            else
                goCompleteProfile(root);
        });
    }

    private void goCompleteProfile(View root) {
        Navigation.findNavController(root).navigate(R.id.action_completeProfile);
    }
}