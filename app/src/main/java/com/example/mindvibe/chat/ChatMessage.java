package com.example.mindvibe.chat;

import java.util.UUID;

public class ChatMessage {
    private final String id;
    private final String text;
    private final MessageSender sender;
    private final boolean loading;

    private ChatMessage(String id, String text, MessageSender sender, boolean loading) {
        this.id = id;
        this.text = text;
        this.sender = sender;
        this.loading = loading;
    }

    public static ChatMessage fromUser(String text) {
        return new ChatMessage(UUID.randomUUID().toString(), text, MessageSender.USER, false);
    }

    public static ChatMessage fromAi(String text) {
        return new ChatMessage(UUID.randomUUID().toString(), text, MessageSender.AI, false);
    }

    public static ChatMessage loadingFromAi() {
        return new ChatMessage(UUID.randomUUID().toString(), "Đang suy nghĩ...", MessageSender.AI, true);
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public MessageSender getSender() {
        return sender;
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isFromUser() {
        return sender == MessageSender.USER;
    }
}
