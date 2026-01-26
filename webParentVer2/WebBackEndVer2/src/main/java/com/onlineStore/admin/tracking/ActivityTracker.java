package com.onlineStore.admin.tracking;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class ActivityTracker {

    private final AtomicInteger activeVisitors = new AtomicInteger(0);
    private final AtomicInteger activeUsers = new AtomicInteger(0);
    private final AtomicInteger activeCustomers = new AtomicInteger(0);

    public void incrementVisitor() {
        activeVisitors.incrementAndGet();
    }

    public void decrementVisitor() {
        activeVisitors.decrementAndGet();
    }

    public int getActiveVisitorCount() {
        return activeVisitors.get();
    }

    public void incrementUser() {
        activeUsers.incrementAndGet();
    }

    public void decrementUser() {
        activeUsers.decrementAndGet();
    }

    public int getActiveUserCount() {
        return activeUsers.get();
    }
    
    public void incrementCustomer() {
        activeCustomers.incrementAndGet();
    }

    public void decrementCustomer() {
        activeCustomers.decrementAndGet();
    }

    public int getActiveCustomerCount() {
        return activeCustomers.get();
    }
}
