package com.revo.authservice.infrastructure.utils;

import com.revo.authservice.domain.dto.UserDto;
import com.revo.authservice.domain.port.BrokerPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
class BrokerAdapter implements BrokerPort {

    private final KafkaTemplate<String, UserDto> kafkaTemplate;

    BrokerAdapter(KafkaTemplate<String, UserDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topic, Object object) {
        kafkaTemplate.send(topic, (UserDto) object);
    }
}
