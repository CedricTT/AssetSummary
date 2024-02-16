package com.bookkeeper.AssetSummary.client;

import com.bookkeeper.AssetSummary.service.AssetSummaryService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
@Slf4j
public class DirectReceiver {

    @Autowired
    AssetSummaryService assetSummaryService;

    @RabbitListener(queues = "PaymentRecordQueue")
    public void process(Message message, Channel channel) throws IOException {
        log.info(Arrays.toString(message.getBody()));
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            assetSummaryService.updateAsset(message);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Process message encounter exception: ", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
