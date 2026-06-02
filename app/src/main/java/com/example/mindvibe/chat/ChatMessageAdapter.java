package com.example.mindvibe.chat;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mindvibe.R;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessageViewHolder> {
    private final List<ChatMessage> messages = new ArrayList<>();

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public int addMessage(ChatMessage message) {
        messages.add(message);
        int position = messages.size() - 1;
        notifyItemInserted(position);
        return position;
    }

    public void replaceMessage(int position, ChatMessage message) {
        if (position < 0 || position >= messages.size()) {
            return;
        }

        messages.set(position, message);
        notifyItemChanged(position);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textMessageBody;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageBody = itemView.findViewById(R.id.textMessageBody);
        }

        void bind(ChatMessage message) {
            textMessageBody.setText(message.getText());
            textMessageBody.setTypeface(null, message.isLoading() ? Typeface.ITALIC : Typeface.NORMAL);
            textMessageBody.setMaxWidth((int) (itemView.getResources().getDisplayMetrics().widthPixels * 0.78f));

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) textMessageBody.getLayoutParams();
            params.gravity = message.isFromUser() ? Gravity.END : Gravity.START;
            textMessageBody.setLayoutParams(params);

            if (message.isFromUser()) {
                textMessageBody.setBackgroundResource(R.drawable.bg_chat_bubble_user);
                textMessageBody.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            } else {
                textMessageBody.setBackgroundResource(R.drawable.bg_chat_bubble_ai);
                textMessageBody.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.purple_dark));
            }
        }
    }
}
