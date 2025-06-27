package com.onlineStore.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        addResourceHandler(registry, "categories-photos");
        addResourceHandler(registry, "customers-photos");

        addResourceHandler(registry, "user-photos");
        addResourceHandler(registry, "brands-photos");
        addResourceHandler(registry, "products-photos");
        addResourceHandler(registry, "pdf-convert");
        addResourceHandler(registry, "site-logo");
    }


    private void addResourceHandler(ResourceHandlerRegistry registry, String pathPattern) {

        Path path = Paths.get(pathPattern);
        String absolutePath = path.toFile().getAbsolutePath();

        registry
                .addResourceHandler("/" + pathPattern + "/**")
                .addResourceLocations("file:/" + absolutePath + "/");

        System.out.println(absolutePath);

    }

}
