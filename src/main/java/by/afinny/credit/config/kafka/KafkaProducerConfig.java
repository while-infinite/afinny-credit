package by.afinny.credit.config.kafka;

import by.afinny.credit.config.kafka.properties.KafkaConfigProperties;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfigProperties.class)
public class KafkaProducerConfig {

    private final KafkaConfigProperties config;
    private KafkaProperties kafkaProperties;
    private String BOOTSTRAP_SERVERS;

    @PostConstruct
    private void createKafkaProperties() {
        kafkaProperties = config.getKafkaProperties();
        BOOTSTRAP_SERVERS = config.getBootstrapServers();
    }

    @Bean
    public DefaultKafkaProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(getKafkaProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    private Map<String, Object> getKafkaProducerProperties() {
        Map<String, Object> producerProperties = kafkaProperties.buildProducerProperties();

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerProperties.get("key.serializer"));
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerProperties.get("value.serializer"));

        return props;
    }
}
