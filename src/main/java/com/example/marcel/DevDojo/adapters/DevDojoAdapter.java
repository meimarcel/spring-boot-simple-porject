package com.example.marcel.DevDojo.adapters;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class DevDojoAdapter implements WebMvcConfigurer {

    private static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 10);

    @Override

    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageableHandler = new PageableHandlerMethodArgumentResolver();
        pageableHandler.setFallbackPageable(DEFAULT_PAGE_REQUEST);
        resolvers.add(pageableHandler);
    }
}
