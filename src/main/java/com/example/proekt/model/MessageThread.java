package com.example.proekt.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class MessageThread {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    @ManyToOne
    private Advertisement advertisement;
    @OneToMany()
    private List<Message> messages = new ArrayList<>();

    public MessageThread() {}

    public MessageThread(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public MessageThread( User user1, User user2, List<Message> messages, Advertisement advertisement) {
        this.user1 = user1;
        this.user2 = user2;
        this.messages = messages;
        this.advertisement=advertisement;
    }
}
