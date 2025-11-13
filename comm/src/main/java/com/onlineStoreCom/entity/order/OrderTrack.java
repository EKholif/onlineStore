package com.onlineStoreCom.entity.order;

import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Tracks the status changes and updates for an order throughout its lifecycle.
 * <p>
 * Each instance of this class represents a single status update for an order,
 * including when the status changed, who made the change, and any relevant notes.
 * This creates an audit trail of all order activities.
 *
 * <p><b>Key Features:</b>
 * <ul>
 *   <li>Maintains a complete history of order status changes</li>
 *   <li>Supports adding notes for each status update</li>
 *   <li>Tracks the exact timestamp of each status change</li>
 *   <li>Links back to the parent order</li>
 * </ul>
 *
 * @see Order
 * @see OrderStatus
 */
@Entity
@Table(name = "order_track")
public class OrderTrack extends IdBasedEntity {

    /**
     * Additional notes or comments about this status update.
     * Can include details about the reason for the status change,
     * delivery attempts, or other relevant information.
     */
    @Column(length = 256)
    private String notes;
    
    /**
     * The exact date and time when this status was applied.
     * Automatically set when the status is updated.
     */
    private Date updatedTime;
    
    /**
     * The new status that was applied to the order.
     * Represents the state of the order after this update.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 45, nullable = false)
    private OrderStatus status;
    
    /**
     * The order that this tracking entry belongs to.
     * Many tracking entries can belong to a single order.
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * Gets the notes associated with this status update.
     *
     * @return the notes, or null if none were provided
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes for this status update.
     *
     * @param notes the notes to set (max 256 characters)
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Gets the timestamp when this status was applied.
     *
     * @return the update timestamp
     */
    public Date getUpdatedTime() {
        return updatedTime;
    }

    /**
     * Sets the timestamp for this status update.
     * Typically called automatically by the system.
     *
     * @param updatedTime the timestamp to set
     */
    public void setUpdatedTime(Date updatedTime) {
        this.updatedTime = updatedTime;
    }

    /**
     * Gets the status that was applied in this update.
     *
     * @return the order status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Sets the status for this update.
     *
     * @param status the status to set
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * Gets the order that this tracking entry belongs to.
     *
     * @return the parent order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the order that this tracking entry belongs to.
     *
     * @param order the parent order to set
     */
    public void setOrder(Order order) {
        this.order = order;
    }
    
    /**
     * Gets the formatted updated time for form display.
     * The time is formatted according to the ISO 8601 standard.
     *
     * @return the formatted date-time string, or null if not set
     */
    @Transient
    public String getUpdatedTimeOnForm() {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        return updatedTime != null ? dateFormatter.format(updatedTime) : null;
    }
    
    /**
     * Parses a date-time string and sets it as the updated time.
     * The string should be in the format "yyyy-MM-dd'T'HH:mm:ss".
     *
     * @param dateString the date-time string to parse
     * @throws ParseException if the string cannot be parsed
     */
    public void setUpdatedTimeOnForm(String dateString) throws ParseException {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        if (dateString != null && !dateString.isEmpty()) {
            this.updatedTime = dateFormatter.parse(dateString);
        }
    }
    
    @Override
    public String toString() {
        return "OrderTrack{" +
               "id=" + id +
               ", status=" + status +
               ", updatedTime=" + updatedTime +
               ", notes='" + notes + '\'' +
               '}';
    }
}
