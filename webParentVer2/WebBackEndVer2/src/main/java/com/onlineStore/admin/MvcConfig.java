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

    @org.springframework.beans.factory.annotation.Value("${app.storage.tenants-path}")
    private String tenantsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // AG-ASSET-PATH-005: New hierarchical asset structure
        addResourceHandler(registry, tenantsPath);
    }

    private void addResourceHandler(ResourceHandlerRegistry registry, String pathPattern) {
        // AG-ASSET-PATH-005: Strict Deterministic Asset Path
        // Priority: Use configured path relative to work dir (usually root).
        
        Path resolvedPath = Paths.get(pathPattern);
        String absolutePath = resolvedPath.toAbsolutePath().toUri().toString();

        // Ensure directory logic by appending trailing slash if missing
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }

        // Log the final path for verification (visible in console)
        System.out.println("AG-ASSET-CONFIG: Mapping /" + pathPattern + "/** to " + absolutePath);

        registry.addResourceHandler("/" + pathPattern + "/**")
                .addResourceLocations(absolutePath);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
        resolvers.add(new com.onlineStore.admin.article.paging.PagingAndSortingArgumentResolver());
    }

}
