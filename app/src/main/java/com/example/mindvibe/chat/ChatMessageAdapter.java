package com.example.mindvibe.chat;

import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    public void setMessages(List<ChatMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void replaceMessage(int position, ChatMessage message) {
        if (position < 0 || position >= messages.size()) {
            return;
        }

        messages.set(position, message);
        notifyItemChanged(position);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView textMessageAvatarStart;
        private final TextView textMessageBody;
        private final TextView textMessageAvatarEnd;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textMessageAvatarStart = itemView.findViewById(R.id.textMessageAvatarStart);
            textMessageBody = itemView.findViewById(R.id.textMessageBody);
            textMessageAvatarEnd = itemView.findViewById(R.id.textMessageAvatarEnd);
        }

        void bind(ChatMessage message) {
            boolean fromUser = message.isFromUser();
            textMessageBody.setText(message.getText());
            textMessageBody.setTypeface(null, message.isLoading() ? Typeface.ITALIC : Typeface.NORMAL);
            textMessageBody.setMaxWidth((int) (itemView.getResources().getDisplayMetrics().widthPixels * 0.68f));

            LinearLayout row = (LinearLayout) itemView;
            row.setGravity(fromUser ? Gravity.END | Gravity.BOTTOM : Gravity.START | Gravity.BOTTOM);
            textMessageAvatarStart.setVisibility(fromUser ? View.GONE : View.VISIBLE);
            textMessageAvatarEnd.setVisibility(fromUser ? View.VISIBLE : View.GONE);

            if (fromUser) {
                textMessageBody.setBackgroundResource(R.drawable.bg_chat_bubble_user);
                textMessageBody.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            } else {
                textMessageBody.setBackgroundResource(R.drawable.bg_chat_bubble_ai);
                textMessageBody.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.vibe_text_primary));
            }
        }
    }
}
