package com.onlineStoreCom.entity.order;

/**
 * Represents the various states an order can be in during its lifecycle.
 * <p>
 * This enum defines all possible statuses that an order can have, from the moment
 * it is placed until it is either completed or returned. Each status represents
 * a specific stage in the order fulfillment process.
 *
 * <p><b>Order Status Flow:</b>
 * <ol>
 *   <li>NEW - Initial state when order is first created</li>
 *   <li>PROCESSING - Order is being prepared for shipment</li>
 *   <li>PACKAGED - Items have been packaged</li>
 *   <li>PICKED - Order has been picked up by the carrier</li>
 *   <li>SHIPPING - Order is in transit</li>
 *   <li>DELIVERED - Order has been delivered to the customer</li>
 *   <li>RETURN_REQUESTED - Customer has requested a return</li>
 *   <li>RETURNED - Order has been returned by the customer</li>
 *   <li>REFUNDED - Refund has been processed</li>
 *   <li>CANCELLED - Order was cancelled</li>
 * </ol>
 *
 * <p>This enum is used throughout the system to track order progress and trigger
 * appropriate business logic at each stage.
 */
public enum OrderStatus {
    /**
     * Newly created order, awaiting processing.
     * This is the initial state for all orders.
     */
    NEW {
        @Override
        public String defaultDescription() {
            return "Order was placed by the customer";
        }
    },
    
    /**
     * Order is being processed and prepared for shipment.
     * Inventory is being allocated at this stage.
     */
    PROCESSING {
        @Override
        public String defaultDescription() {
            return "Order is being processed";
        }
    },
    
    /**
     * All items have been packaged and are ready for pickup.
     * The order is waiting to be picked up by the shipping carrier.
     */
    PACKAGED {
        @Override
        public String defaultDescription() {
            return "Products were packaged";
        }        
    },
    
    /**
     * Order has been picked up by the shipping carrier.
     * The order is now in the carrier's possession.
     */
    PICKED {
        @Override
        public String defaultDescription() {
            return "Shipper picked the package";
        }        
    },
    
    /**
     * Order is in transit to the customer.
     * The carrier is currently delivering the order.
     */
    SHIPPING {
        @Override
        public String defaultDescription() {
            return "Shipper is delivering the package";
        }        
    },
    
    /**
     * Order has been successfully delivered to the customer.
     * This is a terminal state for successful orders.
     */
    DELIVERED {
        @Override
        public String defaultDescription() {
            return "Customer received products";
        }        
    },
    
    /**
     * Customer has initiated a return request.
     * The return is pending approval or processing.
     */
    RETURN_REQUESTED {
        @Override
        public String defaultDescription() {
            return "Customer sent request to return purchase";
        }        
    },
    
    /**
     * Order has been returned by the customer.
     * The returned items are being inspected.
     */
    RETURNED {
        @Override
        public String defaultDescription() {
            return "Products were returned";
        }        
    },
    
    /**
     * Refund has been processed for the order.
     * This is a terminal state for returned orders.
     */
    REFUNDED {
        @Override
        public String defaultDescription() {
            return "Customer has been refunded";
        }        
    },
    
    /**
     * Order was cancelled before fulfillment.
     * This is a terminal state for cancelled orders.
     */
    CANCELLED {
        @Override
        public String defaultDescription() {
            return "Order was rejected";
        }
    },
    
    /**
     * Customer has paid this order.
     */
    PAID {
        @Override
        public String defaultDescription() {
            return "Customer has paid this order";
        }        
    };
    
    public abstract String defaultDescription();
}
