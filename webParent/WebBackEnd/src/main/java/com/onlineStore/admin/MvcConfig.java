package com.onlineStore.admin;

import com.onlineStore.admin.utility.paging.PagingAndSortingArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;
import java.util.List;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @org.springframework.beans.factory.annotation.Value("${app.storage.tenants-path}")
    private String tenantsPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        exposeDirectory(tenantsPath, registry);
    }

    private void exposeDirectory(String dirName, ResourceHandlerRegistry registry) {
        java.nio.file.Path path = Paths.get(dirName);
        String absolutePath = path.toFile().getAbsolutePath();

        String logicalPath = "/" + dirName + "/**";
        
        // Handle Windows paths correctly by converting to URI
        String location = "file:/" + absolutePath + "/";

        registry.addResourceHandler(logicalPath)
                .addResourceLocations(location);

        // Add logging to help user debug path issues
        System.out.println("Mapped " + logicalPath + " to " + location);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
        resolvers.add(new com.onlineStore.admin.article.paging.PagingAndSortingArgumentResolver());
    }

}
