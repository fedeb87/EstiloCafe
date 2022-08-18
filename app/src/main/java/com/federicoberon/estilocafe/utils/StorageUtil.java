package com.federicoberon.estilocafe.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StorageUtil {
    private static final String MEDIA_PATH = "/EstiloCafe/Media";
    private static final String PROFILE_PATH = "/EstiloCafe/Profile";
    private static String absoluteMediaPath;
    public static final int PERMISSION_REQUEST_STORAGE = 0;

    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    private static String getExternalStorageDirectory(){

        if (Build.VERSION_CODES.Q >= Build.VERSION.SDK_INT){
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }else{
            return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath();
        }
    }

    public static String getProfileImagePath(String id){
        return getExternalStorageDirectory() + PROFILE_PATH + "/" + id + "_profile.jpg";
    }

    public static String getMediaAbsolutePath(){
        return getExternalStorageDirectory() + MEDIA_PATH;
    }

    public static String getProfileAbsolutePath(){
        return getExternalStorageDirectory() + PROFILE_PATH;
    }

    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                //Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                //Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer)))
            output.write(buffer, 0, n);

    }

    /**
     * Guarda la foto puesta en la app en el directorio dedicado, se usa en register, en el complete y en el update perfil
     * @param fileName
     * @param bitmap
     */
    public static void saveProfileImageToLocalStorage(String fileName, Bitmap bitmap){
        File dir = new File (getExternalStorageDirectory() + "/EstiloCafe/Profile");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, fileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream f = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, f);
            f.flush();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
        file.getAbsolutePath();
    }

    private static void requestStoragePermission(Activity activity) {
        // Request the permission
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_STORAGE);
    }

    @SuppressLint("Range")
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }
}
