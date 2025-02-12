package com.example.proekt;

import com.example.proekt.model.*;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.MessageService;
import com.example.proekt.service.MessageThreadService;
import com.example.proekt.service.UserService;
import com.example.proekt.web.MessagesController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MessagesController.class)
public class MessagesControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageThreadService messageThreadService;

    @MockBean
    private AdvertisementService advertisementService;

    private User testUser1;
    private User testUser2;
    private Message testMessage;
    private MessageThread testMessageThread;
    private Advertisement testAdvertisement;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testUser1 = new User("testuser1", "password", Role.ROLE_USER);
        testUser2 = new User("testuser2", "password", Role.ROLE_USER);

        testAdvertisement = new Advertisement();
        testAdvertisement.setId(1L);
        testAdvertisement.setOwner(testUser2);

        testMessage = new Message(testUser1, testUser2, "Test message content", LocalDateTime.now());
        testMessage.setId(1L);

        testMessageThread = new MessageThread(testUser1, testUser2, Arrays.asList(testMessage), testAdvertisement);
        testMessageThread.setId(1L);

        when(userService.findByUsername("testuser1")).thenReturn(testUser1);
        when(userService.findByUsername("testuser2")).thenReturn(testUser2);
    }

    @Test
    @WithMockUser(username = "testuser1", roles = "USER")
    void testOpenConversation() throws Exception {
        when(messageThreadService.findById(1L)).thenReturn(testMessageThread);
        when(userService.findByUsername("testuser1")).thenReturn(testUser1);

        mockMvc.perform(get("/messages/conversation/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("messageThread"))
                .andExpect(model().attributeExists("threadId"))
                .andExpect(model().attributeExists("thread"))
                .andExpect(model().attributeExists("messages"))
                .andExpect(model().attributeExists("currentUser"))
                .andExpect(model().attribute("threadId", 1L))
                .andExpect(model().attribute("thread", testMessageThread))
                .andExpect(model().attribute("messages", testMessageThread.getMessages()))
                .andExpect(model().attribute("currentUser", testUser1));
    }

    @Test
    @WithMockUser(username = "testuser1", roles = "USER")
    void testSendMessage() throws Exception {
        Message newMessage = new Message(testUser1, testUser2, "New test message", LocalDateTime.now());
        newMessage.setId(2L);

        when(messageThreadService.findById(1L)).thenReturn(testMessageThread);
        when(messageService.sendMessage(eq("testuser1"), eq("testuser2"), eq("New test message")))
                .thenReturn(newMessage);
        when(messageThreadService.addAMessage(eq(1L), eq(2L))).thenReturn(testMessageThread);

        mockMvc.perform(post("/messages/send/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .param("user", "testuser1")
                        .param("content", "New test message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/messages/conversation/1"));
    }

    @Test
    @WithMockUser(username = "testuser1", roles = "USER")
    void testListMessages() throws Exception {
        List<MessageThread> messageThreads = Arrays.asList(testMessageThread);
        when(messageThreadService.findAllByAdvertisement(1L)).thenReturn(messageThreads);

        mockMvc.perform(get("/messages/conversation/all/{id}", 1L)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("listThreads"))
                .andExpect(model().attributeExists("threadIds"))
                .andExpect(model().attributeExists("messagethreads"))
                .andExpect(model().attribute("messagethreads", messageThreads))
                .andExpect(model().attribute("threadIds", Arrays.asList(1L)));
    }
}