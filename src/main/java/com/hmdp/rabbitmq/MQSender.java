package com.hmdp.rabbitmq;

import com.hmdp.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String ROUTING_KEY = "seckill.message";

    public void send(String message) {
        log.info("message send : {}", message);
        rabbitTemplate.convertAndSend(ROUTING_KEY, message);
    }
}
