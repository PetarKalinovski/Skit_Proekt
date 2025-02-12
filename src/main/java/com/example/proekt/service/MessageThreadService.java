package com.example.proekt.service;

import com.example.proekt.model.Advertisement;
import com.example.proekt.model.Message;
import com.example.proekt.model.MessageThread;

import java.util.List;

public interface MessageThreadService{

    public MessageThread findById(Long id);
    public MessageThread addAMessage(Long mst, Long msg);

    public MessageThread create(String user1,String user2, Long a);

    public  List<MessageThread> findAllByAdvertisement(Long advertisement);
    public MessageThread findByUser1AndUser2AndAdvertisement(String user1, String user2, Long a);

    public MessageThread delete(Long id);

    public List<MessageThread> deleteByAdvertisement(Long ad);

}
