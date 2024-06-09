package com.bookkeeper.AssetSummary.client;

import com.bookkeeper.AssetSummary.service.AssetSummaryService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;

@Component
@Slf4j
public class DirectReceiver {

    @Autowired
    AssetSummaryService assetSummaryService;

    @RabbitListener(queues = "${rabbitmqConfig.queue}")
    public void process(HashMap<String, Object> message,
                        Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {

            if(message.get("cancel") == null)
                assetSummaryService.cancelTransaction(message);

            assetSummaryService.updateAsset(message);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Process message encounter exception: ", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
