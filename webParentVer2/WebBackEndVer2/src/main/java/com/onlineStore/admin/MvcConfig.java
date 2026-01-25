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

        // AG-ASSET-PATH-BACKEND-SERVE: Serve tenant assets from filesystem
        // Maps URL: /tenants/** → Filesystem: {configured-path}/tenants/
        // Example: /tenants/4/assets/categories/1/electronics.png

        Path resolvedPath = Paths.get(tenantsPath).toAbsolutePath().normalize();
        String absolutePath = resolvedPath.toUri().toString();

        // Ensure trailing slash for directory
        if (!absolutePath.endsWith("/")) {
            absolutePath += "/";
        }

        System.out.println("🗂️  AG-BACKEND-ASSET: Mapping /tenants/** to filesystem");
        System.out.println("   Working Dir: " + System.getProperty("user.dir"));
        System.out.println("   Config Path: " + tenantsPath);
        System.out.println("   Resolved: " + resolvedPath);
        System.out.println("   URI: " + absolutePath);

        registry.addResourceHandler("/tenants/**")
                .addResourceLocations(absolutePath);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new PagingAndSortingArgumentResolver());
        resolvers.add(new com.onlineStore.admin.article.paging.PagingAndSortingArgumentResolver());
    }

}
