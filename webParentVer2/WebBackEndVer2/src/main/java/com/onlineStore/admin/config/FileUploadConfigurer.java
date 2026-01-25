package com.onlineStore.admin.config;

import com.onlineStore.admin.utility.FileUploadUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileUploadConfigurer {

    @Value("${app.storage.tenants-path:tenants}")
    private String tenantsPath;

    @PostConstruct
    public void init() {
        FileUploadUtil.setTenantsBasePath(tenantsPath);
    }
}
