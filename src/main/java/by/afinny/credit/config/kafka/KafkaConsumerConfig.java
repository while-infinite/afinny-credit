package by.afinny.credit.config.kafka;

import by.afinny.credit.config.kafka.properties.KafkaConfigProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfigProperties.class)
public class KafkaConsumerConfig {

    private final KafkaConfigProperties config;
    private KafkaProperties kafkaProperties;
    private String BOOTSTRAP_SERVERS;

    @PostConstruct
    private void createKafkaProperties() {
        kafkaProperties = config.getKafkaProperties();
        BOOTSTRAP_SERVERS = config.getBootstrapServers();
    }

    @Bean
    public DefaultKafkaConsumerFactory<String, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(getKafkaConsumerProperties());
    }

    @Bean(name = "kafkaListener")
    public ConcurrentKafkaListenerContainerFactory<String, Object> factory(ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    private Map<String, Object> getKafkaConsumerProperties() {
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerProperties.get("key.deserializer"));
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerProperties.get("value.deserializer"));
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, "false");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "by.afinny.credit.dto.kafka.CardEvent");
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, ErrorHandlingDeserializer.class);

        return props;
    }
}
