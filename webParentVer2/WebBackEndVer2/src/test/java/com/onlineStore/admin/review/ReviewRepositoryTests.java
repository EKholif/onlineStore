package com.onlineStore.admin.review;

import com.onlineStore.admin.product.repository.ProductRepository;
import com.onlineStoreCom.entity.Review.Review;
import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(true)
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository repo;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager entityManager;

    private Review createAndSaveReview() {
        Integer productId = 5;
        // Persist product and customer first
        Product product = new Product();
        product.setName("Test Product Review " + System.currentTimeMillis());
        product.setAlias("test-product-review-" + System.currentTimeMillis());
        product.setShortDescription("Short Desc");
        product.setFullDescription("Full Desc");
        product.setCreatedTime(new Date());
        product.setUpdatedTime(new Date());
        product.setPrice(100.0f);
        product.setCost(80.0f);
        product.setEnabled(true);
        product.setInStock(true);
        product.setMainImage("main.png");
        product.setTenantId(0L);

        product = productRepository.save(product);

        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john.doe.review" + System.currentTimeMillis() + "@test.com");
        customer.setPassword("password");
        customer.setPhoneNumber("1234567890");
        customer.setCreatedTime(new Date());
        com.onlineStoreCom.entity.setting.state.Country.Country country = new com.onlineStoreCom.entity.setting.state.Country.Country();
        country.setName("Test Country " + System.currentTimeMillis());
        country.setCode("TC" + (System.currentTimeMillis() % 100));
        // country.setTenantId(1L); // Removed as Country is shared
        country = entityManager.persist(country);

        customer.setTenantId(0L);
        customer.setCity("City");
        customer.setAddressLine1("Address");
        customer.setState("New York");
        customer.setPostalCode("12345");
        customer.setCountry(country); // 1 exists
        customer = entityManager.persist(customer);

        System.out.println(" customerId    : " + customer.getId());

        Review review = new Review();
        review.setTenantId(0L);
        review.setProduct(product);
        review.setCustomer(customer);
        review.setReviewTime(new Date());
        review.setHeadline("Perfect for my needs. Loving it!");
        review.setComment("Nice to have: wireless remote, iOS app, GPS...");
        review.setRating(5);

        return repo.save(review);
    }

    @Test
    public void testCreateReview() {
        Review savedReview = createAndSaveReview();
        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isGreaterThan(0);
    }

    @Test
    public void testListReviews() {
        createAndSaveReview();
        List<Review> listReviews = repo.findAll();

        assertThat(listReviews.size()).isGreaterThan(0);

        listReviews.forEach(System.out::println);
    }

    @Test
    public void testGetReview() {
        createAndSaveReview();
        List<Review> listReviews = repo.findAll();
        if (!listReviews.isEmpty()) {
            Review review = listReviews.get(0);
            assertThat(review).isNotNull();
            System.out.println(review);
        }
    }

    @Test
    public void testUpdateReview() {
        Review r = createAndSaveReview();

        String headline = "An awesome camera at an awesome price";
        String comment = "Overall great camera and is highly capable...";

        r.setHeadline(headline);
        r.setComment(comment);

        Review updatedReview = repo.save(r);

        assertThat(updatedReview.getHeadline()).isEqualTo(headline);
        assertThat(updatedReview.getComment()).isEqualTo(comment);
    }

    @Test
    public void testDeleteReview() {
        Review r = createAndSaveReview();
        Integer id = r.getId();

        repo.deleteById(id);

        Optional<Review> findById = repo.findById(id);

        assertThat(findById).isNotPresent();
    }
}
