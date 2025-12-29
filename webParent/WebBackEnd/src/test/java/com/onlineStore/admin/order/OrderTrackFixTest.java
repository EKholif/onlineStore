package com.onlineStore.admin.order;

import com.onlineStoreCom.entity.order.Order;
import com.onlineStoreCom.entity.order.OrderStatus;
import com.onlineStoreCom.entity.order.OrderTrack;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Date;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderTrackFixTest {

    @Autowired
    private OrderRepository repo;

    @Test
    public void fixMissingTracks() {
        List<Order> listOrders = repo.findAll();

        for (Order order : listOrders) {
            OrderStatus currentStatus = order.getStatus();
            boolean trackFound = false;

            for (OrderTrack track : order.getOrderTracks()) {
                if (track.getStatus().equals(currentStatus)) {
                    trackFound = true;
                    break;
                }
            }

            if (!trackFound) {
                System.out.println("Missing track for Order ID: " + order.getId() + ", Status: " + currentStatus);

                OrderTrack newTrack = new OrderTrack();
                newTrack.setTenantId(order.getTenantId());
                newTrack.setOrder(order);
                newTrack.setStatus(currentStatus);
                newTrack.setUpdatedTime(new Date());
                newTrack.setNotes("Auto-fix: Added missing track for status " + currentStatus);

                order.getOrderTracks().add(newTrack);
                repo.save(order);

                System.out.println("Fixed Order ID: " + order.getId());
            }
        }
    }
}
