package com.scar.lms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NOTIFIES")
public class Notify {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "IS_READ")
    private boolean isRead;

    @ManyToOne(fetch = FetchType.EAGER,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER,
            cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "NOTIFICATION_ID")
    private Notification notification;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notify notify = (Notify) o;
        return id == notify.id &&
                isRead == notify.isRead &&
                Objects.equals(user, notify.user) &&
                Objects.equals(notification, notify.notification);
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
