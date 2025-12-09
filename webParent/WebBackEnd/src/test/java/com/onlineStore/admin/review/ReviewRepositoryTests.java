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
@Rollback(false)
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository repo;
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void testCreateReview() {
        Integer productId = 5;
        Product product = new Product(productId);

        System.out.println(" productId    : " + product.getId());

        Integer customerId = 5;
        Customer customer = new Customer(customerId);

        System.out.println(" customerId    : " + customer.getId());

        Review review = new Review();
        review.setTenantId(0L);
        review.setProduct(product);
        review.setCustomer(customer);
        review.setReviewTime(new Date());
        review.setHeadline("Perfect for my needs. Loving it!");
        review.setComment("Nice to have: wireless remote, iOS app, GPS...");
        review.setRating(5);

        Review savedReview = repo.save(review);

        assertThat(savedReview).isNotNull();
        assertThat(savedReview.getId()).isGreaterThan(0);
    }

    @Test
    public void testListReviews() {
        List<Review> listReviews = repo.findAll();

        assertThat(listReviews.size()).isGreaterThan(0);

        listReviews.forEach(System.out::println);
    }

    @Test
    public void testGetReview() {
        Integer id = 3;
        Review review = repo.findById(id).get();

        assertThat(review).isNotNull();

        System.out.println(review);
    }

    @Test
    public void testUpdateReview() {
        Integer id = 3;
        String headline = "An awesome camera at an awesome price";
        String comment = "Overall great camera and is highly capable...";

        Review review = repo.findById(id).get();
        review.setHeadline(headline);
        review.setComment(comment);

        Review updatedReview = repo.save(review);

        assertThat(updatedReview.getHeadline()).isEqualTo(headline);
        assertThat(updatedReview.getComment()).isEqualTo(comment);
    }

    @Test
    public void testDeleteReview() {
        Integer id = 3;
        repo.deleteById(id);

        Optional<Review> findById = repo.findById(id);

        assertThat(findById).isNotPresent();
    }
}
