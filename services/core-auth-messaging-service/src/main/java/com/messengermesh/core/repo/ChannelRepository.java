package com.messengermesh.core.repo;

import com.messengermesh.core.model.Channel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChannelRepository extends MongoRepository<Channel, String> {
}
