package com.example.mindvibe.chat;

public class MindVibePromptBuilder {
    private static final String GUIDANCE =
            "Bạn là MindVibe AI, một người bạn đồng hành tinh thần. "
                    + "Trả lời bằng tiếng Việt tự nhiên, ấm áp, ngắn gọn và thực tế. "
                    + "Không chẩn đoán y khoa. Nếu người dùng có dấu hiệu khủng hoảng hoặc tự làm hại, "
                    + "hãy khuyên họ liên hệ người thân, chuyên gia hoặc dịch vụ khẩn cấp tại nơi họ sống.";

    public String buildUserPrompt(String message) {
        return GUIDANCE + "\n\nTin nhắn của người dùng: " + message;
    }
}
