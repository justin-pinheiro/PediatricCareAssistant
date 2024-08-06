package com.example.pediatriccareassistant.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.pediatriccareassistant.R;
import com.example.pediatriccareassistant.model.callback.LoadingCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthenticationController {
    private static final AuthenticationController instance = new AuthenticationController();
    public static final int RC_SIGN_IN = 9001;
    private static final String TAG = "AuthController";

    private FirebaseUser user;
    private final FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;

    private AuthenticationController() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public static AuthenticationController getInstance() {
        return instance;
    }

    public void configureGoogleSignIn(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public void signInWithGoogle(Activity activity) {
        if (googleSignInClient == null) {
            configureGoogleSignIn(activity);
        }
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleGoogleSignInResult(Intent data, LoadingCallback callback) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                Log.w(TAG, "Google sign in success");
                firebaseAuthWithGoogle(account, callback);
            } else {
                Log.w(TAG, "Google sign in failed");
                callback.onLoadingFailed(new Exception("Google Sign-In failed."));
            }
        } catch (ApiException e) {
            Log.e(TAG, "Google sign in failed", e);
            callback.onLoadingFailed(new Exception("Google Sign-In failed: " + e.getMessage()));
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account, LoadingCallback callback) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithCredential:success");
                        user = mAuth.getCurrentUser();
                        callback.onLoadingSuccessful();
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        callback.onLoadingFailed(new Exception("Authentication failed."));
                    }
                });
    }

    public void signOut() {
        mAuth.signOut();
        if(googleSignInClient != null) {
            googleSignInClient.signOut().addOnCompleteListener(task -> Log.d(TAG, "User signed out"));
        }
    }

    public void signInUser(String email, String password, LoadingCallback callback) {
        callback.onStartLoading();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onLoadingSuccessful();
                    } else {
                        callback.onLoadingFailed(new Exception("Authentication failed."));
                    }
                });
    }

    public void registerUser(String email, String password, LoadingCallback callback) {
        callback.onStartLoading();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = mAuth.getCurrentUser();
                        callback.onLoadingSuccessful();
                    } else {
                        callback.onLoadingFailed(new Exception("Registration failed."));
                    }
                });
    }

    public boolean isCurrentUser() {
        return mAuth.getCurrentUser() != null;
    }

    public String getUserUid() {
        return user != null ? user.getUid() : null;
    }
}