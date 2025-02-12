package com.example.proekt.service.impl;

import com.example.proekt.model.Advertisement;
import com.example.proekt.model.Message;
import com.example.proekt.model.MessageThread;
import com.example.proekt.model.exceptions.InvalidMessageThreadData;
import com.example.proekt.model.exceptions.InvalidMessageThreadIdException;
import com.example.proekt.repository.MessageThreadRepository;
import com.example.proekt.service.AdvertisementService;
import com.example.proekt.service.MessageService;
import com.example.proekt.service.MessageThreadService;
import com.example.proekt.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageThreadServiceImpl implements MessageThreadService {

    private final MessageService messageService;
    private final MessageThreadRepository messageThreadRepository;
    private final UserService userService;

    private final AdvertisementService advertisementService;

    public MessageThreadServiceImpl(MessageService messageService, MessageThreadRepository messageThreadRepository, UserService userService, AdvertisementService advertisementService) {
        this.messageService = messageService;
        this.messageThreadRepository = messageThreadRepository;
        this.userService = userService;
        this.advertisementService = advertisementService;
    }

    @Override
    public MessageThread findById(Long id) {
        return this.messageThreadRepository.findById(id).orElseThrow(InvalidMessageThreadIdException::new);
    }

    @Override
    public MessageThread addAMessage(Long mst, Long msg) {
       MessageThread msgt =this.findById(mst);
      List<Message> messages=msgt.getMessages();
      messages.add(this.messageService.findById(msg));
      msgt.setMessages(messages);

      return this.messageThreadRepository.save(msgt);
    }



    @Override
    public MessageThread create( String user1, String user2, Long a) {
        List<Message> messages=new ArrayList<>();
        return this.messageThreadRepository.save(new MessageThread(userService.findByUsername(user1),
                userService.findByUsername(user2),messages, advertisementService.findById(a)));
    }

    @Override
    public List<MessageThread> findAllByAdvertisement(Long advertisement) {
      return this.messageThreadRepository.findAllByAdvertisement(advertisementService.findById(advertisement));
    }

    @Override
    public MessageThread findByUser1AndUser2AndAdvertisement(String user1, String user2, Long a) {
       MessageThread messageThread= messageThreadRepository.findByUser1AndUser2AndAdvertisement(userService.findByUsername(user1),
               userService.findByUsername(user2),advertisementService.findById(a)).orElse(null);
        MessageThread messageThread2=messageThreadRepository.findByUser1AndUser2AndAdvertisement(userService.findByUsername(user2),
                userService.findByUsername(user1),advertisementService.findById(a)).orElse(null);
       if (messageThread!=null){
           return messageThread;
       }
       else if (messageThread2!=null)
         return messageThread2;

       else return null;
    }

    @Override
    public MessageThread delete(Long id) {
        MessageThread messageThread=this.findById(id);
        this.messageThreadRepository.delete(messageThread);
        return messageThread;
    }

    @Override
    public List<MessageThread> deleteByAdvertisement(Long ad) {
        List<MessageThread> messageThreads=findAllByAdvertisement(ad);
        for(MessageThread msg:messageThreads){
            this.delete(msg.getId());
        }
        return messageThreads;
    }


}
