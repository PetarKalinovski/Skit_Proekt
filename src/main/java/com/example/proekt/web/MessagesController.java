package com.example.proekt.web;

import com.example.proekt.model.Advertisement;
import com.example.proekt.model.Message;
import com.example.proekt.model.MessageThread;
import com.example.proekt.model.User;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.MessageService;
import com.example.proekt.service.MessageThreadService;
import com.example.proekt.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/messages")
public class MessagesController {


    private final MessageService messageService;


    private final UserService userService;

    private final MessageThreadService messageThreadService;

    private final AdvertisementService advertisementService;

    public MessagesController(MessageService messageService, UserService userService, MessageThreadService messageThreadService, AdvertisementService advertisementService) {
        this.messageService = messageService;
        this.userService = userService;
        this.messageThreadService = messageThreadService;
        this.advertisementService = advertisementService;
    }

//    @GetMapping("/conversation")
//    public String firstTimeConversation(@RequestParam Long ad, Principal principal, Model model){
//
//
//        return "messageThread";
//    }

    @GetMapping("/conversation/{id}")
    public String openConversation(@PathVariable Long id, Model model, Principal principal) {
        MessageThread messageThread=messageThreadService.findById(id);

        model.addAttribute("threadId", id);
        model.addAttribute("thread", messageThread);
        model.addAttribute("messages", messageThread.getMessages());
        model.addAttribute("currentUser", userService.findByUsername(principal.getName()));

        return "messageThread";
    }

    @PostMapping("/send/{id}")
    public String sendMessage(@PathVariable Long id, @RequestParam String user, @RequestParam String content) {
        MessageThread mst = messageThreadService.findById(id);
        String user2;
        if (user.equals(mst.getUser2().getUsername()))
            user2 = mst.getUser1().getUsername();
        else
            user2 = mst.getUser2().getUsername();
        Message m = messageService.sendMessage(user, user2, content);
        messageThreadService.addAMessage(mst.getId(), m.getId());

        return "redirect:/messages/conversation/" + mst.getId();
    }

    @GetMapping("/conversation/all/{id}")
    public String listMessages(@PathVariable Long id, Model model){
        List<MessageThread> messageThreads=messageThreadService.findAllByAdvertisement(id);
        model.addAttribute("threadIds", messageThreads.stream().map(MessageThread::getId).collect(Collectors.toList()));
        model.addAttribute("messagethreads", messageThreads);
        return "listThreads";
    }
}

