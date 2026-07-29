package com.rick.backend.config;

import com.rick.backend.module.auth.entity.User;
import com.rick.backend.module.auth.service.UserContextHolder;
import com.rick.backend.module.common.servlet.AccessFilter;
import com.rick.common.http.exception.ApiExceptionHandler;
import com.rick.common.http.web.SharpWebMvcConfigurer;
import com.rick.db.repository.TableDAO;
import com.rick.db.repository.model.EntityId;
import com.rick.db.repository.support.IdToEntityConverterFactory;
import com.rick.db.repository.support.InsertUpdateCallback;
import com.rick.db.repository.support.baseinfo.ExtendInsertUpdateCallback;
import com.rick.db.repository.support.baseinfo.ExtendTableDAOImpl;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Rick.Xu
 * @date 2025/11/10 16:48
 */
@Configuration
@ComponentScan(basePackageClasses = {ApiExceptionHandler.class})
public class ProjectConfig extends SharpWebMvcConfigurer {

    @Bean
    @Primary
    public TableDAO tableDAO(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new ExtendTableDAOImpl(namedParameterJdbcTemplate) {
            @Override
            public long getUserId() {
                User user = UserContextHolder.get();
                user = (user == null) ? User.builder().id(1L).build() : user;
                return user.getId();
//                return 1L;
            }

            @Override
            protected void addInsertInfo(Map<String, Object> paramMap) {
                paramMap.put("groupId", 100L);
            }
        };
    }

    @Bean
    public InsertUpdateCallback insertCallback() {
        return new ExtendInsertUpdateCallback() {
            @Override
            public void handler(boolean insert, EntityId<Long> entity, Map<String, Object> args) {
                super.handler(insert, entity, args);
//                baseEntityInfoGetter.setGroupId(100L)
            }
        };
    }

    @Override
    public List<ConverterFactory> converterFactories() {
        // 发起 GET 请求的时候，允许值映射到实体对象的 id 字段上，不常用，提供传参的多样性
        // private Person person;
        // GET person = "1" => person.setId(1L)
        return Arrays.asList(new IdToEntityConverterFactory());
    }

    @Bean
    public FilterRegistrationBean httpServletRequestReplacedFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AccessFilter());
        // /* 是全部的请求拦截，和Interceptor的拦截地址/**区别开
        registration.addUrlPatterns("/*");
        registration.setName("accessRequestFilter");
        registration.setOrder(1);
        return registration;
    }

}

