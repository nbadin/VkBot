package com.example.vkbot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import com.google.gson.annotations.SerializedName;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class VkObject {
    @SerializedName("group_id")
    private long groupId;
    private String type;
    @SerializedName("event_id")
    private String eventId;
    private String v;
    private ObjectData object;

    @Getter
    public static class ObjectData {
        private Message message;
        private ClientInfo clientInfo;

        @Getter
        public static class Message {
            private long date;
            @SerializedName("from_id")
            private long fromId;
            private long id;
            private long version;
            private int out;
            @SerializedName("important")
            private boolean important;
            @SerializedName("is_hidden")
            private boolean isHidden;
            private Object[] attachments;
            @SerializedName("conversation_message_id")
            private long conversationMessageId;
            private Object[] fwdMessages;
            private String text;
            private long peerId;
            private long randomId;
        }

        @Getter
        public static class ClientInfo {
            @SerializedName("button_actions")
            private String[] buttonActions;
            private boolean keyboard;
            @SerializedName("inline_keyboard")
            private boolean inlineKeyboard;
            private boolean carousel;
            @SerializedName("lang_id")
            private int langId;
        }
    }
}
