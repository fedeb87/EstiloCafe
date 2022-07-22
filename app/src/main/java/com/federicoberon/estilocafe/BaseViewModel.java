package com.federicoberon.estilocafe;

import static com.federicoberon.estilocafe.utils.Constants.EMAIL_KEY;
import static com.federicoberon.estilocafe.utils.Constants.ENABLE_LOGS;
import static com.federicoberon.estilocafe.utils.Constants.NICKNAME_KEY;
import static com.federicoberon.estilocafe.utils.Constants.TOP_SCORE_ANIME_KEY;
import static com.federicoberon.estilocafe.utils.Constants.TOP_SCORE_MOVIES_AND_SERIES_KEY;

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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseViewModel extends ViewModel {

    protected final AuthRepository mAuthRepository;
    private final ProfileImageRepository mProfileImageRepository;

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
        mAuthRepository.logout();
    }

    public Task<DocumentSnapshot> getUser(String id) {
        return mAuthRepository.getUser(id);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void saveUserInfo(Context context, final String uid) {

        getUser(uid).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                long timestampUser = documentSnapshot.getLong("timeStamp");

                int topScoreAnime = documentSnapshot.contains("topScoreAnime")?
                        Math.toIntExact(documentSnapshot.getLong("topScoreAnime")):
                        0;

                int topScoreMoviesAndSeries = documentSnapshot.contains("topScoreMoviesAndSeries")?
                        Math.toIntExact(documentSnapshot.getLong("topScoreMoviesAndSeries")):
                        0;

                String image_profile = documentSnapshot.contains("image_profile")?
                        documentSnapshot.getString("image_profile"): null;

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(NICKNAME_KEY, documentSnapshot.getString("nickname"));
                editor.putString(EMAIL_KEY, documentSnapshot.getString("email"));
                editor.putInt(TOP_SCORE_ANIME_KEY, topScoreAnime);
                editor.putInt(TOP_SCORE_MOVIES_AND_SERIES_KEY, topScoreMoviesAndSeries);
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

    // TODO: 22/07/2022 hacer que recupere el menu

    @SuppressLint("CheckResult")
    public void retrieveTopScores() {
        /*mTopScoresRepository.retrieveTopScoresFromRemote().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                TopScoreEntity topScore;
                List<TopScoreEntity> topScoreList = new ArrayList<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    topScore = new TopScoreEntity();
                    topScore.setScore((document.contains("score"))?((Number) document.get("score")).intValue():0);
                    topScore.setCategory((document.contains("category"))?(String) document.get("category"):ANIME_CAT);
                    topScore.setUser_id((document.contains("user_id"))?(String) document.get("user_id"):" ");
                    topScore.setUser_name((document.contains("user_name"))?(String) document.get("user_name"):" ");
                    topScore.setId_firebase(document.getId());
                    topScoreList.add(topScore);
                }

                saveTSToLocalDB(topScoreList)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(topScores -> {
                                    if(topScores != null && !topScores.isEmpty()){
                                        if(sharedPref.getBoolean(ENABLE_LOGS, false))
                                            log.log("TopScores saved");
                                    }
                                },
                                throwable -> {
                                    if(sharedPref.getBoolean(ENABLE_LOGS, false))
                                        Log.e("MIO", "Unable to save topScores: ", throwable);
                                });
            } else {
                Log.e(TAG, "Error getting documents: ", task.getException());
            }
        });*/
    }
}
