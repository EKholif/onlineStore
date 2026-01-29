package com.onlineStore.admin.tracking;


import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tracks real-time activity of Visitors, Users, and Customers.
 */
@Component
public class ActivityTracker {

    // Tracks anonymous visitors (Session count)
    private final AtomicInteger activeVisitors = new AtomicInteger(0);

    // Map<UserId, LastHeartbeat>
    private final Map<Integer, Long> activeUsers = new ConcurrentHashMap<>();

    // Map<CustomerId, LastHeartbeat>
    private final Map<Integer, Long> activeCustomers = new ConcurrentHashMap<>();

    public void visitorConnected() {
        activeVisitors.incrementAndGet();
    }

    public void visitorDisconnected() {
        activeVisitors.decrementAndGet();
    }

    public int getActiveVisitorCount() {
        return activeVisitors.get();
    }

    public void userLoggedIn(Integer userId) {
        activeUsers.put(userId, System.currentTimeMillis());
    }

    public void userLoggedOut(Integer userId) {
        activeUsers.remove(userId);
    }

    public int getActiveUserCount() {
        // Optional: Clean up stale sessions older than 30 mins
        long thirtyMinsAgo = System.currentTimeMillis() - (30 * 60 * 1000);
        activeUsers.entrySet().removeIf(entry -> entry.getValue() < thirtyMinsAgo);

        return activeUsers.size();
    }

    public void customerLoggedIn(Integer customerId) {
        activeCustomers.put(customerId, System.currentTimeMillis());
    }

    public int getActiveCustomerCount() {
        long thirtyMinsAgo = System.currentTimeMillis() - (30 * 60 * 1000);
        activeCustomers.entrySet().removeIf(entry -> entry.getValue() < thirtyMinsAgo);

        return activeCustomers.size();
    }
}
