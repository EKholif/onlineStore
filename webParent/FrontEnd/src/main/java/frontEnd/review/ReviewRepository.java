package frontEnd.review;

import com.onlineStoreCom.entity.Review.Review;
import com.onlineStoreCom.entity.product.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
	public Page<Review> findByCustomer(Integer customerId, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND ("
			+ "r.headline LIKE %?2% OR r.comment LIKE %?2% OR "
			+ "r.product.name LIKE %?2%)")
	public Page<Review> findByCustomer(Integer customerId, String keyword, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.id = ?2")
	public Review findByCustomerAndId(Integer customerId, Integer reviewId);

    public Page<Review> findByProduct(Product product, Pageable pageable);

    @Query("SELECT COUNT(r.id) FROM Review r WHERE r.customer.id = ?1 AND "
			+ "r.product.id = ?2")
	public Long countByCustomerAndProduct(Integer customerId, Integer productId);

	@Modifying
	@Transactional
    // [AG-TEN-RISK-001] Manual Tenant Check for Native Query
    @Query(nativeQuery = true, value = "UPDATE shop.reviews " +
            "SET votes = COALESCE((SELECT SUM(v.votes) FROM shop.reviews_votes v WHERE v.review_id = ?1), 0) " +
            "WHERE id = ?1 AND tenant_id = ?2" // Security: Enforce Tenant Isolation
    )
    void updateVoteCount(Integer reviewId, Long tenantId);

	@Query("SELECT r.votes FROM Review r WHERE r.id = ?1")
	public Integer getVoteCount(Integer reviewId);
}
