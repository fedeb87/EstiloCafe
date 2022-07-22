package com.federicoberon.estilocafe.repository;

import android.content.Context;
import com.federicoberon.estilocafe.R;
import com.federicoberon.estilocafe.model.UserEntity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class AuthRepository {

    private final FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final CollectionReference mUsersCollection;

    @Inject
    public AuthRepository(){
        mAuth = FirebaseAuth.getInstance();
        mUsersCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public FirebaseUser getUserSession() {
        if (mAuth.getCurrentUser() != null)
            return mAuth.getCurrentUser();
        else
            return null;
    }

    public Task<AuthResult> register(String email, String password) {
        return mAuth.createUserWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> googleLogin(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        return mAuth.signInWithCredential(credential);
    }

    public GoogleSignInClient requestGoogleSignIn(Context context) {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        return mGoogleSignInClient;

    }

    public Task<Void> sendPasswordResetEmail(String destEmail){
        return mAuth.sendPasswordResetEmail(destEmail)
                .addOnCompleteListener(Task::isSuccessful);
    }

    public String getUid() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }
        else {
            return null;
        }
    }

    public Query getUserByEmail(String email) {
        return mUsersCollection.whereEqualTo("email", email);
    }

    public Task<DocumentSnapshot> getUser(String id) {
        return mUsersCollection.document(id).get();
    }

    public Task<Void> create(UserEntity user) {
        return mUsersCollection.document(user.getIdFirebase()).set(user);
    }

    public String getEmail() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getEmail();
        } else {
            return null;
        }
    }

    public Task<Void> updateRemoteUser(UserEntity user) {
        Map<String, Object> map = new HashMap<>();
        map.put("nickname", user.getNickname());
        map.put("timestamp", new Date().getTime());
        map.put("image_profile", user.getImage_profile());
        map.put("topScoreAnime", user.getTopScoreAnime());
        map.put("topScoreMoviesAndSeries", user.getTopScoreMoviesAndSeries());
        return mUsersCollection.document(user.getIdFirebase()).update(map);
    }

    public Task<SignInMethodQueryResult> checkUserExists(String email){
        return mAuth.fetchSignInMethodsForEmail(email);
    }

    public void logout() {
        if (mAuth != null)
            mAuth.signOut();

        if (mGoogleSignInClient!=null) mGoogleSignInClient.signOut();
    }
}
