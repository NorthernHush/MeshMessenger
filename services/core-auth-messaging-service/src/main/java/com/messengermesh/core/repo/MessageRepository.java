package com.messengermesh.core.repo;

import com.messengermesh.core.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, String> {
    Page<Message> findByChannelIdOrderByTimestampDesc(String channelId, Pageable pageable);
}
