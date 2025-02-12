package com.example.proekt.repository;

import com.example.proekt.model.Advertisement;
import com.example.proekt.model.MessageThread;
import com.example.proekt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageThreadRepository extends JpaRepository<MessageThread,Long> {

    Optional<MessageThread> findByUser1AndUser2AndAdvertisement(User user1, User user2, Advertisement advertisement);
    Optional<MessageThread> findByUser2AndUser1AndAdvertisement(User user2, User user1, Advertisement advertisement);

    List<MessageThread> findAllByAdvertisement(Advertisement advertisement);


}
