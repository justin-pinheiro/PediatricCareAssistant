package com.example.pediatriccareassistant.model.callback;

public interface LoadingCallback {
    void onStartLoading();
    void onLoadingSuccessful();
    void onLoadingFailed(Exception e);
}
