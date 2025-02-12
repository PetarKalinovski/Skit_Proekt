package com.example.proekt.service.impl;

import com.example.proekt.model.Message;
import com.example.proekt.model.exceptions.InvalidMessageIDException;
import com.example.proekt.repository.MessageRepository;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.MessageService;
import com.example.proekt.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final AdvertisementService advertisementService;
    private final UserService userService;

    public MessageServiceImpl(MessageRepository messageRepository, AdvertisementService advertisementService, UserService userRepository) {
        this.messageRepository = messageRepository;
        this.advertisementService = advertisementService;
        this.userService = userRepository;
    }

    @Override
    public Message findById(Long id) {
        return this.messageRepository.findById(id).orElseThrow(InvalidMessageIDException::new);
    }

    @Override
    public Message sendMessage(String senderId, String recipientId, String content) {
        return this.messageRepository.save(new Message(userService.findByUsername(senderId),
                userService.findByUsername(recipientId), content ,LocalDateTime.now()));
    }
}
