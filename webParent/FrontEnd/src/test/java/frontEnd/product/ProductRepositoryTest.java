package frontEnd.product;

import com.onlineStoreCom.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository repo;

    @Test
    public void testFindOnSale() {
        // 1. Create a product with discount
        Product p = new Product();
        p.setName("Test Discount Product " + new Date().getTime());
        p.setAlias("test-discount-" + new Date().getTime());
        p.setShortDescription("Short");
        p.setFullDescription("Full");
        p.setCreatedTime(new Date());
        p.setUpdatedTime(new Date());
        p.setEnabled(true);
        p.setInStock(true);
        p.setPrice(100);
        p.setDiscountPercent(10.0f); // 10% off
        p.setMainImage("test.png");
        p.setTenantId(0l);
        repo.save(p);

        // 2. Query
        List<Product> onSale = repo.findAllOnSale();

        // 3. Assert
        assertThat(onSale).isNotEmpty();
        boolean found = false;
        for (Product product : onSale) {
            System.out.println("Found On Sale: " + product.getName() + " - " + product.getDiscountPercent() + "%");
            if (product.getId().equals(p.getId())) {
                found = true;
            }
        }
        assertThat(found).isTrue();
    }

    @Test
    public void testFindOnSle() {

        List<Product> onSale = repo.findAllOnSale();

        assertThat(onSale).isNotEmpty();
        for (Product product : onSale) {
            System.out.println("Found On Sale: " + product.getName() + " - " + product.getDiscountPercent() + "%");
        }

    }

}
