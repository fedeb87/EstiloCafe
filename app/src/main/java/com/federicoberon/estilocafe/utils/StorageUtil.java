package com.federicoberon.estilocafe.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.OpenableColumns;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StorageUtil {
    private static final String LOG_TAG = "<<< StorageUtil >>>";
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

    /**
     * Return available storage(bytes) in the device
     * @return true if available storage
     */
    public static boolean getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        return (availableBlocks * blockSize)>50000000;
    }

    public static String getProfileImagePath(String id){
        return getExternalStorageDirectory() + PROFILE_PATH + "/" + id + "_profile.jpg";
    }

    public static String getSongPath(String id){
        return getExternalStorageDirectory() + MEDIA_PATH + "/" + id + ".mp3";
    }

    public static String getMediaAbsolutePath(){
        return getExternalStorageDirectory() + MEDIA_PATH;
    }

    public static String getProfileAbsolutePath(){
        return getExternalStorageDirectory() + PROFILE_PATH;
    }

    public static boolean fileProfileExist(String uid) {
        String fileName = uid + "_profile.jpg";
        File dir = new File (getExternalStorageDirectory() + PROFILE_PATH);
        File file = new File(dir, fileName);
        return file.exists();
    }

    public static boolean fileExist(String folder, String name) {
        File dir = new File (getMediaAbsolutePath());
        File file = new File(dir, folder+"/"+name);
        return file.exists();
    }

    public static boolean openFile(Context context, String zipDir, String name) {
        String url = getMediaAbsolutePath() + "/" + zipDir + "/" + name;

        Uri uri = FileProvider.getUriForFile(
                context,
                "com.federicoberon.estilocafe.provider", new File(url));
        Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        /* set data and type */
        if (url.endsWith(".doc") || url.endsWith(".docx")) {
            // Word document
            pdfOpenintent.setDataAndType(uri, "application/msword");
        } else if (url.endsWith(".pdf")) {
            // PDF file
            pdfOpenintent.setDataAndType(uri, "application/pdf");
        } else if (url.endsWith(".ppt") || url.endsWith(".pptx")) {
            // Powerpoint file
            pdfOpenintent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.endsWith(".xls") || url.endsWith(".xlsx")) {
            // Excel file
            pdfOpenintent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.endsWith(".zip")) {
            // ZIP file
            //pdfOpenintent.setDataAndType(path, "application/zip");
            pdfOpenintent.setDataAndType(uri, "application/zip");

        } else if (url.endsWith(".rar")) {
            // RAR file
            pdfOpenintent.setDataAndType(uri, "application/x-rar-compressed");
        } else if (url.endsWith(".rtf")) {
            // RTF file
            pdfOpenintent.setDataAndType(uri, "application/rtf");
        } else if (url.endsWith(".wav") || url.endsWith(".mp3")) {
            // WAV audio file
            pdfOpenintent.setDataAndType(uri, "audio/x-wav");
        } else if (url.endsWith(".gif")) {
            // GIF file
            pdfOpenintent.setDataAndType(uri, "image/gif");
        } else if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png")) {
            // JPG file
            pdfOpenintent.setDataAndType(uri, "image/jpeg");
        } else if (url.endsWith(".txt")) {
            // Text file
            pdfOpenintent.setDataAndType(uri, "text/plain");
        } else if (url.endsWith(".3gp") || url.endsWith(".mpg") ||
                url.endsWith(".mpeg") || url.endsWith(".mpe") || url.endsWith(".mp4") || url.endsWith(".avi")) {
            // Video files
            pdfOpenintent.setDataAndType(uri, "video/*");
        } else {
            pdfOpenintent.setDataAndType(uri, "*/*");
        }
        pdfOpenintent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(pdfOpenintent);
        } catch (ActivityNotFoundException e) {
            //Log.e(LOG_TAG, "Can't open file:: " + e.getMessage());
            return false;
        }

        return true;
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

    public static void createTreeDirectory(){
        absoluteMediaPath = getExternalStorageDirectory() + MEDIA_PATH;
        String absoluteProfilePath = getExternalStorageDirectory() + PROFILE_PATH;

        File dir = new File (absoluteMediaPath);
        if (!dir.exists())
            dir.mkdirs();
            //Log.e(LOG_TAG, "No se pudo crear el directorio de media por alguna razon absoluteMediaPath: " + absoluteProfilePath);
        dir = new File (absoluteProfilePath);
        if (!dir.exists())
            dir.mkdirs();
            //Log.e(LOG_TAG, "No se pudo crear el directorio de perfiles por alguna razon absoluteProfilePath: " + absoluteProfilePath);

    }

    public static Target getTarget(final String fileName) {
        return new Target() {
            //This method in target is called by picasso when image downloaded
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(() -> {
                    try {
                        File file = new File(fileName);
                        if (file.exists())  file.delete();

                        file.createNewFile();
                        FileOutputStream fileoutputstream = new FileOutputStream(file);
                        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 60, bytearrayoutputstream);
                        fileoutputstream.write(bytearrayoutputstream.toByteArray());
                        fileoutputstream.close();
                    } catch (IOException e) {
                        //Log.e("IOException", e.getLocalizedMessage());
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

    public static void unZipFiles(String dir, String fileName) {
        String source = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + fileName;
        String destination = getMediaAbsolutePath();
        File fileDestination = new File(destination);
        if (fileDestination.exists()) fileDestination.delete();

        try {
            new ZipFile(source, "Pepinazo00_".toCharArray()).extractAll(destination);
            source = getMediaAbsolutePath() + "/" + dir;
            destination = getMediaAbsolutePath();
            new ZipFile(source, "Pepinazo00_".toCharArray()).extractAll(destination);
        } catch (ZipException e) {
            e.printStackTrace();
        }
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

    public static boolean checkStoragePermissionAndCreateDirectory(Activity activity) {
        /* Storage permissions */
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            createTreeDirectory();
            return true;
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission(activity);
            return false;
        }
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
