package com.courtcast.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * File created by Hananiah Davis on May 05, 2025
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger logger = Logger.getLogger(CacheConfig.class.getName());

    @Bean
    public CacheManager cacheManager() {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(500)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats();

        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeine);

        // Optional: Periodically log stats
        new Thread(() -> {
            while (true) {
                cacheManager.getCacheNames().forEach(name -> {
                    var cache = cacheManager.getCache(name);
                    if (cache != null && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> nativeCache) {
                        logger.info("Cache '" + name + "' stats: " + nativeCache.stats());
                    }
                });

                try {
                    Thread.sleep(60_000); // Log every minute
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        return cacheManager;
    }
}

