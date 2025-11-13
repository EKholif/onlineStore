package com.onlineStoreCom.entity.order;

/**
 * Defines the available payment methods for processing orders in the system.
 * <p>
 * This enum represents the various payment options that customers can use to complete
 * their purchases. Each payment method may have different processing requirements,
 * security considerations, and integration points with payment gateways.
 *
 * <p><b>Payment Method Details:</b>
 * <ul>
 *   <li>CREDIT_CARD - For credit card payments (Visa, Mastercard, etc.)</li>
 *   <li>PAYPAL - For PayPal online payments</li>
 *   <li>BANK_TRANSFER - For direct bank transfers</li>
 *   <li>COD - Cash on Delivery (payment upon receipt)</li>
 *   <li>CRYPTO - For cryptocurrency payments</li>
 *   <li>WALLET - For payments using the customer's wallet balance</li>
 * </ul>
 *
 * <p>This enum is used throughout the system to:
 * <ul>
 *   <li>Validate payment methods during checkout</li>
 *   <li>Route payments to the appropriate processing service</li>
 *   <li>Generate appropriate payment forms and UI elements</li>
 *   <li>Track payment-related analytics</li>
 * </ul>
 *
 * @see Order
 * @see PaymentProcessor
 */
public enum PaymentMethod  {
    /**
     * Payment via credit or debit card.
     * <p>
     * This payment method requires card details including card number,
     * expiration date, and CVV. All card transactions are processed
     * through a PCI-DSS compliant payment gateway.
     *
     * <p>Supported card types: Visa, Mastercard, American Express, Discover
     */
    CREDIT_CARD,

    /**
     * Payment via PayPal account.
     * <p>
     * Customers are redirected to PayPal's secure site to complete the payment.
     * No sensitive financial information is stored in our system.
     *
     * <p>Note: Requires PayPal business account integration.
     */
    PAYPAL,

    /**
     * Bank transfer payment.
     * <p>
     * Customers receive bank account details to complete the transfer.
     * Orders are processed only after the payment is confirmed in the bank statement.
     *
     * <p>Typical processing time: 1-3 business days
     */
    BANK_TRANSFER,

    /**
     * Cash on Delivery.
     * <p>
     * Payment is collected in person when the order is delivered.
     * Additional handling fees may apply.
     *
     * <p>Available only for specific regions and order values.
     * Requires signature upon delivery.
     */
    COD,

    /**
     * Cryptocurrency payment.
     * <p>
     * Supports various cryptocurrencies including Bitcoin, Ethereum, and others.
     * The exact amount in cryptocurrency is calculated at the time of transaction.
     *
     * <p>Transactions are processed through a secure crypto payment gateway.
     * All crypto payments are final and non-refundable.
     */
    CRYPTO,

    /**
     * Payment using customer's wallet balance.
     * <p>
     * Deducts the order amount from the customer's wallet.
     * If the wallet balance is insufficient, this method cannot be selected.
     *
     * <p>Customers can add funds to their wallet using other payment methods.
     */
    WALLET;

    /**
     * Gets the default payment method for new customers.
     *
     * @return the default payment method (CREDIT_CARD)
     */
    public static PaymentMethod getDefault() {
        return CREDIT_CARD;
    }

    /**
     * Checks if this payment method requires online processing.
     *
     * @return true if the payment method requires online processing (e.g., CREDIT_CARD, PAYPAL)
     */
    public boolean requiresOnlineProcessing() {
        return this != COD;
    }

    /**
     * Gets the display name of the payment method.
     *
     * @return a user-friendly name for the payment method
     */
    public String getDisplayName() {
        switch (this) {
            case CREDIT_CARD: return "Credit/Debit Card";
            case PAYPAL: return "PayPal";
            case BANK_TRANSFER: return "Bank Transfer";
            case COD: return "Cash on Delivery";
            case CRYPTO: return "Cryptocurrency";
            case WALLET: return "Wallet Balance";
            default: return this.name();
        }
    }
}
