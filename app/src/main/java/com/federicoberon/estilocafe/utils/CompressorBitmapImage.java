package com.federicoberon.estilocafe.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.common.io.Files;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

public class CompressorBitmapImage {

    public static byte[] getImage(Context ctx, String path, int width, int height) {
        final File file_thumb_path = new File(path);
        Bitmap thumb_bitmap = null;
        Bitmap.CompressFormat format;

        String extension = Files.getFileExtension(path);

        switch(extension){
            case "png":
                format = Bitmap.CompressFormat.PNG;
                break;
            case "webp":
                format = Bitmap.CompressFormat.WEBP;
                break;
            default:
                format = Bitmap.CompressFormat.JPEG;
                break;
        }

        try {
            thumb_bitmap = new Compressor(ctx)
                    .setMaxWidth(width)
                    .setMaxHeight(height)
                    .setQuality(75)
                    .setCompressFormat(format)
                    .compressToBitmap(file_thumb_path);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb_bitmap.compress(format,80,baos);
        return baos.toByteArray();
    }

    public static Bitmap getBitmap(Context ctx, String path, int width, int height) {
        final File file_thumb_path = new File(path);
        Bitmap thumb_bitmap = null;

        Bitmap.CompressFormat format;

        String extension = Files.getFileExtension(path);

        switch(extension){
            case "png":
                format = Bitmap.CompressFormat.PNG;
                break;
            case "webp":
                format = Bitmap.CompressFormat.WEBP;
                break;
            default:
                format = Bitmap.CompressFormat.JPEG;
                break;
        }

        try {
            thumb_bitmap = new Compressor(ctx)
                    .setMaxWidth(width)
                    .setMaxHeight(height)
                    .setQuality(75)
                    .setCompressFormat(format)
                    .compressToBitmap(file_thumb_path);
        } catch (IOException e) {
            //e.printStackTrace();
        }

        return thumb_bitmap;
    }
}
