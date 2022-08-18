package com.federicoberon.estilocafe.repository;

import android.content.Context;
import com.federicoberon.estilocafe.utils.CompressorBitmapImage;
import com.federicoberon.estilocafe.utils.StorageUtil;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.util.Date;

import javax.inject.Inject;

public class ProfileImageRepository {

    private final String LOG_TAG = "<<< PIRepository >>>";
    StorageReference mStorage;

    @Inject
    ProfileImageRepository() {
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask save(Context context, File file, String uid) {
        StorageReference storage;
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);

        if (uid != null)
            storage = FirebaseStorage.getInstance().getReference().child(uid + "_profile.jpg");
        else
            storage = FirebaseStorage.getInstance().getReference().child(new Date() + ".jpg");


        mStorage = storage;
        return storage.putBytes(imageByte);
    }

    public void getProfileImageToExternalStorage(String uid){
        StorageReference islandRef = mStorage.child(uid+"_profile.jpg");

        File localFile = new File(StorageUtil.getProfileImagePath(uid));
        if (!localFile.exists()) new File(StorageUtil.getProfileAbsolutePath()).mkdirs();

        islandRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {});
    }

    public FileDownloadTask getProfileImageToExternalStorageLive(String uid){
        StorageReference islandRef = mStorage.child(uid+"_profile.jpg");
        File localFile = new File(StorageUtil.getProfileImagePath(uid));
        return islandRef.getFile(localFile);
    }

    public StorageReference getStorage() {
        return mStorage;
    }
}
