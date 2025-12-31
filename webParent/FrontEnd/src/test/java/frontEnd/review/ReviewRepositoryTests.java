package frontEnd.review;

import com.onlineStoreCom.entity.Review.Review;
import com.onlineStoreCom.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static frontEnd.review.vote.VoteResult.fail;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)

public class ReviewRepositoryTests {

	@Autowired
	private ReviewRepository repo;

	@Test
	public void testFindByCustomerNoKeyword() {
		Integer customerId = 163;
		Pageable pageable = PageRequest.of(1, 5);

		Page<Review> page = repo.findByCustomer(customerId, pageable);
		long totalElements = page.getTotalElements();

        assertThat(totalElements).isGreaterThan(1);
	}

	@Test
	public void testFindByCustomerWithKeyword() {
        Integer customerId = 163;
        String keyword = "SanDisk";
		Pageable pageable = PageRequest.of(1, 5);

		Page<Review> page = repo.findByCustomer(customerId, keyword, pageable);
		long totalElements = page.getTotalElements();

        assertThat(totalElements).isGreaterThan(0);
	}

	@Test
	public void testFindByCustomerAndId() {
        Integer customerId = 163;
		Integer reviewId = 4;

        Review review = repo.findByCustomerAndId(customerId, reviewId);
		assertThat(review).isNotNull();
	}

    @Test
	public void testFindByProduct() {
		Product product = new Product(23);
		Pageable pageable = PageRequest.of(0, 3);
		Page<Review> page = repo.findByProduct(product, pageable);

        assertThat(page.getTotalElements()).isGreaterThan(1);

        List<Review> content = page.getContent();
		content.forEach(System.out::println);
	}

    @Test
	public void testCountByCustomerAndProduct() {
        Integer customerId = 163;
		Integer productId = 1;
		Long count = repo.countByCustomerAndProduct(customerId, productId);

        assertThat(count).isEqualTo(1);
	}

	@Test
	public void testUpdateVoteCount() {
        Integer reviewId = 23;
        // [AG-TEN-RISK-001] Provide TenantID for test isolation checks
        repo.updateVoteCount(reviewId, 1L); // Assuming Tenant 1 for tests

        Optional<Review> reviewOpt = repo.findById(reviewId);
        if (reviewOpt.isPresent()) {
            Review review = reviewOpt.get();
            System.out.println("ggggggggg" + review.getVotes());
            assertThat(review.getVotes()).isEqualTo(0);
        } else {
            System.out.println("Review بالـ ID ده مش موجود: " + reviewId);
            fail("Review مش موجود بعد التحديث");
        }
	}

	@Test
	public void testGetVoteCount() {
        Integer reviewId = 23;
		Integer voteCount = repo.getVoteCount(reviewId);

        assertThat(voteCount).isEqualTo(5);
	}
}
