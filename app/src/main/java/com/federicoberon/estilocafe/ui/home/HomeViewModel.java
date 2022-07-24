package com.federicoberon.estilocafe.ui.home;

import android.content.Context;
import com.federicoberon.estilocafe.BaseViewModel;
import com.federicoberon.estilocafe.EstiloCafeApplication;
import com.federicoberon.estilocafe.repository.AuthRepository;
import com.federicoberon.estilocafe.repository.ProfileImageRepository;
import com.federicoberon.estilocafe.utils.CompressorBitmapImage;
import com.federicoberon.estilocafe.utils.StorageUtil;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HomeViewModel extends BaseViewModel {

    private String mAbsolutePhotoPath;
    private File mPhotoFile;
    private boolean mChangeProfileImage;

    @Inject
    public HomeViewModel(AuthRepository authRepository,
                         ProfileImageRepository profileImageRepository) {
        super(authRepository, profileImageRepository);
    }

    public void setAbsolutePhotoPath(String mAbsolutePhotoPath) {
        this.mAbsolutePhotoPath = mAbsolutePhotoPath;
    }

    public File getPhotoFile() {
        return mPhotoFile;
    }

    public boolean isChangeProfileImage() {
        return mChangeProfileImage;
    }

    public void setChangeProfileImage(boolean mChangeProfileImage) {
        this.mChangeProfileImage = mChangeProfileImage;
    }

    public UploadTask save(Context context, File file, String uid) {
        return mProfileImageRepository.save(context, file, uid);
    }

    public void saveProfileImageToLocalStorage(String path, EstiloCafeApplication app) {
        StorageUtil.saveProfileImageToLocalStorage(getUid() + "_profile.jpg",
                CompressorBitmapImage.getBitmap(app, path, 500, 500));
    }

    public StorageReference getStorage() {
        return mProfileImageRepository.getStorage();
    }

    public void setPhotoFile(File mPhotoFile) {
        this.mPhotoFile = mPhotoFile;
    }

    public String getAbsolutePhotoPath() {
        return mAbsolutePhotoPath;
    }

}
