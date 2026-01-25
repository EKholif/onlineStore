package frontEnd.runners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseRepairRunner implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Drop the global unique constraint on email causing issues
            // This is required to allow the same email in different tenants (Composite Unique Key takes over)
            jdbcTemplate.execute("ALTER TABLE customers DROP INDEX UK_rfbvkrffamfql7cjmen8v976v");
            System.out.println("FIX: Successfully dropped global unique email constraint 'UK_rfbvkrffamfql7cjmen8v976v'");
        } catch (Exception e) {
            // Index might not exist or already dropped
            System.out.println("FIX INFO: Could not drop index 'UK_rfbvkrffamfql7cjmen8v976v' (it might not exist): " + e.getMessage());
        }
    }
}
