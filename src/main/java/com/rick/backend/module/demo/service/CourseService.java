package com.rick.backend.module.demo.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.rick.backend.module.demo.dao.CourseDAO;
import com.rick.backend.module.demo.entity.Course;
import com.rick.db.plugin.BaseServiceImpl;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Validated
public class CourseService extends BaseServiceImpl<CourseDAO, Course, Long> {

    CacheManager cacheManager;

    // 编程式缓存，自定义 Cache，没有加入 cacheManager 的管理
    private final Cache<Long, Course> courseCache = Caffeine.newBuilder()
            .expireAfter(new Expiry<Long, Course>() {
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
            .build();

    public CourseService(CourseDAO baseDAO, CacheManager cacheManager) {
        super(baseDAO);
        this.cacheManager = cacheManager;
    }

    @Cacheable(value = "courses", key = "#id")
//     等价于
//    @Cacheable(cacheNames = "courses", key = "#id")
    @Override
    public Optional<Course> selectById(Long id) {
        return super.selectById(id);
    }

    /**
     * 编程式缓存
     * @param id
     * @return
     */
    public Optional<Course> selectById2(Long id) {
        Course course = courseCache.get(id, (_id) -> selectById(_id).orElse(null));
        return Optional.ofNullable(course);
    }

    /**
     * 编程式缓存 从 cacheManager 获取缓存
     * @param cacheName
     * @param id
     * @return
     */
    public Course getFromCacheManager(String cacheName, Long id) {
        return (Course)cacheManager.getCache(cacheName).get(id).get();
    }

    @CacheEvict(value = "courses", key = "#id")
    @Override
    public int deleteById(Long id) {
        return super.deleteById(id);
    }

    @CachePut(value = "courses", key = "#course.id")
    @Override
    public Course update(Course course) {
        return super.update(course);
    }
}