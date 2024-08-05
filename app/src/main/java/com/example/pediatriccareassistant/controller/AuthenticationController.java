package com.example.pediatriccareassistant.controller;

import com.example.pediatriccareassistant.model.callback.LoadingCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationController
{
    private static final AuthenticationController instance = new AuthenticationController();

    private FirebaseUser user;
    private final FirebaseAuth mAuth;

    private AuthenticationController()
    {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    public static AuthenticationController getInstance() {
        return instance;
    }

    public void signoutUser() {
        FirebaseAuth.getInstance().signOut();
    }

    public void signInUser(String email, String password, LoadingCallback callback)
    {
        callback.onStartLoading();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        callback.onLoadingSuccessful();
                    }
                    else {
                        callback.onLoadingFailed(new Exception("Authentication failed."));
                    }

                });
    }

    public void registerUser(String email, String password, LoadingCallback callback)
    {
        callback.onStartLoading();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        user = mAuth.getCurrentUser();
                        callback.onLoadingSuccessful();
                    } else {
                        callback.onLoadingFailed(new Exception("Registration failed."));
                    }
                });
    }

    public boolean isCurrentUser(){
        return mAuth.getCurrentUser() != null;
    }

    public String getUserUid() {
        return user.getUid();
    }
}
