package frontEnd.shoppingCart;


import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.shoppingCart.CartItem;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = true)

public class CartItemRepositoryTests {


    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private EntityManager entityManager;

//    @Test
//    public void ListAllCustomer() {
//
//        List<Customer> customerList = customersRepository.findAll();
//
//
//        customerList.forEach(c -> System.out.println( "🔥 test name 🔥" + c.getFullName()));
//
//        assertThat(customerList.size()).isGreaterThan(0);
//    }


    @Test
    public void testSaveItem() {
        Integer customerId = 163;
        Integer productId = 67;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setProduct(product);
        newItem.setQuantity(1);

        CartItem savedItem = cartItemRepository.save(newItem);

        assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testSave2Items() {
        Integer customerId = 2;
        Integer productId1 = 10;
        Integer productId2 = 8;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product1 = entityManager.find(Product.class, productId1);
        Product product2 = entityManager.find(Product.class, productId2);

        CartItem item1 = new CartItem();
        item1.setCustomer(customer);
        item1.setProduct(product1);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setCustomer(customer); // إعادة استخدام نفس الـ Customer
        item2.setProduct(product2);  // منتج جديد لكنه محمّل بشكل صحيح
        item2.setQuantity(3);

        Iterable<CartItem> iterable = cartItemRepository.saveAllAndFlush(List.of(item1, item2));

        Assertions.assertThat(iterable).size().isGreaterThan(0);
    }

    @Test
    public void testFindByCustomer() {
        Integer customerId = 2;

        Customer customer = entityManager.find(Customer.class, customerId);

        List<CartItem> listItems = cartItemRepository.findByCustomer(customer);

        listItems.forEach(System.out::println);

        Assertions.assertThat(listItems.size()).isEqualTo(4);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        Integer customerId = 163;
        Integer productId = 67;
        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);
        CartItem item = cartItemRepository.findByCustomerAndProduct(customer, product);
        assertThat(item).isNotNull();

        System.out.println(item);
    }

    @Test
    public void testUpdateQuantity() {
        Integer customerId = 2;
        Integer productId = 8;
        Integer quantity = 4;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        Integer cart = cartItemRepository.updateQuantity(quantity, customer, productId);

        CartItem item = cartItemRepository.findByCustomerAndProduct(customer, product);


        assertThat(item.getQuantity()).isEqualTo(4);
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        Integer customerId = 2;
        Integer productId = 10;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);


        Integer cart = cartItemRepository.deleteByCustomerAndProduct(customerId, productId);

        CartItem item = cartItemRepository.findByCustomerAndProduct(customer, product);

        System.out.println("\uD83D\uDD25\uD83D\uDD25" + cart);

        assertThat(item).isNull();
    }
}