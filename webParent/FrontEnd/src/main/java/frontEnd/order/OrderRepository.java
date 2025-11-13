package frontEnd.order;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Integer> {

	@Query(value = "SELECT DISTINCT o FROM Order o JOIN o.orderDetails od JOIN od.product p "
			+ "WHERE o.customer.id = ?2 AND (p.name LIKE %?1% OR str(o.status) LIKE %?1%)",
			countQuery = "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.orderDetails od JOIN od.product p "
					+ "WHERE o.customer.id = ?2 AND (p.name LIKE %?1% OR str(o.status) LIKE %?1%)")
	Page<Order> findAll(String keyword, Integer customerId, Pageable pageable);

	
	@Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
	public Page<Order> findAll(Integer customerId, Pageable pageable);
	
	public Order findByIdAndCustomer(Integer id, Customer customer);
}
