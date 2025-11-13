package com.onlineStoreCom.entity.order;

import com.onlineStoreCom.entity.category.Category;
import com.onlineStoreCom.entity.product.Product;
import com.onlineStoreCom.entity.setting.subsetting.IdBasedEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

/**
 * Represents a line item within an order, containing details about a specific product
 * being purchased, its quantity, pricing, and associated costs.
 *
 * <p>This entity is a critical part of the order management system, linking products
 * to orders and maintaining the specific details of each item in the order.
 *
 * @see Order
 * @see Product
 */
@Entity
@Table(name = "order_details")

public class OrderDetail extends IdBasedEntity {
    
    /** The quantity of the product ordered */
    private int quantity;
    
    /** The total cost of the product (unit price * quantity) */
    private float productCost;
    
    /** The shipping cost for this line item */
    private float shippingCost;
    
    /** The unit price of the product at the time of order */
    private float unitPrice;
    
    /** The subtotal for this line item (product cost + shipping cost) */
    private float subtotal;

    /**
     * The product associated with this order detail.
     * Many order details can be associated with one product.
     */
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * The order that this detail belongs to.
     * Many order details can belong to one order.
     */
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * Default constructor required by JPA.
     */
    public OrderDetail() {
    }

    /**
     * Constructs a new OrderDetail with the specified category name and cost information.
     * This constructor is typically used for creating order details with basic product information.
     *
     * @param categoryName the name of the product's category
     * @param quantity the quantity of the product ordered
     * @param productCost the cost per unit of the product
     * @param shippingCost the shipping cost for this item
     * @param subtotal the subtotal for this line item
     */
    public OrderDetail(String categoryName, int quantity, float productCost, float shippingCost, float subtotal) {
        this.product = new Product();
        this.product.setCategory(new Category(categoryName));
        this.quantity = quantity;
        this.productCost = productCost * quantity;
        this.shippingCost = shippingCost;
        this.subtotal = subtotal;
    }

    /**
     * Constructs a new OrderDetail with the specified product name and cost information.
     * This constructor is typically used for creating order details with product name.
     *
     * @param quantity the quantity of the product ordered
     * @param productName the name of the product
     * @param productCost the cost per unit of the product
     * @param shippingCost the shipping cost for this item
     * @param subtotal the subtotal for this line item
     */
    public OrderDetail(int quantity, String productName, float productCost, float shippingCost, float subtotal) {
        this.product = new Product(productName);
        this.quantity = quantity;
        this.productCost = productCost * quantity;
        this.shippingCost = shippingCost;
        this.subtotal = subtotal;
    }

    /**
     * Gets the quantity of the product ordered.
     *
     * @return the quantity of the product
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the product ordered.
     *
     * @param quantity the quantity to set
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the total product cost (unit price * quantity).
     *
     * @return the total product cost
     */
    public float getProductCost() {
        return productCost;
    }

    /**
     * Sets the total product cost.
     *
     * @param productCost the product cost to set
     */
    public void setProductCost(float productCost) {
        this.productCost = productCost;
    }

    /**
     * Gets the shipping cost for this line item.
     *
     * @return the shipping cost
     */
    public float getShippingCost() {
        return shippingCost;
    }

    /**
     * Sets the shipping cost for this line item.
     *
     * @param shippingCost the shipping cost to set
     */
    public void setShippingCost(float shippingCost) {
        this.shippingCost = shippingCost;
    }

    /**
     * Gets the unit price of the product at the time of order.
     *
     * @return the unit price
     */
    public float getUnitPrice() {
        return unitPrice;
    }

    /**
     * Sets the unit price of the product.
     *
     * @param unitPrice the unit price to set
     */
    public void setUnitPrice(float unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Gets the subtotal for this line item (product cost + shipping cost).
     *
     * @return the subtotal
     */
    public float getSubtotal() {
        return subtotal;
    }

    /**
     * Sets the subtotal for this line item.
     *
     * @param subtotal the subtotal to set
     */
    public void setSubtotal(float subtotal) {
        this.subtotal = subtotal;
    }

    /**
     * Gets the product associated with this order detail.
     *
     * @return the associated product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Sets the product associated with this order detail.
     *
     * @param product the product to associate
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * Gets the order that this detail belongs to.
     *
     * @return the parent order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the order that this detail belongs to.
     *
     * @param order the parent order to set
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
               "id=" + id +
               ", product=" + (product != null ? product.getName() : "null") +
               ", quantity=" + quantity +
               ", unitPrice=" + unitPrice +
               ", productCost=" + productCost +
               ", shippingCost=" + shippingCost +
               ", subtotal=" + subtotal +
               '}';
    }
}
