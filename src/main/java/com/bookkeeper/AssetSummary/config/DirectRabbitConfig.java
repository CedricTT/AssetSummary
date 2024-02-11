package com.bookkeeper.AssetSummary.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DirectRabbitConfig {
    public Queue PaymentRecordQueue() {
        return new Queue("PaymentRecordQueue", true);
    }

    @Bean
    DirectExchange PaymentRecordExchange() {
        return new DirectExchange("PaymentRecordExchange",true, false);
    }

    @Bean
    Binding bindingDirect() {
        return BindingBuilder.bind(PaymentRecordQueue()).to(PaymentRecordExchange()).with("record");
    }
}
