package pl.koder95.sbp.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.caffeine.Bucket4jCaffeine;
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BucketConfig {
    @Bean
    public ProxyManager<Object> proxyManager() {
        Caffeine<Object, Object> cache = Caffeine.newBuilder()
                .maximumSize(100_000);

        return Bucket4jCaffeine.builderFor(cache)
                .expirationAfterWrite(
                        ExpirationAfterWriteStrategy
                                .basedOnTimeForRefillingBucketUpToMax(Duration.ofHours(1))
                )
                .build();
    }
}
