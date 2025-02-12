package com.example.proekt.service;

import com.example.proekt.model.Advertisement;
import com.example.proekt.model.Message;
import com.example.proekt.model.MessageThread;
import com.example.proekt.repository.MessageRepository;

import java.time.LocalDateTime;

public interface MessageService {


    public Message findById(Long id);
    public Message sendMessage(String senderId, String recipientId, String content);
}
