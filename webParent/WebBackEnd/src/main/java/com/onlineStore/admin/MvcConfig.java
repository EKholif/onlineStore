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

        String dirCategoryName = "categories-photos";
        String dirName = "user-photos";

        Path userPhotoDir = Paths.get(dirName);
        Path CategoryPhotoDir = Paths.get(dirCategoryName);

        String userPhotoPath =userPhotoDir.toFile().getAbsolutePath();
        String categoryPhotoPath =CategoryPhotoDir.toFile().getAbsolutePath();


        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotoPath + "/");
        registry.addResourceHandler("/" + dirCategoryName + "/**").addResourceLocations("file:/" + categoryPhotoPath + "/");

    }




}
