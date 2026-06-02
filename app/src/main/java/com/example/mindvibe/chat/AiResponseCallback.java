package com.example.mindvibe.chat;

public interface AiResponseCallback {
    void onSuccess(String response);

    void onError(String message);
}
