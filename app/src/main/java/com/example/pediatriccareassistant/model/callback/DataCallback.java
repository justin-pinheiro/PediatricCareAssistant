package com.example.pediatriccareassistant.model.callback;

public interface DataCallback<T> {
    void onSuccess(T data);
    void onFailure(Exception e);
}
