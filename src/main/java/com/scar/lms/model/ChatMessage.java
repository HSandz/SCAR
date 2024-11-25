package com.scar.lms.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private String profilePictureUrl;
    private MessageType type;

    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

}