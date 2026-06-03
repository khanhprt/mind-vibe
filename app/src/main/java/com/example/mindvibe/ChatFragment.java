package com.example.mindvibe;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.GradientDrawable;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mindvibe.chat.AiChatClient;
import com.example.mindvibe.chat.AiResponseCallback;
import com.example.mindvibe.chat.ChatMessage;
import com.example.mindvibe.chat.ChatMessageAdapter;
import com.example.mindvibe.chat.FastApiAiChatClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private View listContainer, detailContainer;
    private View viewDetailOnlineDot;
    private RecyclerView recyclerViewChats, recyclerViewMessages;
    private TextInputEditText editTextMessage;
    private ImageButton buttonBack, buttonSend;
    private TextView textChatTitle, textChatStatus, textDetailAvatar;
    private ChatMessageAdapter messageAdapter;
    private AiChatClient aiChatClient;
    private OnBackPressedCallback detailBackCallback;
    private boolean waitingForAi;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        bindViews(view);
        setupConversationList();
        setupHeader();
        setupMessages();
        setupInput();
        setupBackHandling();
        showConversationList();

        aiChatClient = new FastApiAiChatClient(BuildConfig.AI_BACKEND_URL);
        updateSendButtonState();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (aiChatClient != null) {
            aiChatClient.shutdown();
        }
        aiChatClient = null;
        listContainer = null;
        detailContainer = null;
        recyclerViewChats = null;
        recyclerViewMessages = null;
        editTextMessage = null;
        buttonBack = null;
        buttonSend = null;
        textChatTitle = null;
        textChatStatus = null;
        textDetailAvatar = null;
        viewDetailOnlineDot = null;
        detailBackCallback = null;
        super.onDestroyView();
    }

    private void bindViews(View view) {
        listContainer = view.findViewById(R.id.listContainer);
        detailContainer = view.findViewById(R.id.detailContainer);
        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
        recyclerViewMessages = view.findViewById(R.id.recyclerViewMessages);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonBack = view.findViewById(R.id.buttonBack);
        buttonSend = view.findViewById(R.id.buttonSend);
        textChatTitle = view.findViewById(R.id.textChatTitle);
        textChatStatus = view.findViewById(R.id.textChatStatus);
        textDetailAvatar = view.findViewById(R.id.textDetailAvatar);
        viewDetailOnlineDot = view.findViewById(R.id.viewDetailOnlineDot);
    }

    private void setupConversationList() {
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewChats.setAdapter(new ConversationAdapter(getConversations(), this::openConversation));
    }

    private void setupHeader() {
        buttonBack.setOnClickListener(v -> showConversationList());
    }

    private void setupMessages() {
        messageAdapter = new ChatMessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        recyclerViewMessages.setLayoutManager(layoutManager);
        recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void setupInput() {
        buttonSend.setOnClickListener(v -> sendCurrentMessage());
        editTextMessage.setOnEditorActionListener(this::onEditorAction);
        editTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateSendButtonState();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupBackHandling() {
        detailBackCallback = new OnBackPressedCallback(false) {
            @Override
            public void handleOnBackPressed() {
                showConversationList();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), detailBackCallback);
    }

    private List<Conversation> getConversations() {
        List<Conversation> conversations = new ArrayList<>();
        conversations.add(new Conversation(
                "MindVibe AI",
                "Minh co the giup ban len ke hoach hom nay.",
                "09:45",
                1,
                "M",
                R.color.vibe_primary,
                true,
                "Dang hoat dong",
                createMessages(
                        ChatMessage.fromAi("Chao ban, hom nay minh co the giup gi?"),
                        ChatMessage.fromUser("Toi muon sap xep cong viec buoi sang."),
                        ChatMessage.fromAi("Minh goi y uu tien 3 viec quan trong truoc, roi de cac viec nho vao cuoi ngay.")
                )
        ));
        conversations.add(new Conversation(
                "Linh Tran",
                "Toi gui file roi, ban xem giup nhe.",
                "08:21",
                2,
                "L",
                R.color.vibe_pink,
                true,
                "Vua moi truy cap",
                createMessages(
                        ChatMessage.fromAi("Sang nay hop luc 10h dung khong?"),
                        ChatMessage.fromUser("Dung roi, minh dang chuan bi slide."),
                        ChatMessage.fromAi("Toi gui file roi, ban xem giup nhe.")
                )
        ));
        conversations.add(new Conversation(
                "Nhom Android",
                "Duc: Layout moi nhin giong Zalo hon roi.",
                "Hom qua",
                5,
                "#",
                R.color.vibe_green,
                false,
                "12 thanh vien",
                createMessages(
                        ChatMessage.fromAi("Duc: Layout moi nhin giong Zalo hon roi."),
                        ChatMessage.fromUser("Minh dang them search bar cho ca 3 tab."),
                        ChatMessage.fromAi("Thao: Nho giu bottom nav luon hien nhe.")
                )
        ));
        conversations.add(new Conversation(
                "Gia dinh",
                "Toi nay an com som nha.",
                "Thu 2",
                0,
                "G",
                R.color.vibe_orange,
                false,
                "4 thanh vien",
                createMessages(
                        ChatMessage.fromAi("Me: Toi nay an com som nha."),
                        ChatMessage.fromUser("Da, con ve truoc 7h."),
                        ChatMessage.fromAi("Ba: Nho mua them trai cay.")
                )
        ));
        conversations.add(new Conversation(
                "Cloud cua toi",
                "Da luu ghi chu: y tuong giao dien chat.",
                "01/06",
                0,
                "C",
                R.color.vibe_teal,
                false,
                "Kho luu tru rieng",
                createMessages(
                        ChatMessage.fromAi("Da luu ghi chu: y tuong giao dien chat."),
                        ChatMessage.fromUser("Them avatar tron, badge do va search bar xanh."),
                        ChatMessage.fromAi("Da dong bo tren Cloud cua toi.")
                )
        ));
        return conversations;
    }

    private void openConversation(Conversation conversation) {
        textChatTitle.setText(conversation.title);
        textChatStatus.setText(conversation.status);
        textDetailAvatar.setText(conversation.avatarText);
        setAvatarColor(textDetailAvatar, conversation.avatarColorRes);
        viewDetailOnlineDot.setVisibility(conversation.online ? View.VISIBLE : View.GONE);
        messageAdapter.setMessages(conversation.messages);
        listContainer.setVisibility(View.GONE);
        detailContainer.setVisibility(View.VISIBLE);
        if (detailBackCallback != null) {
            detailBackCallback.setEnabled(true);
        }
        scrollToBottom();
        updateSendButtonState();
    }

    private void showConversationList() {
        listContainer.setVisibility(View.VISIBLE);
        detailContainer.setVisibility(View.GONE);
        if (detailBackCallback != null) {
            detailBackCallback.setEnabled(false);
        }
    }

    private List<ChatMessage> createMessages(ChatMessage... messages) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        for (ChatMessage message : messages) {
            chatMessages.add(message);
        }
        return chatMessages;
    }

    private void setAvatarColor(TextView avatarView, int colorRes) {
        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.OVAL);
        background.setColor(ContextCompat.getColor(requireContext(), colorRes));
        avatarView.setBackground(background);
    }

    private boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendCurrentMessage();
            return true;
        }
        return false;
    }

    private void sendCurrentMessage() {
        String message = getCurrentMessage();
        if (message.isEmpty() || waitingForAi) {
            return;
        }

        editTextMessage.setText("");
        messageAdapter.addMessage(ChatMessage.fromUser(message));
        int loadingPosition = messageAdapter.addMessage(ChatMessage.loadingFromAi());
        scrollToBottom();
        setWaitingForAi(true);

        aiChatClient.sendMessage(message, new AiResponseCallback() {
            @Override
            public void onSuccess(String response) {
                if (!isViewReady()) {
                    return;
                }

                messageAdapter.replaceMessage(loadingPosition, ChatMessage.fromAi(response));
                setWaitingForAi(false);
                scrollToBottom();
            }

            @Override
            public void onError(String message) {
                if (!isViewReady()) {
                    return;
                }

                messageAdapter.replaceMessage(loadingPosition, ChatMessage.fromAi(message));
                setWaitingForAi(false);
                scrollToBottom();
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getCurrentMessage() {
        Editable editable = editTextMessage.getText();
        return editable == null ? "" : editable.toString().trim();
    }

    private void setWaitingForAi(boolean waiting) {
        waitingForAi = waiting;
        updateSendButtonState();
    }

    private void updateSendButtonState() {
        if (buttonSend == null) {
            return;
        }

        boolean enabled = detailContainer != null
                && detailContainer.getVisibility() == View.VISIBLE
                && !waitingForAi
                && editTextMessage != null
                && !getCurrentMessage().isEmpty();
        buttonSend.setEnabled(enabled);
        buttonSend.setAlpha(enabled ? 1f : 0.45f);
    }

    private void scrollToBottom() {
        if (recyclerViewMessages == null || messageAdapter.getItemCount() == 0) {
            return;
        }

        recyclerViewMessages.post(() ->
                recyclerViewMessages.smoothScrollToPosition(messageAdapter.getItemCount() - 1)
        );
    }

    private boolean isViewReady() {
        return isAdded()
                && recyclerViewMessages != null
                && editTextMessage != null
                && buttonSend != null;
    }

    private interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private static class Conversation {
        final String title;
        final String preview;
        final String time;
        final int unreadCount;
        final String avatarText;
        final int avatarColorRes;
        final boolean online;
        final String status;
        final List<ChatMessage> messages;

        Conversation(
                String title,
                String preview,
                String time,
                int unreadCount,
                String avatarText,
                int avatarColorRes,
                boolean online,
                String status,
                List<ChatMessage> messages
        ) {
            this.title = title;
            this.preview = preview;
            this.time = time;
            this.unreadCount = unreadCount;
            this.avatarText = avatarText;
            this.avatarColorRes = avatarColorRes;
            this.online = online;
            this.status = status;
            this.messages = messages;
        }
    }

    private static class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
        private final List<Conversation> conversations;
        private final OnConversationClickListener clickListener;

        ConversationAdapter(List<Conversation> conversations, OnConversationClickListener clickListener) {
            this.conversations = conversations;
            this.clickListener = clickListener;
        }

        @NonNull
        @Override
        public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_ai, parent, false);
            return new ConversationViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
            holder.bind(conversations.get(position), clickListener);
        }

        @Override
        public int getItemCount() {
            return conversations.size();
        }

        private static class ConversationViewHolder extends RecyclerView.ViewHolder {
            private final TextView textAvatarLetter;
            private final View viewOnlineDot;
            private final TextView textChatName;
            private final TextView textChatPreview;
            private final TextView textChatTime;
            private final TextView textUnreadBadge;

            ConversationViewHolder(@NonNull View itemView) {
                super(itemView);
                textAvatarLetter = itemView.findViewById(R.id.textAvatarLetter);
                viewOnlineDot = itemView.findViewById(R.id.viewOnlineDot);
                textChatName = itemView.findViewById(R.id.textChatName);
                textChatPreview = itemView.findViewById(R.id.textChatPreview);
                textChatTime = itemView.findViewById(R.id.textChatTime);
                textUnreadBadge = itemView.findViewById(R.id.textUnreadBadge);
            }

            void bind(Conversation conversation, OnConversationClickListener clickListener) {
                textAvatarLetter.setText(conversation.avatarText);
                setAvatarColor(conversation.avatarColorRes);
                viewOnlineDot.setVisibility(conversation.online ? View.VISIBLE : View.GONE);
                textChatName.setText(conversation.title);
                textChatPreview.setText(conversation.preview);
                textChatTime.setText(conversation.time);
                if (conversation.unreadCount > 0) {
                    textUnreadBadge.setText(String.valueOf(conversation.unreadCount));
                    textUnreadBadge.setVisibility(View.VISIBLE);
                } else {
                    textUnreadBadge.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(v -> clickListener.onConversationClick(conversation));
            }

            private void setAvatarColor(int colorRes) {
                GradientDrawable background = new GradientDrawable();
                background.setShape(GradientDrawable.OVAL);
                background.setColor(ContextCompat.getColor(itemView.getContext(), colorRes));
                textAvatarLetter.setBackground(background);
            }
        }
    }
}
