package com.onlineStore.admin.order;

import java.util.Date;
import java.util.List;

import com.onlineStoreCom.entity.order.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
	
	@Query("SELECT NEW OrderDetail(d.product.category.name, d.quantity,"
			+ " d.productCost, d.shippingCost, d.subtotal)"
			+ " FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
	public List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);
	
	@Query("SELECT NEW OrderDetail(d.quantity, d.product.name,"
			+ " d.productCost, d.shippingCost, d.subtotal)"
			+ " FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
	public List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);	
}
