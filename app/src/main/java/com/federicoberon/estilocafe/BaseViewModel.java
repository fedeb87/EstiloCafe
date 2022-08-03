package com.federicoberon.estilocafe;

import static com.federicoberon.estilocafe.utils.Constants.EMAIL_KEY;
import static com.federicoberon.estilocafe.utils.Constants.ENABLE_LOGS;
import static com.federicoberon.estilocafe.utils.Constants.NICKNAME_KEY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModel;

import com.federicoberon.estilocafe.model.UserEntity;
import com.federicoberon.estilocafe.repository.AuthRepository;
import com.federicoberon.estilocafe.repository.ProfileImageRepository;
import com.federicoberon.estilocafe.utils.AppExecutors;
import com.federicoberon.estilocafe.utils.StorageUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;
import java.util.Date;

import javax.inject.Inject;

public class BaseViewModel extends ViewModel {

    protected final AuthRepository mAuthRepository;
    protected final ProfileImageRepository mProfileImageRepository;

    @Inject
    public SharedPreferences sharedPref;

    @Inject
    public BaseViewModel(AuthRepository authRepository, ProfileImageRepository profileImageRepository){
        mAuthRepository = authRepository;
        mProfileImageRepository = profileImageRepository;
    }
    public FirebaseUser getUserSession() {
        return mAuthRepository.getUserSession();
    }

    public String getUid() {
        return mAuthRepository.getUid();
    }

    public UserEntity createUser(String id, String email){
        return createUser(id, email, null, null);
    }

    public UserEntity createUser(String id, String email, String username, String url){
        UserEntity user = new UserEntity();
        user.setIdFirebase(id);
        user.setEmail(email);
        user.setNickname(username);
        user.setTimeStamp(new Date().getTime());
        user.setImage_profile(url);

        return user;
    }

    public Task<Void> createRemoteUser(UserEntity user) {
        user.setTopScoreAnime(0);
        user.setTopScoreMoviesAndSeries(0);
        return mAuthRepository.create(user);
    }

    public void logout() {
        sharedPref.edit().remove(NICKNAME_KEY).apply();
        mAuthRepository.logout();
    }

    public Task<DocumentSnapshot> getUser(String id) {
        return mAuthRepository.getUser(id);
    }

    public Task<DocumentSnapshot> getUser() {
        return mAuthRepository.getUser(getUid());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void saveUserInfo(Context context, final String uid) {

        getUser(uid).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                long timestampUser = documentSnapshot.getLong("timeStamp");

                String image_profile = documentSnapshot.contains("image_profile")?
                        documentSnapshot.getString("image_profile"): null;

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(NICKNAME_KEY, documentSnapshot.getString("nickname"));
                editor.putString(EMAIL_KEY, documentSnapshot.getString("email"));
                editor.apply();

                if(image_profile!=null)
                    AppExecutors.getInstance().diskIO().execute(() -> {
                        // modificacion local
                        File file = new File(StorageUtil.getProfileImagePath(uid));
                        Date lastModDateLocal;
                        if (file.exists()) {
                            lastModDateLocal = new Date(file.lastModified());
                            if ((new Date(timestampUser)).compareTo(lastModDateLocal) >= 0) {
                                mProfileImageRepository.getProfileImageToExternalStorage(uid);
                            }
                        } else {
                            mProfileImageRepository.getProfileImageToExternalStorage(uid);
                        }
                    });

            } else {
                new AlertDialog.Builder(context)
                        .setTitle(context.getResources().getString(
                                R.string.error_generic_title))
                        .setMessage(context.getResources().getString(
                                R.string.error_save_user))
                        .setNegativeButton(android.R.string.ok, null)
                        .setIcon(context.getResources().getDrawable(
                                R.drawable.ic_warning));
                if(sharedPref.getBoolean(ENABLE_LOGS, false))
                    Log.e("ERROR", "Can´t load User from remote storage. Please contact support");
            }
        });

    }
}
