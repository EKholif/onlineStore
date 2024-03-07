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
        String dirBrand = "brand-photos";

        Path userPhotoDir = Paths.get(dirName);
        Path categoryPhotoDir = Paths.get(dirCategoryName);
        Path brandPhotoDir = Paths.get(dirBrand);


        String userPhotoPath =userPhotoDir.toFile().getAbsolutePath();
        String categoryPhotoPath =categoryPhotoDir.toFile().getAbsolutePath();
        String brandPhotoPath =brandPhotoDir.toFile().getAbsolutePath();


        registry.addResourceHandler("/" + dirName + "/**").addResourceLocations("file:/" + userPhotoPath + "/");
        registry.addResourceHandler("/" + dirCategoryName + "/**").addResourceLocations("file:/" + categoryPhotoPath + "/");
        registry.addResourceHandler("/" + dirBrand + "/**").addResourceLocations("file:/" + brandPhotoPath + "/");

    }




}
