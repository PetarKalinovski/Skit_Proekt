package com.example.proekt;

import com.example.proekt.ProektApplication;
import com.example.proekt.model.*;
import com.example.proekt.repository.MessageRepository;
import com.example.proekt.service.MessageService;
import com.example.proekt.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ProektApplication.class)
@ActiveProfiles("test")
@Transactional
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

    private User sender;
    private User recipient;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        messageRepository.deleteAll();

        sender = userService.register("sender", "password", "password", Role.ROLE_USER);
        recipient = userService.register("recipient", "password", "password", Role.ROLE_USER);

        testMessage = messageService.sendMessage(
                sender.getUsername(),
                recipient.getUsername(),
                "Test message content"
        );
    }

    @Test
    void testSendMessage() {
        assertNotNull(testMessage.getId());
        assertEquals(sender, testMessage.getSender());
        assertEquals(recipient, testMessage.getRecipient());
        assertEquals("Test message content", testMessage.getContent());
        assertNotNull(testMessage.getSentAt());
        assertTrue(testMessage.getSentAt().isBefore(LocalDateTime.now().plusSeconds(1)));


        Message persistedMessage = messageService.findById(testMessage.getId());
        assertEquals("Test message content", persistedMessage.getContent());
        assertEquals(sender, persistedMessage.getSender());
    }

    @Test
    void testFindById() {
        Message foundMessage = messageService.findById(testMessage.getId());

        assertNotNull(foundMessage);
        assertEquals(testMessage.getId(), foundMessage.getId());
        assertEquals(testMessage.getContent(), foundMessage.getContent());
        assertEquals(testMessage.getSender(), foundMessage.getSender());
        assertEquals(testMessage.getRecipient(), foundMessage.getRecipient());
    }

    @Test
    void testSendMultipleMessages() {
        Message message2 = messageService.sendMessage(
                sender.getUsername(),
                recipient.getUsername(),
                "Second message"
        );

        Message message3 = messageService.sendMessage(
                recipient.getUsername(),
                sender.getUsername(),
                "Reply message"
        );

        assertNotNull(messageService.findById(message2.getId()));
        assertNotNull(messageService.findById(message3.getId()));

        assertEquals("Second message", message2.getContent());
        assertEquals("Reply message", message3.getContent());

        assertEquals(sender, message2.getSender());
        assertEquals(recipient, message3.getSender());
    }

    @Test
    void testMessageTimestamp() {
        LocalDateTime beforeSend = LocalDateTime.now();
        Message message = messageService.sendMessage(
                sender.getUsername(),
                recipient.getUsername(),
                "Timestamp test message"
        );
        LocalDateTime afterSend = LocalDateTime.now();

        assertTrue(message.getSentAt().isAfter(beforeSend) || message.getSentAt().isEqual(beforeSend));
        assertTrue(message.getSentAt().isBefore(afterSend) || message.getSentAt().isEqual(afterSend));

        Message persistedMessage = messageService.findById(message.getId());
        assertEquals(message.getSentAt(), persistedMessage.getSentAt());
    }
}