package com.federicoberon.estilocafe.ui.login.completeProfile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.federicoberon.estilocafe.BaseViewModel;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.model.UserEntity;
import com.federicoberon.estilocafe.repository.AuthRepository;
import com.federicoberon.estilocafe.repository.ProfileImageRepository;
import com.federicoberon.estilocafe.ui.home.HomeActivity;
import com.federicoberon.estilocafe.utils.CompressorBitmapImage;
import com.federicoberon.estilocafe.utils.DialogErrorHelper;
import com.federicoberon.estilocafe.utils.StorageUtil;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import javax.inject.Inject;

public class CompleteProfileViewModel extends BaseViewModel {
    private File mPhotoFile;
    private boolean mChangeProfileImage;
    private String mAbsolutePhotoPath;
    private final ProfileImageRepository mProfileImageRepository;

    @Inject
    public CompleteProfileViewModel(AuthRepository authRepository, ProfileImageRepository profileImageRepository) {
        super(authRepository, profileImageRepository);
        mProfileImageRepository = profileImageRepository;
    }


    public void setChangeProfileImage(boolean b) {
        this.mChangeProfileImage = b;
    }

    public File getPhotoFile() {
        return mPhotoFile;
    }

    public String getEmail() {
        return mAuthRepository.getEmail();
    }

    public boolean isChangeProfileImage() {
        return mChangeProfileImage;
    }

    public String getPhotoFilePath() {
        if (mPhotoFile != null) return mPhotoFile.getPath();
        else return null;
    }

    public String getmAbsolutePhotoPath() {
        return mAbsolutePhotoPath;
    }

    public void setPhotoFile(File file) {
        this.mPhotoFile = file;
    }

    public void setmAbsolutePhotoPath(String absolutePath) {
        this.mAbsolutePhotoPath = absolutePath;
    }

    public UploadTask savePhotoFileRemote(Context context, String uid) {
        return mProfileImageRepository.save(context, getPhotoFile(), uid);
    }

    public StorageReference getStorage() {
        return mProfileImageRepository.getStorage();
    }

    public void saveUserLocally(Context context, UserEntity user, String path) {

        if (path!= null) {
            Bitmap bitmap = CompressorBitmapImage.getBitmap(context, path, 500, 500);
            StorageUtil.saveProfileImageToLocalStorage(getUid() + "_profile.jpg", bitmap);
        }

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", String.valueOf(user.getNickname()));
        editor.putString("email", String.valueOf(user.getEmail()));
        editor.apply();

    }
    public Task<SignInMethodQueryResult> checkUserExists(String email){
        return mAuthRepository.checkUserExists(email);
    }

    public UserEntity createUser(String id, String email, String username){
        return createUser(id, email, username, null);
    }

    public void createRemoteUser(final Context context, final String username, final String email, final String password) {

        mAuthRepository.register(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (isChangeProfileImage()) {
                    savePhotoFileRemote(context, getUid()).addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {
                            getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                final String url = uri.toString();
                                String id = getUid();
                                UserEntity user = createUser(id, email, username,url);

                                createRemoteUser(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        saveUserLocally(context, user, getPhotoFilePath());

                                        Intent intent = new Intent(context, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);

                                    } else {
                                        DialogErrorHelper.getErrorDialog(context, context.getResources().getString(
                                                R.string.error_generic_title), context.getResources().getString(
                                                R.string.error_save_user)).show();
                                    }
                                });

                            });
                        }
                    });
                }else {
                    String id = getUid();

                    UserEntity user = createUser(id, email, username);

                    createRemoteUser(user).addOnCompleteListener(task13 -> {
                        if (task13.isSuccessful()) {
                            saveUserLocally(context, user, getPhotoFilePath());

                            Intent intent = new Intent(context, HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                        } else {
                            DialogErrorHelper.getErrorDialog(context, context.getResources().getString(
                                    R.string.error_generic_title), context.getResources().getString(
                                    R.string.error_save_user)).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, context.getString(R.string.error_email_registered), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Task<AuthResult> googleLogin(GoogleSignInAccount googleSignInAccount) {
        return mAuthRepository.googleLogin(googleSignInAccount);
    }

    public Task<Void> sendPasswordResetEmail(String email) {
        return mAuthRepository.sendPasswordResetEmail(email);
    }

    public GoogleSignInClient requestGoogleSignIn(Context context) {
        return mAuthRepository.requestGoogleSignIn(context);
    }

    public Task<AuthResult> login(String email, String password) {
        return mAuthRepository.login(email, password);
    }

    public Task<Void> updateRemoteUser(UserEntity user) {
        return mAuthRepository.updateRemoteUser(user);
    }
}
