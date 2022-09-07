package com.devjaewoo.springrabbitmqtest.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final TopicExchange topicExchange;
    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin rabbitAdmin;

    @PostMapping("/rabbit/register")
    public String register(@RequestBody QueueRequest queueRequest) {
        String queueName = queueRequest.getQueue();
        String routingKey = "com.devjaewoo.order.*";
        log.info("Binding queue " + queueName + " with Routing key " + routingKey);

        Binding binding = BindingBuilder.bind(new Queue(queueName)).to(topicExchange).with(routingKey);
        rabbitAdmin.declareBinding(binding);

        return "{\"result\": \"Success\"}";
    }

    @GetMapping("/rabbit/publish/{id}")
    public String publish(@PathVariable Long id) {
        String message = "Ordered ID: " + id;
        String routingKey = "com.devjaewoo.order." + id;

        log.info("Publish message " + message + " to " + routingKey);
        rabbitTemplate.convertAndSend("com.exchange", routingKey, message);

        return "Publish Success";
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class QueueRequest {
        private String queue;
    }
}
