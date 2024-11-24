package com.scar.lms.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String content;
    private String sender;
    private MessageType type;
    private String profilePictureUrl;

    @SuppressWarnings("unused")
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

}