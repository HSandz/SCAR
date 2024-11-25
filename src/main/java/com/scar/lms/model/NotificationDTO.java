package com.scar.lms.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private String content;
    private LocalDateTime sendTime;
    private NotificationType type;

    public enum NotificationType {
        BOOK_BORROWED,
        BOOK_RETURNED,
        OVERDUE_NOTICE,
        OTHER
    }
}
