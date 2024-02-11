package com.bookkeeper.AssetSummary.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Slf4j
@RabbitListener(queues = "PaymentRecordQueue")
public class DirectReceiver {

    @RabbitHandler
    public void process(HashMap<String, Object> message) {
        log.info(message.toString());
    }
}
