package com.example.pediatriccareassistant.model.callback;

/**
 * Callback interface for handling the result of the response generation task.
 *
 * @param <T> Type of the success result
 * @param <S> Type of the error
 */
public interface ChatbotCallback<T, S> {
    void onSuccess(String answer, String id);
    void onFailure(Exception e);
}
