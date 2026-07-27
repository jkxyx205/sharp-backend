package com.rick.backend.module.demo;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.rick.backend.module.demo.entity.Course;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Slf4j
public class CacheTest {

    @Autowired
    CacheManager cacheManager;

    @Test
    public void testCache() {
        // caffeine 手动创建 cache
        com.github.benmanes.caffeine.cache.Cache<Long, Course> cache1 = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10_000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .recordStats().build();

        com.github.benmanes.caffeine.cache.Cache<Long, Course> cache12 = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(10_000)
                .expireAfter(new Expiry<Long, Course>() {
                    // 跟丰富的过期控制
                    @Override
                    public long expireAfterCreate(Long key, Course course, long currentTime) {
                        return TimeUnit.MINUTES.toNanos(5);
                    }
                    @Override
                    public long expireAfterUpdate(Long key, Course course, long currentTime, long currentDuration) {
                        return expireAfterCreate(key, course, currentTime);
                    }
                    @Override
                    public long expireAfterRead(Long key, Course course, long currentTime, long currentDuration) {
                        return currentDuration;
                    }
                })
                .recordStats().build();


        // cacheManager 手动创建 cache，Caffeine 参照配置 CacheConfig
        Cache testCache = cacheManager.getCache("test");
        Course course = testCache.get(100L, Course.class);
        log.info(course == null ? "course is null" : course.getName());
        assertNull(course);

        testCache.put(100L, Course.builder()
                .id(100L)
                .name("Test")
                .teacher("Rick")
                .totalHours(10)
                .isEnded(false)
                .build());


        course = testCache.get(100L, Course.class);
        log.info(course == null ? "course is null" : "course name: " + course.getName());
        assertNotNull(course);
    }
}
