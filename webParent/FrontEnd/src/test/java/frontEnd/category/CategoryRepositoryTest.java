package frontEnd.category;

import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import frontEnd.customer.CustomerRepository;
import frontEnd.product.ProductRepository;
import frontEnd.setting.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)

public class CategoryRepositoryTest {

    @Autowired
    private CustomerRepository customersRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    public void listCategoryChildren() {

        Integer id = 4;
        Category category = categoryRepository.getReferenceById(id);
        Set<Category> getChildren = categoryRepository.getChildren(category);

        for (Category user : getChildren) {
            System.out.println(user.getId() + " --" + user.getName() + "-- "
                    + "--" + user.isEnable());
        }

    }

    @Test
    public void FindAllProduct() {

        Sort sort = Sort.by("name").ascending(); // الترتيب حسب الاسم تصاعديًا

        sort = sort.ascending();

        Pageable pageable = PageRequest.of(1, 20, sort);
        String cat = categoryRepository.findByNameEnabled("Unlocked Cell Phones").getAlias();

        Page<Product> listByCategory = productRepository.findAll(cat, PageRequest.of(0, 10));

        for (Product user : listByCategory) {
            System.out.println(user.getId() + " --" + user.getName() + "-- "
                    + "--" + user.isEnable());

        }
    }

    @Test
    void testFindAllByCategoryAlias() {
        // فرضًا عندك بيانات محمّلة (من data.sql أو import.sql)
        String keyword = "electronics";

        Page<Product> page = productRepository.findAllByProduct(keyword, PageRequest.of(0, 10));

        // شوف النتائج
        System.out.println("Products found: " + page.getTotalElements());
        page.getContent().forEach(p -> System.out.println(p.getName() + " | Cat: " + p.getCategory().getAlias()));

        // Assertion مبدئي
        assertThat(page.getContent()).isNotNull();
    }

    @Test
    public void ListAllCustomer() {

        List<Customer> customerList = customersRepository.findAll();

        customerList.forEach(c -> System.out.println("🔥 test name 🔥" + c.getFullName()));

        assertThat(customerList.size()).isGreaterThan(0);
    }

    @Test
    public void countryTest() {

        List<Country> customerList = countryRepository.findAll();

        customerList.forEach(c -> System.out.println("🔥 test name 🔥" + c.getName()));

        assertThat(customerList.size()).isGreaterThan(0);
    }

    @Test
    public void testListAllCountries() {

        Iterable<Country> listCountries = countryRepository.findAllByOrderByNameAsc();
        System.out.println("🔥 test started 🔥");
        for (Country country : listCountries) {
            System.out.println(" - name   -" + country.getName());
            System.out.println(" - id   -" + country.getId());
            System.out.println(" - code   -" + country.getCode());
            System.out.println("🔥 test started 🔥");
        }

    }
}