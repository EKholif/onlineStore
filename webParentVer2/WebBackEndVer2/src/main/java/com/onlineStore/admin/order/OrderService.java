package com.onlineStore.admin.order;

import com.onlineStore.admin.setting.country.CountryRepository;
import com.onlineStore.admin.utility.paging.PagingAndSortingHelper;
import com.onlineStoreCom.entity.exception.OrderNotFoundException;
import com.onlineStoreCom.entity.order.Order;
import com.onlineStoreCom.entity.order.OrderStatus;
import com.onlineStoreCom.entity.order.OrderTrack;
import com.onlineStoreCom.entity.setting.state.Country.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class OrderService {
	private static final int ORDERS_PER_PAGE = 4;

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OrderService.class);

	@Autowired
	private OrderRepository orderRepo;
    @Autowired
    private CountryRepository countryRepo;
    @Autowired
    private com.onlineStore.admin.product.service.ProductService productService;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		String sortField = helper.getSortField();
		String sortDir = helper.getSortDir();
		String keyword = helper.getKeyword();

		Sort sort = null;

		if ("destination".equals(sortField)) {
			sort = Sort.by("country").and(Sort.by("state")).and(Sort.by("city"));
		} else {
			sort = Sort.by(sortField);
		}

		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);

		Page<Order> page = null;

		if (keyword != null) {
			page = orderRepo.findAll(keyword, pageable);
		} else {
			page = orderRepo.findAll(pageable);
		}

        helper.updateModelAttributes(pageNum, page);
	}

    public Order get(Integer id) throws OrderNotFoundException {
		try {
			return orderRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}
	}

    public void delete(Integer id) throws OrderNotFoundException {
		Long count = orderRepo.countById(id);
		if (count == null || count == 0) {
            throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}

        orderRepo.deleteById(id);
	}

	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}

	public void save(Order orderInForm) {
		Order orderInDB = orderRepo.findById(orderInForm.getId()).get();
		orderInForm.setOrderTime(orderInDB.getOrderTime());
		orderInForm.setCustomer(orderInDB.getCustomer());

        updateOrderTracks(orderInDB, orderInForm);

		orderRepo.save(orderInForm);
    }

    private void updateOrderTracks(Order orderInDB, Order orderInForm) {
        OrderStatus oldStatus = orderInDB.getStatus();
        OrderStatus newStatus = orderInForm.getStatus();

        if (oldStatus != newStatus) {
            boolean trackExists = false;
            for (OrderTrack track : orderInForm.getOrderTracks()) {
                if (track.getStatus().equals(newStatus)) {
                    trackExists = true;
                    break;
                }
            }

            if (!trackExists) {
                OrderTrack newTrack = new OrderTrack();
                newTrack.setOrder(orderInForm);
                newTrack.setStatus(newStatus);
                newTrack.setUpdatedTime(new Date());
                newTrack.setNotes(newStatus.defaultDescription());

                orderInForm.getOrderTracks().add(newTrack);
            }
        }
    }

    @Autowired
    private org.springframework.context.ApplicationEventPublisher eventPublisher;

	public void updateStatus(Integer orderId, String status) {
		Order orderInDB = orderRepo.findById(orderId).get();
		OrderStatus statusToUpdate = OrderStatus.valueOf(status);

        if (!orderInDB.hasStatus(statusToUpdate)) {
			List<OrderTrack> orderTracks = orderInDB.getOrderTracks();

            OrderTrack track = new OrderTrack();
			track.setOrder(orderInDB);
			track.setStatus(statusToUpdate);
			track.setUpdatedTime(new Date());
			track.setNotes(statusToUpdate.defaultDescription());

            orderTracks.add(track);

            orderInDB.setStatus(statusToUpdate);

            Order savedOrder = orderRepo.save(orderInDB);

            // AG-BILLING-EVENT-001: Trigger Revenue Calculation on Completion
            if (statusToUpdate == OrderStatus.DELIVERED) {
                // Publish event efficiently
                eventPublisher.publishEvent(new com.onlineStore.admin.order.event.OrderCompletedEvent(
                        this,
                        savedOrder,
                        com.onlineStoreCom.tenant.TenantContext.getTenantId()));
            }
		}

    }

    // AG-REFACTOR-ORDER-001: Logic moved from Controller
    public void updateOrderTracks(Order order, String[] trackIds, String[] trackStatuses, String[] trackDates,
                                  String[] trackNotes) {
        if (trackIds == null)
            return;

        List<OrderTrack> orderTracks = order.getOrderTracks();
        java.text.DateFormat dateFormatter = new java.text.SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        for (int i = 0; i < trackIds.length; i++) {

            OrderTrack trackRecord = new OrderTrack();

            int trackId = Integer.parseInt(trackIds[i]);
            if (trackId > 0) {
                trackRecord.setId(trackId);
            }

            trackRecord.setOrder(order);
            trackRecord.setStatus(OrderStatus.valueOf(trackStatuses[i]));
            trackRecord.setNotes(trackNotes[i]);

            try {
                trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
            } catch (java.text.ParseException e) {
                LOGGER.error("Error parsing date: {}", e.getMessage());
            }

            orderTracks.add(trackRecord);
        }
    }

    // AG-REFACTOR-ORDER-002: Logic moved from Controller
    public void updateProductDetails(Order order, String[] detailIds, String[] productIds, String[] productPrices,
                                     String[] productDetailCosts, String[] quantities, String[] productSubtotals, String[] productShipCosts) {
        if (detailIds == null)
            return;

        java.util.Set<com.onlineStoreCom.entity.order.OrderDetail> orderDetails = order.getOrderDetails();

        for (int i = 0; i < detailIds.length; i++) {

            com.onlineStoreCom.entity.order.OrderDetail orderDetail = new com.onlineStoreCom.entity.order.OrderDetail();
            Integer productId = Integer.parseInt(productIds[i]);

            com.onlineStoreCom.entity.product.Product product = productService.findById(productId);

            orderDetail.setProduct(product);

            int detailId = Integer.parseInt(detailIds[i]);

            if (detailId > 0) {
                orderDetail.setId(detailId);
            }
            orderDetail.setProduct(product);
            orderDetail.setOrder(order);
            orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
            orderDetail.setSubtotal(Float.parseFloat(productSubtotals[i]));
            orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
            orderDetail.setQuantity(Integer.parseInt(quantities[i]));
            orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

            orderDetails.add(orderDetail);
        }
    }
}
