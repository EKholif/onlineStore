package frontEnd.order;


import com.onlineStoreCom.entity.order.OrderDetail;
import com.onlineStoreCom.entity.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderDetailRepositoryTests {

	@Autowired
	private OrderDetailRepository repo;
	
	@Test
	public void testCountByProductAndCustomerAndOrderStatus() {
		Integer productId = 1;
		Integer customerId = 163;
		
		Long count = repo.countByProductAndCustomerAndOrderStatus(productId, customerId, OrderStatus.NEW);
		assertThat(count).isGreaterThan(0);
	}

	@Test
	public void finfAll(){

		List<OrderDetail> list = repo.findAll();

         list.forEach(c -> System.out.println( "🔥 test name 🔥" + c.getProduct().getName() +"🔥 test Id 🔥" + c.getProduct().getId()));


	}

}
