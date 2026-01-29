package frontEnd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication(scanBasePackages = { "frontEnd", "com.onlineStoreCom" })
@EntityScan({ "com.onlineStoreCom.entity" })
@org.springframework.data.jpa.repository.config.EnableJpaRepositories({"frontEnd", "com.onlineStoreCom.repo",
        "com.onlineStoreCom.analytics"})
public class FrontEndApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrontEndApplication.class, args);
    }

}
