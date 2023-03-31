package by.afinny.credit.config.kafka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigProperties {

    private String bootstrapServers;
    private Map<String, Topic> topics = new HashMap<>();
    private KafkaProperties kafkaProperties;

    @Getter
    @Setter
    public static class Topic {
        private String path;
        private boolean enabled;
    }
}
