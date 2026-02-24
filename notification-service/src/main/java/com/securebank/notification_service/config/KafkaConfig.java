package com.securebank.notification_service.config;

import com.securebank.notification_service.dto.event.AccountOpenedEvent;
import com.securebank.notification_service.dto.event.DocumentVerifiedEvent;
import com.securebank.notification_service.dto.event.UserRegisteredEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return props;
    }

    @Bean
    public ConsumerFactory<String, UserRegisteredEvent> userRegisteredConsumerFactory() {
        Map<String, Object> props = baseConfig();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-user-registered-v2");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(UserRegisteredEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> userRegisteredKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserRegisteredEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userRegisteredConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, AccountOpenedEvent> accountOpenedConsumerFactory() {
        Map<String, Object> props = baseConfig();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-account-opened-v2");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(AccountOpenedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AccountOpenedEvent> accountOpenedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, AccountOpenedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(accountOpenedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, DocumentVerifiedEvent> documentVerifiedConsumerFactory() {
        Map<String, Object> props = baseConfig();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-document-verified-v2");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                new JsonDeserializer<>(DocumentVerifiedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DocumentVerifiedEvent> documentVerifiedKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, DocumentVerifiedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(documentVerifiedConsumerFactory());
        return factory;
    }
}