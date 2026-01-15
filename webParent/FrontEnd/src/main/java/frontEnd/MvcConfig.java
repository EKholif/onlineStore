package frontEnd;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @org.springframework.beans.factory.annotation.Autowired
    private frontEnd.setting.ThemeGuardInterceptor themeGuardInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        addResourceHandler(registry, "categories-photos");
        addResourceHandler(registry, "customers-photos");

        addResourceHandler(registry, "user-photos");
        addResourceHandler(registry, "brands-photos");
        addResourceHandler(registry, "products-photos");
        addResourceHandler(registry, "site-logo");
        addResourceHandler(registry, "pdf-convert");

        // [AG-ASSET-PATH-006] Serve tenant-specific assets from Backend's centralized
        // location
        // WHY: tenants/ directory is in Backend (webParent/WebBackEnd/tenants), not
        // Frontend
        // BUSINESS IMPACT: Enables multi-tenant asset serving across Frontend/Backend
        // boundary
        Path tenantsPath = Paths.get("../WebBackEnd/tenants").toAbsolutePath().normalize();
        registry
                .addResourceHandler("/tenants/**")
                .addResourceLocations("file:/" + tenantsPath.toString().replace("\\", "/") + "/");
    }

    private void addResourceHandler(ResourceHandlerRegistry registry, String pathPattern) {

        Path path = Paths.get(pathPattern);
        String absolutePath = path.toFile().getAbsolutePath();

        registry
                .addResourceHandler("/" + pathPattern + "/**")
                .addResourceLocations("file:/" + absolutePath + "/");

    }

    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        // [AG-FE-THEME-003] Register Theme Guard
        registry.addInterceptor(themeGuardInterceptor);
    }

}
