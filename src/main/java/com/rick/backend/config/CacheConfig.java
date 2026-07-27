package com.rick.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats());

        /**
         * 一旦调用了 setCacheNames()，行为会变成"白名单模式"——只有名单里的名字才会被真正创建为 cache，不在名单里的 @Cacheable(value = "未声明的名字") 会被当作"没有缓存"直接透传（方法照常执行，但不会报错，只是缓存不生效，容易被忽略排查半天）。
         * cacheManager.setCacheNames(Arrays.asList("courses", "users"));
         * 之后 @Cacheable(value = "teachers", ...) 会静默失效，不缓存也不报错
         */
        // cacheManager.setCacheNames(Arrays.asList("courses", "users"));

        return cacheManager;
    }

}