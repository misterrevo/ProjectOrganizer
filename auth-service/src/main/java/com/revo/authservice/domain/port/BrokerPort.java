package com.revo.authservice.domain.port;

public interface BrokerPort {
    void send(String topic, Object object);
}
