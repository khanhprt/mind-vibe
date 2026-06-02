package com.example.mindvibe.chat;

public interface AiChatClient {
    void sendMessage(String message, AiResponseCallback callback);

    void shutdown();
}
