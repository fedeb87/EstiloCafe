package com.federicoberon.estilocafe.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.federicoberon.estilocafe.R;

public class DialogErrorHelper {

    @SuppressLint("UseCompatLoadingForDrawables")
    public static AlertDialog.Builder getWarningDialog(Context context, String title, String body, DialogInterface.OnClickListener listener){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(body)
                //.setMessage("Are you sure you want to delete this entry?")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.ok_string, listener)
                .setIcon(context.getResources().getDrawable(
                        R.drawable.ic_warning));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static AlertDialog.Builder getErrorDialog(Context context, String title, String body){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(body)
                //.setMessage("Are you sure you want to delete this entry?")

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.ok_string, null)
                .setIcon(context.getResources().getDrawable(
                        R.drawable.ic_warning));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public static AlertDialog.Builder getCriticDialog(Context context, String title, String body){
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(body)
                .setCancelable(false)
                .setNegativeButton(R.string.go_settings_string, (dialogInterface, i) -> context.startActivity(new Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)))
                .setIcon(context.getResources().getDrawable(
                        R.drawable.ic_warning));
    }
}
