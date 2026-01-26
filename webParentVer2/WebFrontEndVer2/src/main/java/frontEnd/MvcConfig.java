package frontEnd;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @org.springframework.beans.factory.annotation.Autowired
    private frontEnd.setting.ThemeGuardInterceptor themeGuardInterceptor;

    @org.springframework.beans.factory.annotation.Value("${app.storage.tenants-path}")
    private String backendTenantsPath;

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
        // WHY: tenants/ directory is in Backend (webParentVer2/WebBackEndVer2/tenants),
        // not Frontend
        // BUSINESS IMPACT: Enables multi-tenant asset serving across Frontend/Backend
        // boundary
        // Expected structure:
        // webParentVer2/WebBackEndVer2/tenants/{tenantId}/assets/{type}/{id}/

        String workingDir = System.getProperty("user.dir");
        Path tenantsPath = Paths.get(workingDir, backendTenantsPath).toAbsolutePath().normalize();

        // Validate path exists
        if (!Files.exists(tenantsPath)) {
            System.err.println("⚠️  WARNING: Backend tenants directory not found at: " + tenantsPath);
            System.err.println("⚠️  Assets may not be served correctly!");
        }

        String tenantsUri = tenantsPath.toUri().toString();

        // Ensure trailing slash
        if (!tenantsUri.endsWith("/")) {
            tenantsUri += "/";
        }

        System.out.println("AG-FRONTEND-ASSET: Working Dir: " + workingDir);
        System.out.println("AG-FRONTEND-ASSET: Tenants Resolved Path: " + tenantsPath);
        System.out.println("AG-FRONTEND-ASSET: Tenants Path Exists: " + Files.exists(tenantsPath));
        System.out.println("AG-FRONTEND-ASSET: Mapping /tenants/** to " + tenantsUri);

        registry
                .addResourceHandler("/tenants/**")
                .addResourceLocations(tenantsUri);
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
