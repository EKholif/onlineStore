package com.onlineStore.admin.order;

import com.onlineStoreCom.entity.customer.Customer;
import com.onlineStoreCom.entity.order.*;
import com.onlineStoreCom.entity.product.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.domain.Pageable.ofSize;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@org.springframework.boot.autoconfigure.domain.EntityScan({"com.onlineStoreCom.entity"})
@Rollback(value = true)
public class OrderRepositoryTests {

	@Autowired
	private OrderRepository repo;
    @Autowired
    private TestEntityManager entityManager;

    @org.junit.jupiter.api.BeforeEach
    public void setUp() {
        com.onlineStoreCom.tenant.TenantContext.setTenantId(1L);
    }

	@Test
	public void testCreateNewOrderWithSingleProduct() {
        Customer customer = entityManager.getEntityManager().createQuery("SELECT c FROM Customer c", Customer.class)
                .setMaxResults(1).getResultList().get(0);
        Product product = entityManager.getEntityManager().createQuery("SELECT p FROM Product p", Product.class)
                .setMaxResults(1).getResultList().get(0);

		Order mainOrder = new Order();
		mainOrder.setOrderTime(new Date());
		mainOrder.setCustomer(customer);
		mainOrder.copyAddressFromCustomer();

		mainOrder.setShippingCost(10);
		mainOrder.setProductCost(product.getCost());
		mainOrder.setTax(0);
		mainOrder.setSubtotal(product.getPrice());
		mainOrder.setTotal(product.getPrice() + 10);

		mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		mainOrder.setStatus(OrderStatus.NEW);
		mainOrder.setDeliverDate(new Date());
		mainOrder.setDeliverDays(1);

		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setProduct(product);
		orderDetail.setOrder(mainOrder);
		orderDetail.setProductCost(product.getCost());
		orderDetail.setShippingCost(10);
		orderDetail.setQuantity(1);
		orderDetail.setSubtotal(product.getPrice());
		orderDetail.setUnitPrice(product.getPrice());

		mainOrder.getOrderDetails().add(orderDetail);

		Order savedOrder = repo.save(mainOrder);

        assertThat(savedOrder.getId()).isGreaterThan(0);
	}

	@Test
	public void testCreateNewOrderWithMultipleProducts() {
        Customer customer = entityManager.getEntityManager().createQuery("SELECT c FROM Customer c", Customer.class)
                .setMaxResults(1).getResultList().get(0);
        Product product1 = entityManager.getEntityManager().createQuery("SELECT p FROM Product p", Product.class)
                .setMaxResults(2).getResultList().get(0);
        Product product2 = entityManager.getEntityManager().createQuery("SELECT p FROM Product p", Product.class)
                .setMaxResults(2).getResultList().get(1);

		Order mainOrder = new Order();
		mainOrder.setOrderTime(new Date());
		mainOrder.setCustomer(customer);
		mainOrder.copyAddressFromCustomer();

		OrderDetail orderDetail1 = new OrderDetail();
		orderDetail1.setProduct(product1);
		orderDetail1.setOrder(mainOrder);
		orderDetail1.setProductCost(product1.getCost());
		orderDetail1.setShippingCost(10);
		orderDetail1.setQuantity(1);
		orderDetail1.setSubtotal(product1.getPrice());
		orderDetail1.setUnitPrice(product1.getPrice());

		OrderDetail orderDetail2 = new OrderDetail();
		orderDetail2.setProduct(product2);
		orderDetail2.setOrder(mainOrder);
		orderDetail2.setProductCost(product2.getCost());
		orderDetail2.setShippingCost(20);
		orderDetail2.setQuantity(2);
		orderDetail2.setSubtotal(product2.getPrice() * 2);
		orderDetail2.setUnitPrice(product2.getPrice());

		mainOrder.getOrderDetails().add(orderDetail1);
		mainOrder.getOrderDetails().add(orderDetail2);

		mainOrder.setShippingCost(30);
		mainOrder.setProductCost(product1.getCost() + product2.getCost());
		mainOrder.setTax(0);
		float subtotal = product1.getPrice() + product2.getPrice() * 2;
		mainOrder.setSubtotal(subtotal);
		mainOrder.setTotal(subtotal + 30);

		mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		mainOrder.setStatus(OrderStatus.PACKAGED);
		mainOrder.setDeliverDate(new Date());
		mainOrder.setDeliverDays(3);

        Order savedOrder = repo.save(mainOrder);
        assertThat(savedOrder.getId()).isGreaterThan(0);
	}

