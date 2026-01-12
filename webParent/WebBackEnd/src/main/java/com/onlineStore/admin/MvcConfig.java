package com.onlineStore.admin;

import com.onlineStore.admin.utility.paging.PagingAndSortingArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // AG-ASSET-PATH-005: New hierarchical asset structure
        addResourceHandler(registry, "tenants");

        // Legacy paths removed to strictly enforce tenant isolation and hierarchy
        // All assets must now be accessed via /tenants/{id}/assets/...
    }

    private void addResourceHandler(ResourceHandlerRegistry registry, String pathPattern) {

        Path path = Paths.get(pathPattern);
        String absolutePath = path.toFile().getAbsolutePath();

        registry
                .addResourceHandler("/" + pathPattern + "/**")
                .addResourceLocations("file:/" + absolutePath + "/");

    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
        resolvers.add(new com.onlineStore.admin.article.paging.PagingAndSortingArgumentResolver());
    }

}
