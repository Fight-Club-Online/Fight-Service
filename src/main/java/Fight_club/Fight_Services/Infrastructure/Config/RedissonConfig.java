package Fight_club.Fight_Services.Infrastructure.Config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RedissonConfig {
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password:}")
    private String password;

    @Value("${spring.data.redis.ssl.enabled}")
    private boolean ssl;


    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        String protocol = ssl ? "rediss://" : "redis://";
        String address = protocol + host + ":" + port;

        config.useSingleServer()
                .setAddress(address)
                .setPassword(password.isEmpty() ? null : password);

        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }
}