	@Test
	public void testListOrders() {
        Iterable<Order> orders = repo.findAllSet("Brian");

		assertThat(orders).hasSizeGreaterThan(0);

		orders.forEach(System.out::println);
	}

	@Test
	public void testListPage() {

        Pageable pageable = ofSize(10);
        Iterable<Order> orders = repo.findAll(pageable);

		assertThat(orders).hasSizeGreaterThan(0);

		orders.forEach(System.out::println);
	}

	@Test
	public void testUpdateOrder() {
        Order order = repo.findAll(Pageable.ofSize(1)).getContent().get(0);

		order.setStatus(OrderStatus.SHIPPING);
		order.setPaymentMethod(PaymentMethod.COD);
		order.setOrderTime(new Date());
		order.setDeliverDays(2);

        Order updatedOrder = repo.save(order);

		System.out.println(updatedOrder.getOrderDetails());
		assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.SHIPPING);
	}

    @Test
	public void testGetOrder() {
        Order order = repo.findAll(Pageable.ofSize(1)).getContent().get(0);

		assertThat(order).isNotNull();
		System.out.println(order);
	}

    @Test
	public void testDeleteOrder() {
        Order order = repo.findAll(Pageable.ofSize(1)).getContent().get(0);
        Integer orderId = order.getId();
		repo.deleteById(orderId);

        Optional<Order> result = repo.findById(orderId);
		assertThat(result).isNotPresent();
	}

    @Test
	public void testUpdateOrderTracks() {
        Order order = repo.findAll(Pageable.ofSize(1)).getContent().get(0);

		OrderTrack newTrack = new OrderTrack();
		newTrack.setOrder(order);
		newTrack.setUpdatedTime(new Date());
		newTrack.setStatus(OrderStatus.NEW);
		newTrack.setNotes(OrderStatus.NEW.defaultDescription());

		OrderTrack processingTrack = new OrderTrack();
		processingTrack.setOrder(order);
		processingTrack.setUpdatedTime(new Date());
		processingTrack.setStatus(OrderStatus.PROCESSING);
		processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
		orderTracks.add(newTrack);
		orderTracks.add(processingTrack);

        Order updatedOrder = repo.save(order);

        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
	}

    @Test
	public void testAddTrackWithStatusNewToOrder() {
        Order order = repo.findAll(Pageable.ofSize(1)).getContent().get(0);

		OrderTrack newTrack = new OrderTrack();
		newTrack.setOrder(order);
		newTrack.setUpdatedTime(new Date());
		newTrack.setStatus(OrderStatus.NEW);
		newTrack.setNotes(OrderStatus.NEW.defaultDescription());

        List<OrderTrack> orderTracks = order.getOrderTracks();
        orderTracks.add(newTrack);

		Order updatedOrder = repo.save(order);

        assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
	}

	@Test
	public void testAddTratatusNewToOrder() {
        Order order = repo.findAll(Pageable.ofSize(1)).getContent().get(0);

		List<OrderTrack> orderTracks = order.getOrderTracks();

        for (OrderTrack r : orderTracks) {
            System.out.println("OrderTrack     " + r.getStatus());

		}

        // assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
	}

	@Test
	public void testFindByOrderTimeBetween() throws ParseException {
        // Create an order for today ensuring data exists
        testCreateNewOrderWithSingleProduct();

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date today = new Date();
        // Set range to include today (e.g., start of month to end of month, or just
        // generous range)
        Date startTime = new Date(today.getTime() - (1000L * 60 * 60 * 24 * 30)); // 30 days ago
        Date endTime = new Date(today.getTime() + (1000L * 60 * 60 * 24)); // Tomorrow

		List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);

        assertThat(listOrders.size()).isGreaterThan(0);

        for (Order order : listOrders) {
            System.out.printf("%s | %s | %.2f | %.2f | %.2f \n",
                    order.getId(), order.getOrderTime(), order.getProductCost(),
					order.getSubtotal(), order.getTotal());
		}
	}
}
