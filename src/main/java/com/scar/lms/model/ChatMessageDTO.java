package com.scar.lms.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDTO {
    private String content;
    private String sender;
    private LocalDateTime timestamp;
    private String profilePictureUrl;
    private MessageType type;

    @SuppressWarnings("unused")
    public enum MessageType {
        CHAT, JOIN, LEAVE
    }

}